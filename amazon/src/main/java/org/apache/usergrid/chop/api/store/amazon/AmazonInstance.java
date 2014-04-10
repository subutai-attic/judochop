package org.apache.usergrid.chop.api.store.amazon;


import org.apache.usergrid.chop.stack.Instance;
import org.apache.usergrid.chop.stack.InstanceState;
import org.apache.usergrid.chop.stack.InstanceSpec;


public class AmazonInstance implements Instance {

    private String id;
    private InstanceSpec spec;
    private InstanceState state;
    private String privateDnsName;
    private String publicDnsName;
    private String privateIpAddress;
    private String publicIpAddress;


    public AmazonInstance( final String id, final InstanceSpec spec, final InstanceState state,
                           final String privateDnsName, final String publicDnsName, final String privateIpAddress,
                           final String publicIpAddress ) {
        this.id = id;
        this.spec = spec;
        this.state = state;
        this.privateDnsName = privateDnsName;
        this.publicDnsName = publicDnsName;
        this.privateIpAddress = privateIpAddress;
        this.publicIpAddress = publicIpAddress;
    }


    @Override
    public String getId() {
        return id;
    }


    @Override
    public InstanceSpec getSpec() {
        return spec;
    }


    /**
     * @return Returns the last checked state of the instance
     */
    @Override
    public InstanceState getState() {
        return state;
    }


    @Override
    public String getPrivateDnsName() {
        return privateDnsName;
    }


    @Override
    public String getPublicDnsName() {
        return publicDnsName;
    }


    @Override
    public String getPrivateIpAddress() {
        return privateIpAddress;
    }


    @Override
    public String getPublicIpAddress() {
        return publicIpAddress;
    }
}
