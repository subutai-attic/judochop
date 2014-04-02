package org.safehaus.chop.webapp.coordinator.rest;


import org.safehaus.jettyjam.utils.TestMode;


/**
 * A base class for all signal resources.
 */
public abstract class TestableResource {
    public static final String TEST_PARAM = TestMode.TEST_MODE_PROPERTY;

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
