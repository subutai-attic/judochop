package org.apache.usergrid.chop.runner.drivers;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.safehaus.chop.api.Signal;


/** The driver States and its possible state transitions: hence its state machine. */
public enum State {
    // stopped ==> (reset signal) ==> ready
    COMPLETED( 3, new Signal[] {}, new Integer[] {} ),

    // stopped ==> (reset signal) ==> ready
    STOPPED( 2, new Signal[] {}, new Integer[] {} ),

    // running ==> (stop signal) ==> stopped
    // running ==> (completed signal) ==> ready
    RUNNING( 1, new Signal[] { Signal.STOP, Signal.COMPLETED }, new Integer[] { 2, 3 } ),

    // ready ==> (load signal) ==> ready
    // ready ==> (start signal) ==> running
    READY( 0, new Signal[] { Signal.START }, new Integer[] { 1 } );


    private final int id;
    private final Map<Signal, Integer> trantab;


    private State( int id, Signal[] signals, Integer[] states ) {
        this.id = id;
        trantab = getTrantab( signals, states );
    }


    public int getId() {
        return id;
    }


    public State get( Integer id ) {
        if ( id == null ) {
            return null;
        }

        switch ( id ) {
            case 0:
                return READY;
            case 1:
                return RUNNING;
            case 2:
                return STOPPED;
            case 3:
                return COMPLETED;
        }

        throw new RuntimeException( "Should never get here!" );
    }


    public State next( Signal signal ) {
        return get( trantab.get( signal ) );
    }


    private static Map<Signal, Integer> getTrantab( Signal[] signals, Integer[] states ) {
        Map<Signal, Integer> trantab = new HashMap<Signal, Integer>( signals.length );

        for ( int ii = 0; ii < signals.length; ii++ ) {
            trantab.put( signals[ii], states[ii] );
        }

        return Collections.unmodifiableMap( trantab );
    }
}
