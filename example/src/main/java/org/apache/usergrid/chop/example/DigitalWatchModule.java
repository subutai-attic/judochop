/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/6/13
 * Time: 1:10 AM
 */
package org.apache.usergrid.chop.example;


import com.google.inject.AbstractModule;


/**
 * Guice {@link com.google.inject.Module} for wiring digital watches.
 */
public class DigitalWatchModule extends AbstractModule {
    protected void configure() {
        bind( PowerSource.class ).to( RechargeableBattery.class );
        bind( Watch.class ).to( DigitalWatch.class );
    }
}
