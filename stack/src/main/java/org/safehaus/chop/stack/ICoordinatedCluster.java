package org.safehaus.chop.stack;


import java.util.Collection;


public interface ICoordinatedCluster extends Cluster {
    Collection<Instance> getInstances();

    boolean add( Instance instance );
}
