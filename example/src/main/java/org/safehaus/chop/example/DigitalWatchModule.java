/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/6/13
 * Time: 1:10 AM
 */
package org.safehaus.chop.example;


import com.google.inject.AbstractModule;


public class DigitalWatchModule extends AbstractModule {
    protected void configure() {
        bind( Battery.class );
        bind( Watch.class ).to( DigitalWatch.class );
    }
}
