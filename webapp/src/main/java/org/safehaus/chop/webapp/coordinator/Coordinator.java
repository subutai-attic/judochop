package org.safehaus.chop.webapp.coordinator;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.store.amazon.InstanceValues;
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

import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Coordinates all chop runs in the server.
 */
@Singleton
public class Coordinator {

    private Map<User, Set<CoordinatedStack>> activeStacksByUser = new HashMap<User, Set<CoordinatedStack>>();
    private Map<Commit, Set<CoordinatedStack>> activeStacksByCommit = new HashMap<Commit, Set<CoordinatedStack>>();
    private final Object lock = new Object();

    @Inject
    private ChopUiFig chopUiFig;

    @Inject
    private InstanceManager instanceManager;

    @Inject
    private IpRuleManager ipRuleManager;


    public CoordinatedStack setupStack( Stack stack, User user, Commit commit, Module module ) throws Exception {
        CoordinatedStack coordinatedStack;

        synchronized ( lock ) {
            coordinatedStack = getMatching( user, commit, module );

            if ( coordinatedStack != null ) {
                return coordinatedStack;
            }

            coordinatedStack = new CoordinatedStack( stack, user, commit, module );

            File runnerJar = new File( chopUiFig.getContextPath() );
            runnerJar = new File( runnerJar, user.getUsername() );
            runnerJar = new File( runnerJar, module.getGroupId() );
            runnerJar = new File( runnerJar, module.getArtifactId() );
            runnerJar = new File( runnerJar, module.getVersion() );
            runnerJar = new File( runnerJar, commit.getId() );
            runnerJar = new File( runnerJar, "runner.jar" ); // TODO fig this?

            ipRuleManager.setDataCenter( stack.getDataCenter() );
            ipRuleManager.applyIpRuleSet( stack.getIpRuleSet() );

            Collection<InstanceValues> sshCommands = new LinkedList<InstanceValues>();

            for ( ICoordinatedCluster cluster : coordinatedStack.getClusters() ) {
                LaunchResult result = instanceManager.launchCluster(
                        coordinatedStack, cluster, 100000 );

                for ( Instance instance : result.getInstances() ) {
                    cluster.add( instance );
                }
                sshCommands.addAll( getSSHCommands( cluster, runnerJar ) );
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


    private static Collection<InstanceValues> getSSHCommands( ICoordinatedCluster cluster, File runnerJar )
            throws MalformedURLException {
        Collection<InstanceValues> commandList = new LinkedList<InstanceValues>();
        StringBuilder sb;
        String command;

        for( Object obj: cluster.getInstanceSpec().getScriptEnvironment().keySet() ) {
            String envVar = obj.toString();
            String value = cluster.getInstanceSpec().getScriptEnvironment().getProperty( envVar );

            sb = new StringBuilder();
            command = sb.append( "export " ).append( envVar ).append( "=" ).append( value ).toString();

            commandList.add( new InstanceValues( command, " " ) ); // TODO ssh key file ?
        }

        URLClassLoader classLoader = new URLClassLoader( new URL[] { runnerJar.toURL() },
                Thread.currentThread().getContextClassLoader() );

        for( URL scriptFile: cluster.getInstanceSpec().getSetupScripts() ) {
            /** First save file beside runner.jar */
            File file = new File( scriptFile.getPath() );
            File fileToSave = new File( runnerJar, file.getName() );
            UploadResource.writeToFile( classLoader.getResourceAsStream( file.getName() ), fileToSave.getPath() );

            for( Instance instance: cluster.getInstances() ) {
                /** SCP the script to instance **/
                sb = new StringBuilder();
                command = sb.append( "scp " ) // TODO should we fig these parameters?
                            .append( fileToSave.getPath() )
                            .append( " ubuntu@" )
                            .append( instance.getPublicIpAddress() )
                            .append( ":/home/ubuntu/" )
                            .toString();

                commandList.add( new InstanceValues( command, " " ) ); // TODO ssh key file

                /** Run the script command */
                sb = new StringBuilder();
                command = sb.append( "sudo ./home/ubuntu/" )
                            .append( fileToSave.getName() )
                            .toString();

                commandList.add( new InstanceValues( command, " " ) ); // TODO ssh key file
            }
        }

        return commandList;
    }
}
