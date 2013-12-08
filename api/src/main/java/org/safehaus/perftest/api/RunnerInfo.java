package org.safehaus.perftest.api;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Minimal requirements for runner information.
 */
public class RunnerInfo extends Properties {


    @JsonProperty
    public String getIpv4() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


    @JsonProperty
    public String getHostname() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


    @JsonProperty
    public int getServerPort() {
        return 0;  //To change body of created methods use File | Settings | File Templates.
    }


    @JsonProperty
    public String getUrl() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


    @JsonProperty
    public String getRunnerTempDir() {
        return null;  //To change body of created methods use File | Settings | File Templates.
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
     * @return the properties listing as an input stream
     * @throws java.io.IOException there are io failures
     */
    public InputStream getPropertiesAsStream() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        store( bytes, null );
        bytes.flush();
        return new ByteArrayInputStream( bytes.toByteArray() );
    }
}
