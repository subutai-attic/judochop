package org.safehaus.chop.stack;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * A basic IpRule implementation.
 */
public class BasicIpRule implements IpRule {
    private String ipProtocol;
    private Integer toPort;
    private Integer fromPort;
    private List<String> ipRanges = new ArrayList<String>();


    @Override
    public String getIpProtocol() {
        return ipProtocol;
    }


    public void setIpProtocol( final String ipProtocol ) {
        this.ipProtocol = ipProtocol;
    }


    @Override
    public IpRule withIpProtocol( final String ipProtocol ) {
        this.ipProtocol = ipProtocol;
        return this;
    }


    @Override
    public Integer getFromPort() {
        return fromPort;
    }


    public void setFromPort( Integer fromPort ) {
        this.fromPort = fromPort;
    }


    @Override
    public IpRule withFromPort( final Integer fromPort ) {
        this.fromPort = fromPort;
        return this;
    }


    @Override
    public Integer getToPort() {
        return toPort;
    }


    @Override
    public IpRule withToPort( final Integer toPort ) {
        this.toPort = toPort;
        return this;
    }


    public void setToPort( Integer toPort ) {
        this.toPort = toPort;
    }


    @Override
    public List<String> getIpRanges() {
        return ipRanges;
    }


    public void setIpRanges( List<String> ipRanges ) {
        this.ipRanges = ipRanges;
    }


    @Override
    public IpRule withIpRanges( final String... ipRanges ) {
        Collections.addAll( this.ipRanges, ipRanges );
        return this;
    }
}
