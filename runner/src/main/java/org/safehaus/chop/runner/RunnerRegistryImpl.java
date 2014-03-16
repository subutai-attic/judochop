package org.safehaus.chop.runner;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.RunnerRegistry;
import org.safehaus.embedded.jetty.utils.CertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;


/**
 * An implementation of the RunnerRegistry SPI interface to hit coordinator services.
 */
@Singleton
public class RunnerRegistryImpl implements RunnerRegistry {
    private static final Logger LOG = LoggerFactory.getLogger( RunnerRegistryImpl.class );

    private CoordinatorFig coordinatorFig;
    private URL endpoint;

    @Inject
    private Runner me;

    @Inject
    private Project project;


    @Inject
    private void setCoordinatorConfig( CoordinatorFig coordinatorFig ) {
        this.coordinatorFig = coordinatorFig;

        try {
            endpoint = new URL( coordinatorFig.getEndpoint() );
        }
        catch ( MalformedURLException e ) {
            LOG.error( "Failed to parse URL for coordinator", e );
        }

        // Need to get the configuration information for the coordinator
        if ( ! CertUtils.isTrusted( endpoint.getHost() ) ) {
            CertUtils.preparations( endpoint.getHost(), endpoint.getPort() );
        }
        Preconditions.checkState( CertUtils.isTrusted( endpoint.getHost() ), "coordinator must be trusted" );
    }


    private WebResource addQueryParameters( WebResource resource, Project project, Runner runner ) {
        return resource.queryParam( "runnerHostname", runner.getHostname() )
                .queryParam( "runnerPort", String.valueOf( runner.getServerPort() ) )
                .queryParam( "runnerIpv4Address", runner.getIpv4Address() )
                .queryParam( "moduleGroupId", project.getGroupId() )
                .queryParam( "moduleArtifactId", project.getArtifactId() )
                .queryParam( "moduleVersion", project.getVersion() )
                .queryParam( "commitId", project.getVcsVersion() )
                .queryParam( "username", coordinatorFig.getUsername() )
                .queryParam( "password", coordinatorFig.getPassword() );
    }


    @Override
    public List<Runner> getRunners() {
        // get a list of all runners associated with this project
        WebResource resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, me );
        List<Runner> runners = resource.path( coordinatorFig.getRunnersListPath() )
                                  .type( MediaType.APPLICATION_JSON ).get( new GenericType<List<Runner>>() {} );

        LOG.debug( "Got back runners list = {}", runners );

        return runners;
    }


    @Override
    public List<Runner> getRunners( final Runner runner ) {
        List<Runner> runners = getRunners();

        for ( int ii = 0; ii < runners.size(); ii++ ) {
            if ( runners.get( ii ).getHostname().equals( runner.getHostname() ) ) {
                runners.remove( ii );
            }
        }

        return runners;
    }


    @Override
    public void register( final Runner runner ) {
        WebResource resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, runner );
        String result = resource.path( coordinatorFig.getRunnersRegisterPath() )
                                .type( MediaType.TEXT_PLAIN ).post( String.class );

        LOG.debug( "Got back results from register post = {}", result );
    }


    @Override
    public void unregister( final Runner runner ) {
        WebResource resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, runner );
        String result = resource.path( coordinatorFig.getRunnersUnregisterPath() )
                                .type( MediaType.TEXT_PLAIN ).post( String.class );

        LOG.debug( "Got back results from unregister post = {}", result );
    }
}