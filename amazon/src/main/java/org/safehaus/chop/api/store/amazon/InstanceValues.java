package org.safehaus.chop.api.store.amazon;


import org.safehaus.chop.api.SshValues;

import com.amazonaws.services.ec2.model.Instance;
import com.google.common.base.Preconditions;


/**
 * A simple values holder for Amazon Instance based associations.
 */
public class InstanceValues implements SshValues<Instance> {
    private final String command;
    private final String sshKeyFile;


    public InstanceValues( final String command, final String sshKeyFile ) {
        Preconditions.checkNotNull( command, "The 'command' parameter cannot be null." );
        Preconditions.checkNotNull( sshKeyFile, "The 'sshKeyFile' parameter cannot be null." );

        this.command = command;
        this.sshKeyFile = sshKeyFile;
    }


    @Override
    public String getHostname( final Instance associate ) {
        return associate.getPublicDnsName();
    }


    @Override
    public String getCommand( final Instance associate ) {
        return command;
    }


    @Override
    public String getSshKeyFile( final Instance associate ) {
        return sshKeyFile;
    }
}
