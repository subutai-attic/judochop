package org.safehaus.chop.stack;


public interface ICoordinatedCluster extends Cluster {
    Iterable<Instance> getInstances();

    boolean add( Instance instance );
}
