package org.safehaus.chop.client.ssh;


import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;


/**
 * A reusable Ssh command that can be asynchronously executed and can work with
 * both an ExecutorService and standalone. Will experiment with what works faster
 * for Maven Plugin.
 */
public class AsyncSsh<A> implements Callable<ResponseInfo> {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger( AsyncSsh.class );

    private final String sshKeyFile;

    private final String server;

    private ResponseInfo response;

    private String command;

    private A associate;


    public AsyncSsh( String command, String sshKeyFile, String server ) {
        setCommand( command );
        this.sshKeyFile = sshKeyFile;
        this.server = server;
    }


    @Override
    public ResponseInfo call() throws Exception {
        response = SSHCommands.sendCommandToInstance( getCommand(), getSshKeyFile(), getServer() );
        LOG.info( "Successfully executed command {}", getCommand() );
        return getResponse();
    }


    public String getCommand() {
        return command;
    }


    public AsyncSsh<A> setCommand( String command ) {
        this.command = command;
        return this;
    }


    public String getSshKeyFile() {
        return sshKeyFile;
    }


    public String getServer() {
        return server;
    }


    public ResponseInfo getResponse() {
        return response;
    }


    public boolean isSuccess() {
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

        for ( A associate : associates ) {
            AsyncSsh<A> command = new AsyncSsh<A>( values.getCommand( associate ),
                    values.getSshKeyFile( associate ), values.getHostname( associate ) );
            commands.add( command );
        }

        return commands;
    }


    public static <A> boolean execute( Collection<A> associates, SshValues<A> values ) throws InterruptedException {
        Collection<AsyncSsh<A>> commands = getCommands( associates, values );
        ExecutorService service = Executors.newFixedThreadPool( associates.size() + 1 );
        service.invokeAll( commands );
        service.shutdown();

        return extractFailures( commands ).isEmpty();
    }
}
