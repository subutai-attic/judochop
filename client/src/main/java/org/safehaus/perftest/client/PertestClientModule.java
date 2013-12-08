/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 1:48 PM
 */
package org.safehaus.perftest.client;


import org.safehaus.perftest.api.store.amazon.AmazonStoreModule;

import com.google.inject.AbstractModule;
import com.netflix.config.DynamicPropertyFactory;


public class PertestClientModule extends AbstractModule implements ConfigKeys {
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


    protected void configure() {
        install( new AmazonStoreModule() );
        bind( PerftestClient.class ).to( PerftestClientImpl.class );
    }
}
