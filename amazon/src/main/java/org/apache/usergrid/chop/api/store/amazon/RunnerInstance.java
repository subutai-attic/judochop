package org.apache.usergrid.chop.api.store.amazon;


import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Properties;

import org.apache.usergrid.chop.api.Runner;
import org.safehaus.guicyfig.Bypass;
import org.safehaus.guicyfig.OptionState;
import org.safehaus.guicyfig.Overrides;

import com.amazonaws.services.ec2.model.Instance;


/**
 * Bogus runner implementation.
 */
public class RunnerInstance implements Runner {
    private String ipv4Address;
    private String hostname;
    private String url;
    private Instance instance;
    private int port = Integer.parseInt( Runner.DEFAULT_SERVER_PORT );


    public RunnerInstance( Instance instance ) {
        this.instance = instance;
        this.hostname = instance.getPublicDnsName();
        this.ipv4Address = instance.getPublicIpAddress();
        this.url = "https://" + instance.getPublicDnsName() + ":" + Runner.DEFAULT_SERVER_PORT + "/";
    }

    @SuppressWarnings( "UnusedDeclaration" )
    public RunnerInstance( Instance instance, int port ) {
        this.instance = instance;
        this.port = port;
        this.hostname = instance.getPublicDnsName();
        this.ipv4Address = instance.getPublicIpAddress();
        this.url = "https://" + instance.getPublicDnsName() + ":" + port + "/";
    }


    public Instance getInstance() {
        return instance;
    }


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
        return port;
    }


    @Override
    public String getUrl() {
        return url;
    }


    @Override
    public String getTempDir() {
        throw new UnsupportedOperationException();
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
    public OptionState getOption( final String s ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public String getKeyByMethod( final String s ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Object getValueByMethod( final String s ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Properties filterOptions( final Properties properties ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Map<String, Object> filterOptions( final Map<String, Object> stringObjectMap ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void override( final String s, final String s2 ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean setOverrides( final Overrides overrides ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Overrides getOverrides() {
        throw new UnsupportedOperationException();
    }


    @Override
    public void bypass( final String s, final String s2 ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public boolean setBypass( final Bypass bypass ) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Bypass getBypass() {
        throw new UnsupportedOperationException();
    }


    @Override
    public Class getFigInterface() {
        return Runner.class;
    }


    @Override
    public boolean isSingleton() {
        return false;
    }
}
