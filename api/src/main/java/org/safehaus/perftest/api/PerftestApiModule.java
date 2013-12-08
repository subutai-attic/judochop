/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 6:48 PM
 */
package org.safehaus.perftest.api;


import org.safehaus.perftest.api.settings.ConfigKeys;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;


public class PerftestApiModule extends AbstractModule implements ConfigKeys {
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


    protected void configure() {
        bind( TestInfo.class ).to( TestInfoImpl.class );
    }


    @Provides
    @Named( PERFTEST_VERSION_KEY )
    DynamicStringProperty getPerftestVersion() {
        return propertyFactory.getStringProperty( PERFTEST_VERSION_KEY, "1.0" );
    }


    @Provides
    @Named( CREATE_TIMESTAMP_KEY )
    DynamicStringProperty getCreateTimestamp() {
        return propertyFactory.getStringProperty( CREATE_TIMESTAMP_KEY,  "none" );
    }


    @Provides
    @Named( GIT_UUID_KEY )
    DynamicStringProperty getGitUuid() {
        return propertyFactory.getStringProperty( GIT_UUID_KEY, "none" );
    }


    @Provides
    @Named( GIT_URL_KEY )
    DynamicStringProperty getGitUrl() {
        return propertyFactory.getStringProperty( GIT_URL_KEY, "none" );
    }


    @Provides
    @Named( GROUP_ID_KEY )
    DynamicStringProperty getGroupId() {
        return propertyFactory.getStringProperty( GROUP_ID_KEY, "none" );
    }


    @Provides
    @Named( ARTIFACT_ID_KEY )
    DynamicStringProperty getArtifactId() {
        return propertyFactory.getStringProperty( ARTIFACT_ID_KEY, "none" );
    }


    @Provides
    @Named( TEST_MODULE_FQCN_KEY )
    DynamicStringProperty getTestModuleFqcn() {
        return propertyFactory.getStringProperty( TEST_MODULE_FQCN_KEY, DEFAULT_TEST_MODULE );
    }
}
