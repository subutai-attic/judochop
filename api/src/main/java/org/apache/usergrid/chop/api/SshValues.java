package org.apache.usergrid.chop.api;


public interface SshValues<A> {
    public String getHostname( A associate );
    public String getCommand( A associate );
    public String getSshKeyFile( A associate );
    public String getSourceFile( A associate );
    public String getDestinationFile( A associate );
    public boolean isScpCommand( A associate );
}