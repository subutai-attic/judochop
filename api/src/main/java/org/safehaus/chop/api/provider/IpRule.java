package org.safehaus.chop.api.provider;


import java.util.Collection;
import java.util.List;


/**
 * Provider agnostic interface for IpPermissions.
 */
public interface IpRule {
    String getIpProtocol();

    void setIpProtocol();

    IpRule withIpProtocol( String ipProtocol );

    Integer getFromPort();

    void setFromPort( Integer fromPort );

    IpRule withFromPort( Integer fromPort );

    Integer getToPort();

    void setToPort( Integer toPort );

    IpRule withToPort( Integer toPort );

    List<String> getIpRanges();

    void setIpRanges( Collection<String> ipRanges );

    IpRule withIpRanges( String... ipRanges );

    IpRule withIpRanges( Collection<String> ipRanges );
}
