package org.safehaus.chop.stack;


/**
 * A basic Cluster implementation.
 */
public class BasicCluster implements Cluster {
    private String name;
    private InstanceSpec instanceSpec = new BasicInstanceSpec();
    private int size;


    @Override
    public String getName() {
        return name;
    }


    public BasicCluster setName( final String name ) {
        this.name = name;
        return this;
    }


    @Override
    public InstanceSpec getInstanceSpec() {
        return instanceSpec;
    }


    public BasicCluster setInstanceSpec( final InstanceSpec instanceSpec ) {
        this.instanceSpec = instanceSpec;
        return this;
    }


    @Override
    public int getSize() {
        return size;
    }


    public BasicCluster setSize( final int size ) {
        this.size = size;
        return this;
    }
}
