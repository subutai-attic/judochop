package org.safehaus.chop.webapp.coordinator;


import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.usergrid.chop.api.Commit;
import org.apache.usergrid.chop.api.Constants;
import org.apache.usergrid.chop.api.Module;
import org.apache.usergrid.chop.api.ProviderParams;
import org.apache.usergrid.chop.api.store.amazon.InstanceValues;
import org.usergrid.chop.client.ssh.AsyncSsh;
import org.usergrid.chop.client.ssh.SSHCommands;
import org.safehaus.chop.spi.IpRuleManager;
import org.safehaus.chop.stack.CoordinatedStack;
import org.safehaus.chop.stack.ICoordinatedCluster;
import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.spi.InstanceManager;
import org.safehaus.chop.spi.LaunchResult;
import org.safehaus.chop.stack.Stack;
import org.safehaus.chop.stack.User;
import org.safehaus.chop.webapp.ChopUiFig;
import org.safehaus.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.chop.webapp.dao.ProviderParamsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Coordinates all chop runs in the server.
 */
@Singleton
public class StackCoordinator {

    private static final Logger LOG = LoggerFactory.getLogger( StackCoordinator.class );

    private Map<User, Set<CoordinatedStack>> activeStacksByUser = new HashMap<User, Set<CoordinatedStack>>();
    private Map<Commit, Set<CoordinatedStack>> activeStacksByCommit = new HashMap<Commit, Set<CoordinatedStack>>();
    private final Object lock = new Object();

    @Inject
    private ChopUiFig chopUiFig;

    @Inject
    private InstanceManager instanceManager;

    @Inject
    private IpRuleManager ipRuleManager;

    @Inject
    private ProviderParamsDao providerParamsDao;


    public CoordinatedStack setupStack( Stack stack, User user, Commit commit, Module module ) throws Exception {
        CoordinatedStack coordinatedStack;

        synchronized ( lock ) {
            coordinatedStack = getMatching( user, commit, module );

            if ( coordinatedStack != null ) {
                return coordinatedStack;
            }

            coordinatedStack = new CoordinatedStack( stack, user, commit, module );

            /*
             * File storage scheme:
             *
             * ${base_for_files}/${user}/${groupId}/${artifactId}/${version}/${commitId}/runner.jar
             */
            File runnerJar = new File( chopUiFig.getContextPath() );
            runnerJar = new File( runnerJar, user.getUsername() );
            runnerJar = new File( runnerJar, module.getGroupId() );
            runnerJar = new File( runnerJar, module.getArtifactId() );
            runnerJar = new File( runnerJar, module.getVersion() );
            runnerJar = new File( runnerJar, commit.getId() );
            runnerJar = new File( runnerJar, Constants.RUNNER_JAR );

            ipRuleManager.setDataCenter( stack.getDataCenter() );
            ipRuleManager.applyIpRuleSet( stack.getIpRuleSet() );

            ProviderParams providerParams = providerParamsDao.getByUser( user.getUsername() );

            for ( ICoordinatedCluster cluster : coordinatedStack.getClusters() ) {

                String keyFile = providerParams.getKeys().get( cluster.getInstanceSpec().getKeyName() );
                if( keyFile == null || ! ( new File( keyFile ) ).exists() ) {
                    // TODO should we clean up launched clusters?
                    throw new FileNotFoundException( "No key file found with the key name: " +
                            cluster.getInstanceSpec().getKeyName() + " and path: " + keyFile );
                }

                LaunchResult result = instanceManager.launchCluster(
                        coordinatedStack, cluster, 100000 );

                for ( Instance instance : result.getInstances() ) {
                    cluster.add( instance );
                }

                boolean success = executeSSHCommands( cluster, runnerJar, keyFile );
                if( ! success ) {
                    // TODO should we clean up launched clusters?
                    throw new RuntimeException( "SSH commands have failed, will not continue" );
                }
            }

            addStack( coordinatedStack );
            lock.notifyAll();
        }

        return coordinatedStack;
    }


    private void addStack( CoordinatedStack stack ) {
        Set<CoordinatedStack> stacks = activeStacksByCommit.get( stack.getCommit() );

        if ( stacks == null ) {
            stacks = new HashSet<CoordinatedStack>();
            activeStacksByCommit.put( stack.getCommit(), stacks );
        }
        stacks.add( stack );

        stacks = activeStacksByUser.get( stack.getUser() );
        if ( stacks == null ) {
            stacks = new HashSet<CoordinatedStack>();
            activeStacksByUser.put( stack.getUser(), stacks );
        }
        stacks.add( stack );
    }


    private CoordinatedStack getMatching( User user, Commit commit, Module module ) {
        if ( activeStacksByCommit.get( commit ) != null ) {
            Set<CoordinatedStack> stacks = activeStacksByCommit.get( commit );

            for ( CoordinatedStack existing : stacks ) {
                if ( existing.getUser().equals( user ) && existing.getModule().equals( module ) ) {
                    return existing;
                }
            }
        }

        return null;
    }


    private static boolean executeSSHCommands( ICoordinatedCluster cluster, File runnerJar, String keyFile )
            throws MalformedURLException {

        InstanceValues sshCommand;
        StringBuilder sb = new StringBuilder();
        String command;
        Collection<AsyncSsh<Instance>> executed = new LinkedList<AsyncSsh<Instance>>();

        for( Object obj: cluster.getInstanceSpec().getScriptEnvironment().keySet() ) {

            String envVar = obj.toString();
            String value = cluster.getInstanceSpec().getScriptEnvironment().getProperty( envVar );

            sb.append( "export " )
              .append( envVar )
              .append( "=" )
              .append( value )
              .append( ";" );
        }
        String exportVars = sb.toString();

        URLClassLoader classLoader = new URLClassLoader( new URL[] { runnerJar.toURL() },
                Thread.currentThread().getContextClassLoader() );

        for( URL scriptFile: cluster.getInstanceSpec().getSetupScripts() ) {
            /** First save file beside runner.jar */
            File file = new File( scriptFile.getPath() );
            File fileToSave = new File( runnerJar, file.getName() );
            UploadResource.writeToFile( classLoader.getResourceAsStream( file.getName() ), fileToSave.getPath() );

            try {
                /** SCP the script to instance **/
                sb = new StringBuilder();
                sb.append( "/home/" )
                  .append( SSHCommands.DEFAULT_USER )
                  .append( "/" )
                  .append( fileToSave.getName() );

                String destFile = sb.toString();
                sshCommand = new InstanceValues( fileToSave.getPath(), destFile, keyFile );
                executed.addAll( AsyncSsh.execute( cluster.getInstances(), sshCommand ) );

                /** calling chmod first just in case **/
                sb = new StringBuilder();
                sb.append( "chmod 0755 " )
                  .append( fileToSave.getPath() );
                sshCommand = new InstanceValues( sb.toString(), keyFile );
                executed.addAll( AsyncSsh.execute( cluster.getInstances(), sshCommand ) );

                /** Run the script command */
                sb = new StringBuilder();
                sb.append( exportVars )
                  .append( "sudo -E " )
                  .append( destFile );

                sshCommand = new InstanceValues( sb.toString(), keyFile );
                executed.addAll( AsyncSsh.execute( cluster.getInstances(), sshCommand ) );
            }
            catch ( InterruptedException e ) {
                LOG.error( "Interrupted while trying to execute SSH command", e );
                return false;
            }

        }

        return AsyncSsh.extractFailures( executed ).size() == 0;
    }
}
