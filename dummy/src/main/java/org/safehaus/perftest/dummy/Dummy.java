package org.safehaus.perftest.dummy;


import org.safehaus.chop.api.annotations.TimeChop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/6/13 Time: 1:08 AM To change this template use File | Settings |
 * File Templates.
 */
@TimeChop( time = 1000, delay = 0, threads = 10 )
public class Dummy {
    private static final Logger LOG = LoggerFactory.getLogger( Dummy.class );

    public void foobar()
    {
        LOG.debug( "foobar" );
    }
}
