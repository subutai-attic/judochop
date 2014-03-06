package org.safehaus.chop.webapp.dao.model;

import org.safehaus.chop.api.Runner;
import org.safehaus.guicyfig.Bypass;
import org.safehaus.guicyfig.OptionState;
import org.safehaus.guicyfig.Overrides;

import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Properties;

public class BasicRunner implements Runner {

    private String hostname;

    public BasicRunner(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getIpv4Address() {
        return null;  
    }

    @Override
    public int getServerPort() {
        return 0;  
    }

    @Override
    public String getUrl() {
        return null;  
    }

    @Override
    public String getTempDir() {
        return null;  
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        
    }

    @Override
    public OptionState[] getOptions() {
        return new OptionState[0];  
    }

    @Override
    public OptionState getOption(String s) {
        return null;  
    }

    @Override
    public String getKeyByMethod(String s) {
        return null;  
    }

    @Override
    public Object getValueByMethod(String s) {
        return null;  
    }

    @Override
    public Properties filterOptions(Properties properties) {
        return null;  
    }

    @Override
    public Map<String, Object> filterOptions(Map<String, Object> stringObjectMap) {
        return null;  
    }

    @Override
    public void override(String s, String s2) {
        
    }

    @Override
    public boolean setOverrides(Overrides overrides) {
        return false;  
    }

    @Override
    public Overrides getOverrides() {
        return null;  
    }

    @Override
    public void bypass(String s, String s2) {
        
    }

    @Override
    public boolean setBypass(Bypass bypass) {
        return false;  
    }

    @Override
    public Bypass getBypass() {
        return null;  
    }

    @Override
    public Class getFigInterface() {
        return null;  
    }

    @Override
    public boolean isSingleton() {
        return false;  
    }
}
