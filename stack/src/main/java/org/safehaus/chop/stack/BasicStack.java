package org.safehaus.chop.stack;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * A basic Stack implementation.
 */
public class BasicStack implements Stack {
    private String name;
    private UUID id = UUID.randomUUID();
    private List<Cluster> clusters = new ArrayList<Cluster>();


    @Override
    public String getName() {
        return name;
    }


    public BasicStack setName( final String name ) {
        this.name = name;
        return this;
    }


    @Override
    public UUID getId() {
        return id;
    }


    public BasicStack setId( UUID id ) {
        this.id = id;
        return this;
    }


    @Override
    public List<? extends Cluster> getClusters() {
        return clusters;
    }


    public BasicStack setClusters( List<Cluster> clusters ) {
        this.clusters = clusters;
        return this;
    }


    public BasicStack add( Cluster cluster ) {
        clusters.add( cluster );
        return this;
    }
}
