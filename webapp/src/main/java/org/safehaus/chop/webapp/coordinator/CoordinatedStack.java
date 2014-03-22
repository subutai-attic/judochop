package org.safehaus.chop.webapp.coordinator;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.safehaus.chop.api.Commit;
import org.safehaus.chop.api.Module;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.stack.Cluster;
import org.safehaus.chop.stack.Stack;
import org.safehaus.chop.webapp.dao.model.User;


/**
 * A Stack implementation used to decorate a standard Stack with runtime
 * information used by the coordinator to control and manage it.
 */
public class CoordinatedStack implements Stack {

    private final Stack delegate;
    private final List<CoordinatedCluster> clusters;
    private final Commit commit;
    private final Module module;
    private final User user;

    private StackState state = StackState.INACTIVE;
    private Set<Runner> runners;


    CoordinatedStack( Stack delegate, User user, Commit commit, Module module ) {
        this.delegate = delegate;
        this.clusters = new ArrayList<CoordinatedCluster>( delegate.getClusters().size() );
        this.user = user;
        this.commit = commit;
        this.module = module;

        for ( Cluster cluster : delegate.getClusters() ) {
            this.clusters.add( new CoordinatedCluster( cluster ) );
        }
    }


    @Override
    public String getName() {
        return delegate.getName();
    }


    @Override
    public UUID getId() {
        return delegate.getId();
    }


    @Override
    public List<? extends CoordinatedCluster> getClusters() {
        return clusters;
    }


    public Commit getCommit() {
        return commit;
    }


    public Module getModule() {
        return module;
    }


    public User getUser() {
        return user;
    }


    public StackState getState() {
        return state;
    }


    public Iterable<Runner> getRunners() {
        return runners;
    }
}
