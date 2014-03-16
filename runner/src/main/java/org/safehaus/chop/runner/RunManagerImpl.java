package org.safehaus.chop.runner;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import org.safehaus.chop.api.*;
import org.safehaus.chop.api.Summary;
import org.safehaus.chop.spi.RunManager;
import org.safehaus.embedded.jetty.utils.CertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;


/**
 * An implementation of the RunManager that works with the Coordinator service on the web ui.
 */
public class RunManagerImpl implements RunManager {
    private static final Logger LOG = LoggerFactory.getLogger( RunManagerImpl.class );

    private CoordinatorFig coordinatorFig;
    private URL endpoint;

    @Inject
    private Runner me;


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
    public void store( final Project project, final Summary summary, final File resultsFile,
                       final Class<?> testClass ) throws FileNotFoundException, MalformedURLException {
        Preconditions.checkNotNull( summary, "The summary argument cannot be null." );

        // post the summary information
        WebResource resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, me );
        String result = resource.path( coordinatorFig.getUploadSummaryPath() )
                                .queryParam( "testClass", testClass.getName() )
                                .type( MediaType.APPLICATION_JSON ).post( String.class, summary );

        LOG.debug( "Got back result from summary post = {}", result );

        // upload the results file
        InputStream in = new FileInputStream( resultsFile );
        FormDataMultiPart part = new FormDataMultiPart();
        part.field( "file", resultsFile.getName() );

        FormDataBodyPart body = new FormDataBodyPart( "content", in, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        part.bodyPart( body );

        resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, me );
        result = resource.path( coordinatorFig.getUploadResultsPath() )
                         .queryParam( "testClass", testClass.getName() )
                         .type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, part );

        LOG.debug( "Got back result from results file upload = {}", result );
    }


    @Override
    public boolean hasCompleted( final Runner runner, final Project project, final int runNumber,
                                 final Class<?> testClass ) {
        // get run status information
        WebResource resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, runner );
        String result = resource.path( coordinatorFig.getRunStatusPath() )
                                  .queryParam( "runNumber", String.valueOf( runNumber ) )
                                  .queryParam( "testClass", testClass.getName() )
                                  .type( MediaType.TEXT_PLAIN ).get( String.class );

        LOG.debug( "Got back result from run status get = {}", result );

        return Boolean.parseBoolean( result );
    }


    @Override
    public int getNextRunNumber( final Project project ) {
        // get run status information
        WebResource resource = Client.create().resource( coordinatorFig.getEndpoint() );
        resource = addQueryParameters( resource, project, me );
        String result = resource.path( coordinatorFig.getRunStatusPath() )
                                  .type( MediaType.TEXT_PLAIN ).get( String.class );

        LOG.debug( "Got back result from next run number get = {}", result );

        return Integer.parseInt( result );
    }
}