package org.safehaus.chop.api;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;

import com.fasterxml.jackson.annotation.JsonProperty;


/** Minimal requirements for runner information. */
@FigSingleton
public interface RunnerFig extends GuicyFig {


    // ~~~~~~~~~~~~~~~~~~~~~ RunnerFig Related Configuration ~~~~~~~~~~~~~~~~~~~~


    String IPV4_KEY = "public-ipv4";

    /**
     * Gets the IPv4 public address used by the RunnerFig. Uses {@link RunnerFig#IPV4_KEY}
     * to access the IPv4 public address.
     *
     * @return the IPv4 public address (octet) as a String
     */
    @JsonProperty
    @Key( IPV4_KEY )
    String getIpv4Address();


    String HOSTNAME_KEY = "public-hostname";

    /**
     * Gets the public hostname of the RunnerFig. Uses {@link RunnerFig#HOSTNAME_KEY} to
     * access the public hostname.
     *
     * @return the public hostname
     */
    @JsonProperty
    @Key( HOSTNAME_KEY )
    String getHostname();

    String SERVER_PORT_KEY = "server.port";
    String DEFAULT_SERVER_PORT = "24981";

    /**
     * Gets the RunnerFig server port. Uses {@link RunnerFig#SERVER_PORT_KEY} to access
     * the server port. The default port used is setup via {@link
     * RunnerFig#DEFAULT_SERVER_PORT}.
     *
     * @return the RunnerFig's server port
     */
    @JsonProperty
    @Key( SERVER_PORT_KEY )
    @Default( DEFAULT_SERVER_PORT )
    int getServerPort();


    String URL_KEY = "url.key";

    /**
     * Gets the URL of the RunnerFig's REST interface. Uses {@link RunnerFig#URL_KEY} to
     * access the RunnerFig's URL.
     *
     * @return the URL of the RunnerFig's REST interface
     */
    @JsonProperty
    @Key( URL_KEY )
    String getUrl();


    String DEFAULT_RUNNER_TEMP_DIR = "/tmp";
    String RUNNER_TEMP_DIR_KEY = "runner.temp.dir";

    /**
     * Gets the temporary directory used by the RunnerFig to store files. Uses {@link
     * RunnerFig#RUNNER_TEMP_DIR_KEY} to access the temp dir used by the RunnerFig.
     *
     * @return the temporary directory used by the RunnerFig
     */
    @JsonProperty
    @Key( RUNNER_TEMP_DIR_KEY )
    @Default( DEFAULT_RUNNER_TEMP_DIR )
    String getTempDir();
}
