/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.client.ssh;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
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

    private static final int SESSION_CONNECT_TIMEOUT = 120000;
    public static final String DEFAULT_USER = "ubuntu";


    public static ResponseInfo sendCommandToInstance ( String command,  String keyFile, String hostURL ) {
        Collection<String> messages = new LinkedList<String>();
        Collection<String> errMessages = new LinkedList<String>();
        Session session = null;
        Channel channel = null;

        try {
            session = getSession( hostURL, keyFile );
            if( session == null ) {
                errMessages.add( "Could not get session to connect to " + hostURL );
                return new ResponseInfo( hostURL, true, false, messages, errMessages );
            }
            channel = session.openChannel( "exec" );
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
            return new ResponseInfo( hostURL, true, true, messages, errMessages ) ;
        }
        catch ( Exception e ) {
            LOG.warn( "Error while sending ssh command to " + hostURL, e );
            return new ResponseInfo( hostURL, false, false, messages, errMessages );
        }
        finally {
            try {
                if ( channel != null ) {
                    channel.disconnect();
                }
            }
            catch ( Exception e ) { }
            try {
                if ( session != null ) {
                    session.disconnect();
                }
            }
            catch ( Exception e ) { }
        }
    }


    /**
     * @param srcfilePath Full path of the source file
     * @param destFilePath Full path of the file on destination or the folder under which the source file will be put
     * @param keyFile Full path of the key file that will be used in SSH authentication to the host
     * @param hostURL Either the IP or the host name of destination host
     * @return
     */
    public static ResponseInfo scpFileToInstance( String srcfilePath, String destFilePath, String keyFile,
                                                  String hostURL ) {

        Collection<String> messages = new LinkedList<String>();
        Collection<String> errMessages = new LinkedList<String>();
        FileInputStream fis = null;
        Session session = null;
        Channel channel = null;
        String output;

        try {
            session = getSession( hostURL, keyFile );
            if( session == null ) {
                errMessages.add( "Could not get session to connect to " + hostURL );
                return new ResponseInfo( hostURL, true, false, messages, errMessages );
            }
            // exec 'scp -t destFile' remotely
            String command = "scp -t " + destFilePath;
            channel = session.openChannel( "exec" );
            ( ( ChannelExec ) channel ).setCommand( command );

            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();

            channel.connect();

            if( ( output = checkAck( in ) ) != null ) {
                errMessages.add( output );
                return new ResponseInfo( hostURL, true, false, messages, errMessages );
            }

            File srcFile = new File( srcfilePath );

            // send "C0<filemode> filesize filename", where filename should not include '/'
            StringBuilder sb = new StringBuilder();
            String fileMode = PosixFilePermissions.toString( Files.getPosixFilePermissions( srcFile.toPath() ) );
            long filesize = srcFile.length();
            command = sb.append( "C0" )
                        .append( convertToNumericalForm( fileMode ) )
                        .append( " " )
                        .append( filesize )
                        .append( " " )
                        .append( srcFile.getName() )
                        .append( "\n" )
                        .toString();

            out.write( command.getBytes() );
            out.flush();

            if( ( output = checkAck( in ) ) != null ) {
                errMessages.add( output );
                return new ResponseInfo( hostURL, true, false, messages, errMessages );
            }

            // send the content of source file
            fis = new FileInputStream( srcfilePath );
            byte[] buf = new byte[ 1024 ];
            while( true ) {
                int len = fis.read( buf, 0, buf.length );
                if( len <= 0 ) {
                    break;
                }
                out.write( buf, 0, len );
            }

            fis.close();
            fis=null;

            // send '\0'
            buf[ 0 ] = 0;
            out.write( buf, 0, 1 );
            out.flush();

            if( ( output = checkAck( in ) ) != null ) {
                errMessages.add( output );
                return new ResponseInfo( hostURL, true, false, messages, errMessages );
            }

            out.close();
            channel.disconnect();
            session.disconnect();

            return new ResponseInfo( hostURL, true, true, messages, errMessages );
        }
        catch( Exception e ) {
            LOG.error( "Error while SCPing file to " + hostURL, e );
            return new ResponseInfo( hostURL, true, false, messages, errMessages );
        }
        finally {
            try {
                if ( channel != null ) {
                    channel.disconnect();
                }
            }
            catch ( Exception e ) { }
            try {
                if ( session != null ) {
                    session.disconnect();
                }
            }
            catch ( Exception e ) { }
            try {
                if( fis != null ) {
                    fis.close();
                }
            }
            catch( Exception e ) { }
        }
    }


    private static Session getSession( String hostURL, String keyFile ) {
        JSch ssh;
        Session session = null;

        boolean successful = SSHCommands.waitActive( hostURL, 22, SESSION_CONNECT_TIMEOUT );
        if( ! successful ) {
            LOG.warn( "Can't reach ssh port of host {}", hostURL );
        }

        // try to open ssh session
        try {
            ssh = new JSch();
            ssh.addIdentity( keyFile );
            session = ssh.getSession( DEFAULT_USER, hostURL );
            session.setConfig( "StrictHostKeyChecking", "no" );
            session.connect();

            // should be successful, so we can continue
            return session;
        }
        catch ( Exception e ) {
            LOG.error( "Error while connecting to ssh session of " + hostURL, e );
        }
        finally {
            try {
                if( session != null ) {
                    session.disconnect();
                }
            }
            catch ( Exception ee ) { }
        }
        return null;
    }


    public static boolean waitActive( String hostURL, int port, int timeout ) {
        LOG.info( "Waiting maximum {} msecs for SSH port of {} to get active", timeout, hostURL );
        long startTime = System.currentTimeMillis();

        while ( System.currentTimeMillis() - startTime < timeout ) {
            Socket s = null;
            try {
                s = new Socket();
                s.setReuseAddress( true );
                SocketAddress sa = new InetSocketAddress( hostURL, port );
                s.connect( sa, 2000 );
                return true;
            }
            catch ( Exception e ) {
            }
            finally {
                if ( s != null ) {
                    try {
                        s.close();
                    }
                    catch ( IOException e ) {
                    }
                }
            }
        }
        return false;
    }


    private static String checkAck( InputStream in ) throws IOException {
        int b = in.read();

        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if( b == 0 || b == -1 ) {
            return null;
        }

        if( b == 1 || b == 2 ) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
    	        c=in.read();
    	        sb.append((char)c);
            }
            while ( c != '\n' );

            return sb.toString();
        }
        throw new RuntimeException( "Invalid value, this shouldn't have gotten here" );
    }


    private static String convertToNumericalForm( String fileMode ) {

        if ( fileMode.length() != 9 ) {
            throw new RuntimeException( "File mode string should be 9 characters long: " + fileMode );
        }

        int[] permissions = new int[3];

        for( int i = 0; i < 3; i++ ) {
            if( fileMode.charAt( i * 3 ) == 'r' ) {
                permissions[ i ] += 4;
            }
            if( fileMode.charAt( i * 3 + 1 ) == 'w' ) {
                permissions[ i ] += 2;
            }
            if( fileMode.charAt( i * 3 + 2 ) == 'x' ) {
                permissions[ i ] += 1;
            }
        }

        StringBuilder sb = new StringBuilder( 3 );
        return sb.append( permissions[ 0 ] )
                 .append( permissions[ 1 ] )
                 .append( permissions[ 2 ] )
                 .toString();
    }
}
