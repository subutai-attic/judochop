package org.safehaus.chop.plugin;


import org.safehaus.chop.client.ResponseInfo;
import org.safehaus.chop.client.ssh.SSHCommands;

import com.amazonaws.services.ec2.model.Instance;


/**
* Created with IntelliJ IDEA. User: akarasulu Date: 1/18/14 Time: 5:32 AM To change this template use File | Settings |
* File Templates.
*/
class SSHRequestThread implements Runnable {


    private String instanceURL;

    private String sshKeyFile;

    private ResponseInfo result;

    private Instance instance;

    private Thread thread;


    public void start() {
        thread = new Thread( this );
        thread.start();
    }


    public void join( long timeout ) throws InterruptedException {
        thread.join( timeout );
    }


    public Instance getInstance() {
        return instance;
    }


    public void setInstance( Instance instance ) {
        this.instance = instance;
    }


    public void setInstanceURL( final String instanceURL ) {
        this.instanceURL = instanceURL;
    }


    public void setSshKeyFile( final String sshKeyFile ) {
        this.sshKeyFile = sshKeyFile;
    }


    public ResponseInfo getResult() {
        return result;
    }


    @Override
    public void run() {
        result = SSHCommands.restartTomcatOnInstance( sshKeyFile, instanceURL );
    }
}
