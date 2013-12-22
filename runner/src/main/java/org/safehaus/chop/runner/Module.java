/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 11/22/13
 * Time: 11:44 PM
 */
package org.safehaus.chop.runner;


import java.util.HashMap;
import java.util.Map;

import org.safehaus.archaius.DynamicPropertyNames;
import org.safehaus.chop.api.ApiModule;
import org.safehaus.chop.client.PerftestClientModule;
import org.safehaus.chop.runner.rest.ResetResource;
import org.safehaus.chop.runner.rest.StartResource;
import org.safehaus.chop.runner.rest.StatsResource;
import org.safehaus.chop.runner.rest.StatusResource;
import org.safehaus.chop.runner.rest.StopResource;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


public class Module extends ServletModule {
    public static final String DEFAULT_TEST_STOP_TIMEOUT = "1000L";
    public static final String DEFAULT_TEST_PACKAGE_BASE = "org.safehaus.chop.runner.example";
    public static final String PACKAGES_KEY = "com.sun.jersey.config.property.packages";


    protected void configureServlets() {
        install( new ApiModule() );
        install( new PerftestClientModule() );

        // Hook Jersey into Guice Servlet
        bind( GuiceContainer.class );

        // Hook Jackson into Jersey as the POJO <-> JSON mapper
        bind( JacksonJsonProvider.class ).asEagerSingleton();

        DynamicPropertyNames names = new DynamicPropertyNames();
        names.bindProperty( binder(), ConfigKeys.TEST_STOP_TIMEOUT, DEFAULT_TEST_STOP_TIMEOUT );
        names.bindProperty( binder(), ConfigKeys.TEST_PACKAGE_BASE, DEFAULT_TEST_PACKAGE_BASE );
        bind( IController.class ).to( Controller.class ).asEagerSingleton();

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
