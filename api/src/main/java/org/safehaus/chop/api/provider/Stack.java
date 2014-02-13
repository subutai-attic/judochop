package org.safehaus.chop.api.provider;


import java.util.Collection;


/**
 * A Judo Chop test stack.
 */
public interface Stack {
    String getName();

    String getId();

    Collection<Cluster> getClusters();
}
