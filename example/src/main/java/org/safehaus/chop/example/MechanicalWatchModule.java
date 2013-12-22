/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/6/13
 * Time: 1:10 AM
 */
package org.safehaus.chop.example;


import com.google.inject.AbstractModule;


public class MechanicalWatchModule extends AbstractModule {
    protected void configure() {
        bind( Mainspring.class );
        bind( Watch.class ).to( MechanicalWatch.class );
    }
}
