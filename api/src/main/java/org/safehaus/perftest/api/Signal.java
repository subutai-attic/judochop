package org.safehaus.perftest.api;


/** The state dependent and/or impacting signals sent to a runner. */
public enum Signal {
    START( 0 ), STOP( 1 ), RESET( 2 ), LOAD( 3 ), COMPLETED( 4 );

    private final int id;


    private Signal( int id ) {
        this.id = id;
    }


    public int getId() {
        return id;
    }


    public Signal get( int id ) {
        switch ( id ) {
            case 0:
                return START;
            case 1:
                return STOP;
            case 2:
                return RESET;
            case 3:
                return LOAD;
            case 4:
                return COMPLETED;
        }

        throw new RuntimeException( "Should never get here!" );
    }
}
