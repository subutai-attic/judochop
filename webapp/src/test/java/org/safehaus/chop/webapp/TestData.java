package org.safehaus.chop.webapp;


import org.safehaus.jettyjam.utils.JettyJarResource;
import org.safehaus.jettyjam.utils.JettyResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Contains the information needed to execute tests.
 */
public class TestData {
    private static final Logger LOG = LoggerFactory.getLogger( TestData.class );

    private Integer port;
    private String hostname;
    private String serverUrl;
    private String endpoint;
    private Logger logger;


    public TestData() {
    }


    public TestData( JettyResource jettyResource ) {
        setHostname( jettyResource.getHostname() )
                .setPort( jettyResource.getPort() )
                .setServerUrl( jettyResource.getServerUrl().toString() ).setLogger( LOG );
    }


    public TestData( JettyJarResource jarResource ) {
        setHostname( jarResource.getHostname() )
                .setPort( jarResource.getPort() )
                .setServerUrl( jarResource.getServerUrl().toString() ).setLogger( LOG );
    }


    public String getServerUrl() {
        return serverUrl;
    }


    public TestData setServerUrl( final String serverUrl ) {
        this.serverUrl = serverUrl;
        return this;
    }


    public String getEndpoint() {
        return endpoint;
    }


    public TestData setEndpoint( final String endpoint ) {
        this.endpoint = endpoint;
        return this;
    }


    public Logger getLogger() {
        return logger;
    }


    public TestData setLogger( final Logger logger ) {
        this.logger = logger;
        return this;
    }


    public String getHostname() {
        return hostname;
    }


    public TestData setHostname( final String hostname ) {
        this.hostname = hostname;
        return this;
    }


    public Integer getPort() {
        return port;
    }


    public TestData setPort( final Integer port ) {
        this.port = port;
        return this;
    }
}
