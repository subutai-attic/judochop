package org.safehaus.chop.stack;


import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * Provider agnostic interface for IpPermissions.
 */
@JsonDeserialize( as = BasicIpRule.class )
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
}
