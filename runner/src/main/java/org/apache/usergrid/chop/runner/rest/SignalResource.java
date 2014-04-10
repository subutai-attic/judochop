package org.apache.usergrid.chop.runner.rest;


import javax.ws.rs.core.Response;

import org.apache.usergrid.chop.api.BaseResult;
import org.apache.usergrid.chop.api.Project;
import org.apache.usergrid.chop.api.Signal;
import org.apache.usergrid.chop.api.State;
import org.apache.usergrid.chop.runner.IController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A base class for all signal resources.
 */
public abstract class SignalResource extends TestableResource {
    private static final Logger LOG = LoggerFactory.getLogger( SignalResource.class );

    private final Project project;
    private final IController controller;
    private final String endpoint;
    private final Signal signal;


    protected SignalResource( IController controller, Project project, String endpoint, Signal signal ) {
        super( endpoint );
        this.endpoint = endpoint;
        this.project = project;
        this.controller = controller;
        this.signal = signal;
    }


    public Response op( boolean inTestMode ) {
        State state = controller.getState();
        BaseResult result = new BaseResult();
        result.setState( state );
        result.setMessage( state.getMessage( signal ) );
        result.setProject( project );
        result.setEndpoint( endpoint );

        if ( inTestMode ) {
            result.setStatus( true );
            result.setMessage( getTestMessage() );
            LOG.info( getTestMessage() );
            return Response.ok( result ).build();
        }

        if ( state.accepts( signal ) ) {
            controller.send( signal );
            result.setState( controller.getState() );
            result.setStatus( true );
            LOG.info( result.getMessage() );
            return Response.ok( result ).build();
        }

        result.setStatus( false );
        LOG.warn( result.getMessage() ); // ==> got message from state
        return Response.status( Response.Status.CONFLICT ).entity( result ).build();
    }
}
