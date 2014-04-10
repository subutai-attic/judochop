package org.apache.usergrid.chop.stack;


import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.safehaus.chop.api.Signal;
import org.safehaus.chop.api.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


/**
 *
 */
public enum  StackState {
    // inactive ==> (load signal) ==> ready
    INACTIVE( 3, new Signal[] { Signal.LOAD }, new Integer[] { 0 } ),

    // stopped ==> (reset signal) ==> ready
    STOPPED( 2, new Signal[] { Signal.RESET }, new Integer[] { 0 } ),

    // running ==> (stop signal) ==> stopped
    // running ==> (completed signal) ==> ready
    RUNNING( 1, new Signal[] { Signal.STOP, Signal.COMPLETED }, new Integer[] { 2, 0 } ),

    // ready ==> (load signal) ==> ready
    // ready ==> (start signal) ==> running
    READY( 0, new Signal[] { Signal.LOAD, Signal.START }, new Integer[] { 0, 1 } );

    private static final Logger LOG = LoggerFactory.getLogger( State.class );

    private final int id;
    private final Map<Signal, Integer> trantab;
    private final Set<Signal> accepts;


    private StackState( int id, Signal[] signals, Integer[] states ) {
        this.id = id;
        trantab = getTrantab( signals, states );
        accepts = new HashSet<Signal>( signals.length );
        Collections.addAll( accepts, signals );
    }


    public int getId() {
        return id;
    }


    /**
     * Check to see if the state accepts a signal: meaning is the signal a
     * valid signal to produce a state transition.
     *
     * @param signal the signal to check
     * @return true if the signal will be accepted, false otherwise
     */
    public boolean accepts( Signal signal ) {
        Preconditions.checkNotNull( signal, "Signal parameter cannot be null: state = {}", toString() );
        return accepts.contains( signal );
    }


    /**
     * Check to see if the state accepts a signal: in other words is the signal a
     * valid signal to produce a state transition and does that transition lead
     * to the supplied 'next' state parameter.
     *
     * @param signal the signal to check
     * @param next the next state to transit to
     * @return true if the signal will be accepted and the next state will be the
     * supplied state, false otherwise
     */
    public boolean accepts( Signal signal, State next ) {
        if ( signal == null || next == null ) {
            return false;
        }

        if ( ! accepts.contains( signal ) ) {
            return false;
        }

        Integer id = trantab.get( signal );
        if ( id == null ) {
            return false;
        }

        StackState realNext = get( id );

        if ( realNext == null ) {
            return false;
        }

        return realNext.equals( next );
    }


    public StackState get( Integer id ) {
        Preconditions.checkNotNull( id, "The id cannot be null: state = {}", toString() );

        switch ( id ) {
            case 0:
                return READY;
            case 1:
                return RUNNING;
            case 2:
                return STOPPED;
            case 3:
                return INACTIVE;
        }

        throw new RuntimeException( "Should never get here!" );
    }


    public StackState next( Signal signal ) {
        Preconditions.checkNotNull( signal, "The signal cannot be null: state = {}", toString() );
        Integer id = trantab.get( signal );

        LOG.info( "Got signal {} in {} state: id = " + id, signal, toString() );

        return get( id );
    }


    private static Map<Signal, Integer> getTrantab( Signal[] signals, Integer[] states ) {
        Map<Signal, Integer> trantab = new HashMap<Signal, Integer>( signals.length );

        for ( int ii = 0; ii < signals.length; ii++ ) {
            trantab.put( signals[ii], states[ii] );
        }

        return Collections.unmodifiableMap( trantab );
    }
}
