package org.safehaus.chop.runner.rest;


import javax.ws.rs.core.Response;

import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.State;
import org.safehaus.chop.runner.IController;
import org.safehaus.jettyjam.utils.TestMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A base class for all signal resources.
 */
public abstract class TestableResource {
    public static final String TEST_PARAM = TestMode.TEST_MODE_PROPERTY;
    private static final Logger LOG = LoggerFactory.getLogger( TestableResource.class );

    private final String endpoint;


    protected TestableResource( String endpoint ) {
        this.endpoint = endpoint;
    }


    public String getTestMessage() {
        return endpoint + " resource called in test mode.";
    }


    public boolean inTestMode( String testMode ) {
        return testMode != null &&
                ( testMode.equals( TestMode.INTEG.toString() ) || testMode.equals( TestMode.UNIT.toString() ) );
    }
}
