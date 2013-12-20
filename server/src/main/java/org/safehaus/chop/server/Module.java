/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 11/22/13
 * Time: 11:44 PM
 */
package org.safehaus.chop.server;


import java.util.HashMap;
import java.util.Map;

import org.safehaus.chop.server.runners.Stats;
import org.safehaus.chop.api.PerftestApiModule;
import org.safehaus.perftest.client.PerftestClientModule;
import org.safehaus.chop.server.rest.ResetResource;
import org.safehaus.chop.server.rest.StartResource;
import org.safehaus.chop.server.rest.StatsResource;
import org.safehaus.chop.server.rest.StatusResource;
import org.safehaus.chop.server.rest.StopResource;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


public class Module extends ServletModule {
    public static final String PACKAGES_KEY = "com.sun.jersey.config.property.packages";


    protected void configureServlets() {
        install( new PerftestApiModule() );
        install( new PerftestClientModule() );

        // Hook Jersey into Guice Servlet
        bind( GuiceContainer.class );

        // Hook Jackson into Jersey as the POJO <-> JSON mapper
        bind( JacksonJsonProvider.class ).asEagerSingleton();

        bind( IResultsLog.class ).to( ResultsLog.class );

        bind( Stats.class );

        bind( Controller.class );

        bind( ResetResource.class ).asEagerSingleton();
        bind( StopResource.class ).asEagerSingleton();
        bind( StartResource.class ).asEagerSingleton();
        bind( StatsResource.class ).asEagerSingleton();
        bind( StatusResource.class ).asEagerSingleton();

        Map<String, String> params = new HashMap<String, String>();
        params.put( PACKAGES_KEY, getClass().getPackage().toString() );
        serve( "/*" ).with( GuiceContainer.class, params );
    }
}
