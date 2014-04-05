package org.safehaus.chop.api.store.amazon;


import org.safehaus.chop.api.SshValues;
import org.safehaus.chop.stack.Instance;

import com.google.common.base.Preconditions;


/**
 * A simple values holder for Amazon Instance based associations.
 */
public class InstanceValues implements SshValues<Instance> {

    private boolean scpCommand;
    private String command;
    private String sshKeyFile;
    private String srcFilePath;
    private String dstFilePath;


    public InstanceValues( final String command, final String sshKeyFile ) {
        Preconditions.checkNotNull( command, "The 'command' parameter cannot be null." );
        Preconditions.checkNotNull( sshKeyFile, "The 'sshKeyFile' parameter cannot be null." );

        this.command = command;
        this.sshKeyFile = sshKeyFile;

        scpCommand = false;
    }


    public InstanceValues( final String srcFilePath, final String dstFilePath, final String sshKeyFile ) {
        Preconditions.checkNotNull( srcFilePath, "The 'srcFilePath' parameter cannot be null." );
        Preconditions.checkNotNull( dstFilePath, "The 'dstFilePath' parameter cannot be null." );
        Preconditions.checkNotNull( sshKeyFile, "The 'sshKeyFile' parameter cannot be null." );

        this.srcFilePath = srcFilePath;
        this.dstFilePath = dstFilePath;
        this.sshKeyFile = sshKeyFile;

        scpCommand = true;
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


    @Override
    public String getSourceFile( final Instance associate ) {
        return srcFilePath;
    }


    @Override
    public String getDestinationFile( final Instance associate ) {
        return dstFilePath;
    }


    @Override
    public boolean isScpCommand( final Instance associate ) {
        return scpCommand;
    }
}

