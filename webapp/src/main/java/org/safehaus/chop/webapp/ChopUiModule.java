/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 11/22/13
 * Time: 11:44 PM
 */
package org.safehaus.chop.webapp;

import java.util.HashMap;
import java.util.Map;

import org.safehaus.chop.webapp.rest.TestGetResource;
import org.safehaus.chop.webapp.rest.UploadResource;
import org.safehaus.chop.webapp.rest.RestFig;
import org.safehaus.chop.webapp.view.util.VaadinServlet;
import org.safehaus.guicyfig.GuicyFigModule;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.servlet.ServletModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;


public class ChopUiModule extends JerseyServletModule {
    public static final String PACKAGES_KEY = "com.sun.jersey.config.property.packages";

    protected void configureServlets() {
        install( new GuicyFigModule( ChopUiFig.class, RestFig.class ) );
//        install( new ChopClientModule() );

        // Hook Jersey into Guice Servlet
        bind( GuiceContainer.class );

        // Hook Jackson into Jersey as the POJO <-> JSON mapper
        bind( JacksonJsonProvider.class ).asEagerSingleton();

        bind( UploadResource.class ).asEagerSingleton();
        bind( TestGetResource.class ).asEagerSingleton();

        // This should be before "/*" otherwise the vaadin servlet will not work
        serve( "/VAADIN/*" ).with( VaadinServlet.class );

        Map<String, String> params = new HashMap<String, String>();
        params.put( PACKAGES_KEY, getClass().getPackage().toString() );
        serve( "/*" ).with( GuiceContainer.class, params );
    }
}
