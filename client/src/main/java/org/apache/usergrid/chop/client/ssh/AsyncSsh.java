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


import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.usergrid.chop.api.SshValues;


/**
 * A reusable Ssh command that can be asynchronously executed and can work with
 * both an ExecutorService and standalone. Will experiment with what works faster
 * for Maven Plugin.
 */
public class AsyncSsh<A> implements Callable<ResponseInfo> {

    private String sshKeyFile;

    private String server;

    private String srcFile;

    private String destFile;

    private boolean isScpCommand;

    protected ResponseInfo response;

    private String command;

    private A associate;


    public AsyncSsh( String command, String sshKeyFile, String server ) {
        this.command = command;
        this.sshKeyFile = sshKeyFile;
        this.server = server;
        this.isScpCommand = false;
    }


    public AsyncSsh( String srcFile, String destFile, String sshKeyFile, String server ) {
        this.srcFile = srcFile;
        this.destFile = destFile;
        this.sshKeyFile = sshKeyFile;
        this.server = server;
        this.isScpCommand = true;
    }


    @Override
    public ResponseInfo call() throws Exception {
        if( isScpCommand ) {
            response = SSHCommands.scpFileToInstance( srcFile, destFile, sshKeyFile, server );
        }
        else {
            response = SSHCommands.sendCommandToInstance( command, sshKeyFile, server );
        }
        return response;
    }


    public String getCommand() {
        return command;
    }


    public String getSshKeyFile() {
        return sshKeyFile;
    }


    public String getServer() {
        return server;
    }


    public String getSrcFile() {
        return srcFile;
    }


    public String getDestFile() {
        return destFile;
    }


    public boolean isScpCommand() {
        return isScpCommand;
    }


    public ResponseInfo getResponse() {
        return response;
    }


    public boolean isSuccess() {
        //noinspection SimplifiableIfStatement
        if ( response == null ) {
            return false;
        }

        return response.isOperationSuccessful() && response.isRequestSuccessful();
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public A getAssociate() {
        return associate;
    }


    public AsyncSsh<A> setAssociate( A associate ) {
        this.associate = associate;
        return this;
    }


    @Override
    public int hashCode() {
        return server.hashCode();
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if( isScpCommand ) {
            sb.append( "scp -i " )
              .append( sshKeyFile )
              .append( " " )
              .append( srcFile )
              .append( " ubuntu@" )
              .append( server )
              .append( ":" )
              .append( destFile );
        }
        else {
            sb.append( "ssh -i " )
              .append( sshKeyFile )
              .append( " ubuntu@" )
              .append( server )
              .append( " " )
              .append( command );
        }
        return sb.toString();
    }


    public static <A> Collection<AsyncSsh<A>> extractFailures( Collection<AsyncSsh<A>> commands ) {
        Collection<AsyncSsh<A>> failures = new HashSet<AsyncSsh<A>>( commands.size() );

        for ( AsyncSsh<A> command : commands ) {
            if ( ! command.isSuccess() ) {
                failures.add( command );
            }
        }

        return failures;
    }


    public static <A> Collection<AsyncSsh<A>> getCommands( Collection<A> associates, SshValues<A> values ) {
        Collection<AsyncSsh<A>> commands = new HashSet<AsyncSsh<A>>( associates.size() );
        AsyncSsh<A> command;

        for ( A associate : associates ) {
            if( values.isScpCommand( associate ) ) {
                command = new AsyncSsh<A>( values.getSourceFile( associate ), values.getDestinationFile( associate ),
                        values.getSshKeyFile( associate ), values.getHostname( associate ) );
            }
            else {
                command = new AsyncSsh<A>( values.getCommand( associate ), values.getSshKeyFile( associate ), values
                                .getHostname( associate ) );
            }
            commands.add( command );
        }

        return commands;
    }


    public static <A> Collection<AsyncSsh<A>> execute( Collection<A> associates, SshValues<A> values )
            throws InterruptedException {
        Collection<AsyncSsh<A>> commands = getCommands( associates, values );
        ExecutorService service = Executors.newFixedThreadPool( associates.size() + 1 );
        service.invokeAll( commands );
        service.shutdown();

        return commands;
    }
}
