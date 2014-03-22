package org.safehaus.chop.stack;


/**
 * The states of an instance.
 */
public enum InstanceState {

    Pending("pending"),
    Running("running"),
    ShuttingDown("shutting-down"),
    Terminated("terminated"),
    Stopping("stopping"),
    Stopped("stopped");

    private String value;

    private InstanceState( String value ) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    /**
     * Use this in place of valueOf.
     *
     * @param value
     *            real value
     * @return InstanceState corresponding to the value
     */
    public static InstanceState fromValue( String value ) {
        if ( value == null || "".equals( value ) ) {
            throw new IllegalArgumentException( "Value cannot be null or empty!" );

        } else if ( "pending".equals( value ) ) {
            return InstanceState.Pending;
        } else if ( "running".equals( value ) ) {
            return InstanceState.Running;
        } else if ( "shutting-down".equals( value ) ) {
            return InstanceState.ShuttingDown;
        } else if ( "terminated".equals( value ) ) {
            return InstanceState.Terminated;
        } else if ( "stopping".equals( value ) ) {
            return InstanceState.Stopping;
        } else if ( "stopped".equals( value ) ) {
            return InstanceState.Stopped;
        } else {
            throw new IllegalArgumentException( "Cannot create enum from " + value + " value!" );
        }
    }



}
