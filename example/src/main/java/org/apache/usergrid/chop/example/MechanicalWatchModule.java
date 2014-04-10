/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/6/13
 * Time: 1:10 AM
 */
package org.apache.usergrid.chop.example;


import com.google.inject.AbstractModule;


/**
 * A Guice {@link com.google.inject.Module} for mechanical watches.
 */
public class MechanicalWatchModule extends AbstractModule {
    protected void configure() {
        bind( PowerSource.class ).to( Mainspring.class );
        bind( Watch.class ).to( MechanicalWatch.class );
    }
}
