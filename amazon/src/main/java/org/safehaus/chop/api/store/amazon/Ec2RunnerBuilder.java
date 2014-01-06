package org.safehaus.chop.api.store.amazon;


import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.safehaus.chop.api.RunnerFig;
import org.safehaus.guicyfig.Bypass;
import org.safehaus.guicyfig.OptionState;
import org.safehaus.guicyfig.Overrides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Builds a RunnerFig in the EC2 environment.
 */
public class Ec2RunnerBuilder {
    private static final Logger LOG = LoggerFactory.getLogger( Ec2RunnerBuilder.class );

    private static final String EC2METADATA_PROCESS = "/usr/bin/ec2metadata";
    private static final String PUBLIC_HOSTNAME_KEY = "public-hostname";
    private static final String PUBLIC_IPV4_KEY = "public-ipv4";
    private static final String SERVLET_TEMP_DIR = "javax.servlet.context.tempdir";

    private Properties props = new Properties();

    private String ipv4Address;
    private String hostname;
    private int serverPort;
    private String url;
    private String runnerTempDir;


    /**
     * Creates a runner builder that builds a RunnerFig using values in a
     * properties file. Used primarily to create a representation of a
     * remote runner.
     *
     * @param in the InputStream to the properties file
     * @throws IOException if there is a problem reading from the properties file input stream
     */
    Ec2RunnerBuilder( InputStream in ) throws IOException {
        props.load( in );
        extractValues();
    }


    /**
     * Creates a runner builder that builds a RunnerFig using values produced by
     * a program on an EC2 instance if this code does in fact run on an EC2
     * instance.
     */
    @SuppressWarnings( "UnusedDeclaration" )
    Ec2RunnerBuilder() {
        if ( ! new File( EC2METADATA_PROCESS ).exists() ) {
            return;
        }

        try {
            File file = File.createTempFile( "ec2metadata", "out" );
            ProcessBuilder pb = new ProcessBuilder( EC2METADATA_PROCESS );
            pb.redirectOutput( file );
            Process process = pb.start();

            try {
                process.waitFor();
            }
            catch ( InterruptedException e ) {
                LOG.error( "Interrupted while waiting for process {}", EC2METADATA_PROCESS, e );
            }

            props.load( new FileInputStream( file ) );

            if ( LOG.isDebugEnabled() ) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                props.store( out, null );
                LOG.debug( "Contents of RunnerFig =\n{}", new String( out.toByteArray() ) );
            }
        }
        catch ( IOException e ) {
            LOG.error( "Failed to execute process {}", EC2METADATA_PROCESS, e );
        }

        normalizeProperties();
        extractValues();
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public Ec2RunnerBuilder setIpv4Address( String ipv4Address ) {
        this.ipv4Address = ipv4Address;
        return this;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public Ec2RunnerBuilder setHostname( String hostname ) {
        this.hostname = hostname;
        return this;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public Ec2RunnerBuilder setServerPort( int serverPort ) {
        this.serverPort = serverPort;
        return this;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public Ec2RunnerBuilder setUrl( String url ) {
        this.url = url;
        return this;
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public Ec2RunnerBuilder setRunnerTempDir( String runnerTempDir ) {
        this.runnerTempDir = runnerTempDir;
        return this;
    }


    private void extractValues() {
        ipv4Address = props.getProperty( RunnerFig.IPV4_KEY );
        hostname = props.getProperty( RunnerFig.HOSTNAME_KEY );
        serverPort = Integer.parseInt( props.getProperty( RunnerFig.SERVER_PORT_KEY ) );
        url = props.getProperty( RunnerFig.URL_KEY );
        runnerTempDir = props.getProperty( RunnerFig.RUNNER_TEMP_DIR_KEY );
    }


    private void normalizeProperties() {
        if ( props.containsKey( PUBLIC_HOSTNAME_KEY ) ) {
            props.setProperty( RunnerFig.HOSTNAME_KEY, props.getProperty( PUBLIC_HOSTNAME_KEY ) );
        }

        if ( props.containsKey( PUBLIC_IPV4_KEY ) ) {
            props.setProperty( RunnerFig.IPV4_KEY, props.getProperty( PUBLIC_IPV4_KEY ) );
        }

        if ( props.containsKey( SERVLET_TEMP_DIR ) ) {
            props.setProperty( RunnerFig.RUNNER_TEMP_DIR_KEY, props.getProperty( SERVLET_TEMP_DIR ) );
        }
    }


    public RunnerFig getRunner() {
        return new RunnerFig() {

            @Override
            public String getIpv4Address() {
                return ipv4Address;
            }


            @Override
            public String getHostname() {
                return hostname;
            }


            @Override
            public int getServerPort() {
                return serverPort;
            }


            @Override
            public String getUrl() {
                return url;
            }


            @Override
            public String getTempDir() {
                return runnerTempDir;
            }


            @Override
            public void addPropertyChangeListener( final PropertyChangeListener listener ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public void removePropertyChangeListener( final PropertyChangeListener listener ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public OptionState[] getOptions() {
                throw new UnsupportedOperationException();
            }


            @Override
            public OptionState getOption( final String key ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public String getKeyByMethod( final String methodName ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public Object getValueByMethod( final String methodName ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public Properties filterOptions( final Properties properties ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public Map<String, Object> filterOptions( final Map<String, Object> entries ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public void override( final String key, final String override ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public void setOverrides( final Overrides overrides ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public Overrides getOverrides() {
                throw new UnsupportedOperationException();
            }


            @Override
            public void bypass( final String key, final String bypass ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public void setBypass( final Bypass bypass ) {
                throw new UnsupportedOperationException();
            }


            @Override
            public Bypass getBypass() {
                throw new UnsupportedOperationException();
            }


            @Override
            public Class getFigInterface() {
                return RunnerFig.class;
            }


            @Override
            public boolean isSingleton() {
                return false;
            }
        };
    }
}
