package org.safehaus.chop.webapp.coordinator;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.stack.CoordinatedStack;
import org.safehaus.chop.stack.ICoordinatedCluster;
import org.safehaus.chop.stack.Instance;
import org.safehaus.chop.spi.InstanceManager;
import org.safehaus.chop.spi.InstanceRegistry;
import org.safehaus.chop.spi.LaunchResult;
import org.safehaus.chop.stack.Stack;
import org.safehaus.chop.stack.User;

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
    private InstanceManager instanceManager;

    @Inject
    private InstanceRegistry instanceRegistry;


    public CoordinatedStack setupStack( Stack stack, User user, Commit commit, Module module ) {
        CoordinatedStack coordinatedStack;

        synchronized ( lock ) {
            coordinatedStack = getMatching( user, commit, module );

            if ( coordinatedStack != null ) {
                return coordinatedStack;
            }

            coordinatedStack = new CoordinatedStack( stack, user, commit, module );

            for ( ICoordinatedCluster cluster : coordinatedStack.getClusters() ) {
                LaunchResult result = instanceManager.launchCluster(
                        coordinatedStack, cluster, 100000 );
                instanceManager.getClusterInstances( coordinatedStack, cluster );

                for ( Instance instance : result.getInstances() ) {
                    cluster.add( instance );
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
}
