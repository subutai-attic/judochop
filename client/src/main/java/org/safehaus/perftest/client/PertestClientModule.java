/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 1:48 PM
 */
package org.safehaus.perftest.client;


import org.safehaus.perftest.annotations.DynStrProp;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.netflix.config.DynamicProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;


public class PertestClientModule extends AbstractModule implements ConfigKeys {
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();
    private DynamicStringProperty formation = propertyFactory.getStringProperty( FORMATION_KEY, "formationDefault" );

    protected void configure() {
        bind( PerftestClient.class ).to( PerftestClientImpl.class );

//        bind( DynamicStringProperty.class ).toProvider( DynamicStringPropertyProvider.class );
//        bind( DynamicStringProperty.class ).annotatedWith( Names.named( FORMATION_KEY ) ).toInstance( formation );
    }


//    @Provides
//    DynamicStringProperty getStringProperty( @Named( "keyVal" ) String keyVal, @Named( "defVal" ) String defVal ) {
//        return propertyFactory.getStringProperty( keyVal, defVal );
//    }
}
