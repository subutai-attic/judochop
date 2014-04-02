/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 5:40 PM
 */
package org.safehaus.chop.api.store.amazon;


import java.util.ArrayList;
import java.util.List;

import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.Runner;
import org.safehaus.chop.spi.InstanceManager;
import org.safehaus.chop.spi.IpRuleManager;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.GuicyFigModule;

import com.google.inject.AbstractModule;


public class AmazonModule extends AbstractModule implements Constants {


    protected void configure() {
        List<Class<? extends GuicyFig>> figs = new ArrayList<Class<? extends GuicyFig>>( 2 );
        figs.add( AmazonFig.class );
        figs.add( Runner.class );
        install( new GuicyFigModule( figs ) );
        bind( InstanceManager.class ).to( EC2InstanceManager.class );
        bind( IpRuleManager.class ).to( AmazonIpRuleManager.class );
    }
}
