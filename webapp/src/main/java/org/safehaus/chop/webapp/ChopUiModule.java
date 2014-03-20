/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 11/22/13
 * Time: 11:44 PM
 */
package org.safehaus.chop.webapp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.guice.web.GuiceShiroFilter;

import org.safehaus.chop.api.store.amazon.AmazonStoreModule;
import org.safehaus.chop.webapp.coordinator.rest.TestGetResource;
import org.safehaus.chop.webapp.coordinator.rest.UploadResource;
import org.safehaus.chop.webapp.coordinator.rest.RestFig;
import org.safehaus.chop.webapp.elasticsearch.ElasticFig;
import org.safehaus.chop.webapp.elasticsearch.ElasticSearchClient;
import org.safehaus.chop.webapp.elasticsearch.IElasticSearchClient;
import org.safehaus.chop.webapp.view.util.VaadinServlet;
import org.safehaus.guicyfig.GuicyFigModule;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.servlet.ServletModule;
import com.netflix.config.ConfigurationManager;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


public class ChopUiModule extends ServletModule {
    public static final String PACKAGES_KEY = "com.sun.jersey.config.property.packages";

    static {
        try {
            ConfigurationManager.loadCascadedPropertiesFromResources( "chop-ui" );
        }
        catch ( IOException e ) {
            throw new RuntimeException( "Could not load configuration file", e );
        }
    }

    protected void configureServlets() {
        install( new GuicyFigModule( ChopUiFig.class, RestFig.class, ElasticFig.class ) );
        install( new AmazonStoreModule() );
//        install( new ChopClientModule() );

        // Hook Jersey into Guice Servlet
        bind( GuiceContainer.class );

        bind( IElasticSearchClient.class ).to( ElasticSearchClient.class );

        // Hook Jackson into Jersey as the POJO <-> JSON mapper
        bind( JacksonJsonProvider.class ).asEagerSingleton();

        bind( UploadResource.class ).asEagerSingleton();
        bind( TestGetResource.class ).asEagerSingleton();

        filter("/*").through(GuiceShiroFilter.class);

        // This should be before "/*" otherwise the vaadin servlet will not work
        serve( "/VAADIN/*" ).with( VaadinServlet.class );

        Map<String, String> params = new HashMap<String, String>();
        params.put( PACKAGES_KEY, getClass().getPackage().toString() );
        serve( "/*" ).with( GuiceContainer.class, params );
    }
}
