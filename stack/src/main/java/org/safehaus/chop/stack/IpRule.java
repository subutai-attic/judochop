package org.safehaus.chop.stack;


import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Provider agnostic interface for IpPermissions.
 */
public interface IpRule {

    @JsonProperty
    String getIpProtocol();

    @JsonProperty
    IpRule withIpProtocol( String ipProtocol );

    @JsonProperty
    Integer getFromPort();

    @JsonProperty
    IpRule withFromPort( Integer fromPort );

    @JsonProperty
    Integer getToPort();

    @JsonProperty
    IpRule withToPort( Integer toPort );

    @JsonProperty
    List<String> getIpRanges();

    @JsonProperty
    IpRule withIpRanges( String... ipRanges );

    @JsonProperty
    IpRule withIpRanges( Collection<String> ipRanges );
}
