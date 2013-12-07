package org.safehaus.perftest;


import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Minimal requirements for runner information.
 */
public interface RunnerInfo {
    @SuppressWarnings( "UnusedDeclaration" )
    @JsonProperty
    String getIpv4();

    @JsonProperty
    String getHostname();

    @JsonProperty
    int getServerPort();

    @JsonProperty
    String getFormation();

    void setFormation( String formation );

    @JsonProperty
    String getUrl();

    @JsonProperty
    String getTempDir();
}
