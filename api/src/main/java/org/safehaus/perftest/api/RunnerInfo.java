package org.safehaus.perftest.api;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.safehaus.perftest.api.settings.ConfigKeys;

import com.fasterxml.jackson.annotation.JsonProperty;


/** Minimal requirements for runner information. */
public class RunnerInfo extends Properties implements ConfigKeys {


    @JsonProperty
    public String getIpv4() {
        return getProperty( IPV4_KEY );
    }


    @JsonProperty
    public String getHostname() {
        return getProperty( HOSTNAME_KEY );
    }


    @JsonProperty
    public int getServerPort() {
        return Integer.getInteger( getProperty( SERVER_PORT_KEY ) );
    }


    @JsonProperty
    public String getUrl() {
        return getProperty( URL_KEY );
    }


    @JsonProperty
    public String getRunnerTempDir() {
        return getProperty( RUNNER_TEMP_DIR_KEY );
    }


    public RunnerInfo() {
        super();
    }


    public RunnerInfo( InputStream in ) throws IOException {
        super();
        load( in );
    }


    /**
     * Gets the properties listing as an input stream.
     *
     * @return the properties listing as an input stream
     *
     * @throws java.io.IOException there are io failures
     */
    public InputStream getPropertiesAsStream() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        store( bytes, null );
        bytes.flush();
        return new ByteArrayInputStream( bytes.toByteArray() );
    }
}
