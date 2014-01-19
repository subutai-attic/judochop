package org.safehaus.chop.client.ssh;


/**
 * Extracts the hostname from an associate.
 */
public interface SshValues<A> {
    public String getHostname( A associate );
    public String getCommand( A associate );
    public String getSshKeyFile( A associate );
}
