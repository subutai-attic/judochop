package org.safehaus.chop.client.ssh;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class SSHCommands {

    private static final Logger LOG = LoggerFactory.getLogger( SSHCommands.class );

    public static ResponseInfo restartTomcatOnInstance ( String newPassword, String keyFile, String instanceURL ) {
        String command = "sudo sed -i 's/WtPgEXEwioa0LXUtqGwtkC4YUSgl2w4BF9VXsBviT/" + newPassword
                + "/g' /etc/tomcat7/tomcat-users.xml; sudo service tomcat7 restart";
        ResponseInfo response = sendCommandToInstance( command, keyFile, instanceURL );

        if ( ! response.isRequestSuccessful() || ! response.isOperationSuccessful() ) {
            return response;
        }

        response = sendCommandToInstance( "sudo service tomcat7 status", keyFile, instanceURL );

        if ( ! response.isRequestSuccessful() || ! response.isOperationSuccessful() ) {
            return response;
        }

        boolean succeeded = false;
        for ( String message : response.getMessages() ) {
            if ( message.contains( "Tomcat servlet engine is running with pid" ) ) {
                succeeded = true;
                break;
            }
        }

        return new ResponseInfo( instanceURL, true, succeeded, response.getMessages(), response.getErrorMessages() );
    }


    public static void blockForSshServer( String keyFile, String instanceUrl ) {
        while ( true ) {
            try {
                Collection<String> messages = new LinkedList<String>();
                Collection<String> errMessages = new LinkedList<String>();

                JSch ssh = new JSch();
                ssh.addIdentity( keyFile );
                Session session = ssh.getSession( "ubuntu", instanceUrl );
                session.setConfig( "StrictHostKeyChecking", "no" );
                session.connect();

                Channel channel = session.openChannel( "exec" );
                ( ( ChannelExec ) channel ).setCommand( "ls" );
                channel.connect();
                BufferedReader reader = new BufferedReader( new InputStreamReader( channel.getInputStream() ) );

                String output;
                while ( ( output = reader.readLine() ) != null ) {
                    messages.add( output );
                }
                reader.close();

                reader = new BufferedReader( new InputStreamReader( ( ( ChannelExec ) channel ).getErrStream() ) );

                while ( ( output = reader.readLine() ) != null ) {
                    errMessages.add( output );
                }
                reader.close();

                channel.disconnect();
                session.disconnect();
                break;
            } catch ( Throwable t ) {
                if ( t.getCause() instanceof ConnectException ) {
                    LOG.warn( "Ssh server {} not up yet.", instanceUrl );
                }
            }
        }
    }

    public static ResponseInfo sendCommandToInstance ( String command,  String keyFile, String instanceURL ) {
        Collection<String> messages = new LinkedList<String>();
        Collection<String> errMessages = new LinkedList<String>();
        try {
            JSch ssh = new JSch();
            ssh.addIdentity( keyFile );
            Session session = ssh.getSession("ubuntu", instanceURL );
            session.setConfig( "StrictHostKeyChecking", "no" );
            session.connect();

            Channel channel = session.openChannel( "exec" );
            ( ( ChannelExec ) channel ).setCommand( command );
            channel.connect();
            BufferedReader reader = new BufferedReader( new InputStreamReader( channel.getInputStream() ) );

            String output;
            while ( ( output = reader.readLine() ) != null ) {
                messages.add( output );
            }
            reader.close();

            reader = new BufferedReader( new InputStreamReader( ( ( ChannelExec ) channel ).getErrStream() ) );

            while ( ( output = reader.readLine() ) != null ) {
                errMessages.add( output );
            }
            reader.close();

            channel.disconnect();
            session.disconnect();
            return new ResponseInfo( instanceURL, true, true, messages, errMessages ) ;
        }
        catch ( Exception e ) {
            LOG.warn( "Error while restarting tomcat on " + instanceURL, e );
            return new ResponseInfo( instanceURL, false, false, messages, errMessages );
        }
    }
}
