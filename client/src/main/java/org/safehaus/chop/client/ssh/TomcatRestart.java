package org.safehaus.chop.client.ssh;


import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.safehaus.chop.api.SshValues;


/**
 * An AsyncSsh command that sets the new password and restarts tomcat.
 */
public class TomcatRestart<A> extends AsyncSsh<A> {
    public final String newPassword;

    public TomcatRestart( final String newPassword, final String sshKeyFile, final String server ) {
        super( "Tomcat Rest", sshKeyFile, server );
        this.newPassword = newPassword;
    }


    @Override
    public ResponseInfo call() {
        this.response = SSHCommands.restartTomcatOnInstance( newPassword, getSshKeyFile(), getServer() );
        return response;
    }


    public static <A> Collection<TomcatRestart<A>> extractTomcatFailures( Collection<TomcatRestart<A>> commands ) {
        Collection<TomcatRestart<A>> failures = new HashSet<TomcatRestart<A>>( commands.size() );

        for ( TomcatRestart<A> command : commands ) {
            if ( ! command.isSuccess() ) {
                failures.add( command );
            }
        }

        return failures;
    }


    public static <A> Collection<TomcatRestart<A>> getCommands( String newPassword,
                                                                Collection<A> associates, SshValues<A> values )
    {
        Collection<TomcatRestart<A>> commands = new HashSet<TomcatRestart<A>>( associates.size() );

        for ( A associate : associates ) {
            TomcatRestart<A> command = new TomcatRestart<A>( newPassword,
                    values.getSshKeyFile( associate ), values.getHostname( associate ) );
            commands.add( command );
        }

        return commands;
    }


    public static <A> boolean execute( String newPassword, Collection<A> associates, SshValues<A> values )
            throws InterruptedException
    {
        Collection<TomcatRestart<A>> commands = getCommands( newPassword, associates, values );
        ExecutorService service = Executors.newFixedThreadPool( associates.size() + 1 );
        service.invokeAll( commands );
        service.shutdown();

        return extractTomcatFailures( commands ).isEmpty();
    }

}
