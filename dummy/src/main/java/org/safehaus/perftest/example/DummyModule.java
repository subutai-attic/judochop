/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/6/13
 * Time: 1:10 AM
 */
package org.safehaus.perftest.example;


import org.safehaus.perftest.api.Perftest;
import org.safehaus.perftest.api.PerftestApiModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;


public class DummyModule extends AbstractModule {
    protected void configure() {
        install( new PerftestApiModule() );
        bind( Perftest.class ).to( DummyPerftest.class );
        bind( Module.class ).toInstance( this );
    }
}
