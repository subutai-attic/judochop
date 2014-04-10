/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 11/22/13
 * Time: 11:44 PM
 */
package org.apache.usergrid.chop.runner;


import java.util.HashMap;
import java.util.Map;

import org.apache.usergrid.chop.runner.rest.StatusResource;
import org.apache.usergrid.chop.runner.rest.ResetResource;
import org.apache.usergrid.chop.runner.rest.StatsResource;
import org.apache.usergrid.chop.runner.rest.StopResource;
import org.safehaus.chop.api.CoordinatorFig;
import org.safehaus.chop.client.ChopClientModule;
import org.apache.usergrid.chop.runner.rest.StartResource;
import org.apache.usergrid.chop.spi.RunManager;
import org.apache.usergrid.chop.spi.RunnerRegistry;
import org.safehaus.guicyfig.GuicyFigModule;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


public class Module extends ServletModule {
    public static final String PACKAGES_KEY = "com.sun.jersey.config.property.packages";


    protected void configureServlets() {
        //noinspection unchecked
        install( new GuicyFigModule( ServletFig.class, CoordinatorFig.class ) );
        install( new ChopClientModule() );

        // Hook Jersey into Guice Servlet
        bind( GuiceContainer.class );

        // Hook Jackson into Jersey as the POJO <-> JSON mapper
        bind( JacksonJsonProvider.class ).asEagerSingleton();

        bind( IController.class ).to( Controller.class );
        bind( RunnerRegistry.class ).to( RunnerRegistryImpl.class );
        bind( RunManager.class ).to( RunManagerImpl.class );

        bind( ResetResource.class );
        bind( StopResource.class );
        bind( StartResource.class );
        bind( StatsResource.class );
        bind( StatusResource.class );

        Map<String, String> params = new HashMap<String, String>();
        params.put( PACKAGES_KEY, getClass().getPackage().toString() );
        serve( "/*" ).with( GuiceContainer.class, params );
    }
}
