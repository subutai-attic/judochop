package org.safehaus.chop.webapp;


import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.ProviderParams;
import org.safehaus.chop.stack.Cluster;
import org.safehaus.chop.stack.InstanceSpec;
import org.safehaus.chop.stack.Stack;

import com.google.inject.Singleton;


/**
 * Coordinates all chop runs in the server.
 */
@Singleton
public class Coordinator {


    public void launchStack( ProviderParams params, Stack stack, Commit commit ) {

        for ( Cluster cluster : stack.getClusters() ) {
            launchCluster( cluster );
        }


    }


    public void launchCluster( Cluster cluster ) {
    }


    public void launchInstance( InstanceSpec spec ) {
    }
}
