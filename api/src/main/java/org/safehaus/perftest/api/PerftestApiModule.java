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


public class PerftestApiModule extends AbstractModule implements ConfigKeys {
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


    protected void configure() {
        bind( TestInfo.class ).to( TestInfoImpl.class );
    }


    @Provides
    @Named( PERFTEST_VERSION_KEY )
    String getPerftestVersion() {
        return propertyFactory.getStringProperty( PERFTEST_VERSION_KEY, "1.0" ).get();
    }


    @Provides
    @Named( CREATE_TIMESTAMP_KEY )
    String getCreateTimestamp() {
        return propertyFactory.getStringProperty( CREATE_TIMESTAMP_KEY,  "none" ).get();
    }


    @Provides
    @Named( GIT_UUID_KEY )
    String getGitUuid() {
        return propertyFactory.getStringProperty( GIT_UUID_KEY, "none" ).get();
    }


    @Provides
    @Named( GIT_URL_KEY )
    String getGitUrl() {
        return propertyFactory.getStringProperty( GIT_URL_KEY, "none" ).get();
    }


    @Provides
    @Named( GROUP_ID_KEY )
    String getGroupId() {
        return propertyFactory.getStringProperty( GROUP_ID_KEY, "none" ).get();
    }


    @Provides
    @Named( ARTIFACT_ID_KEY )
    String getArtifactId() {
        return propertyFactory.getStringProperty( ARTIFACT_ID_KEY, "none" ).get();
    }


    @Provides
    @Named( TEST_MODULE_FQCN_KEY )
    String getTestModuleFqcn() {
        return propertyFactory.getStringProperty( TEST_MODULE_FQCN_KEY, DEFAULT_TEST_MODULE ).get();
    }


    @Provides
    @Named( LOAD_KEY )
    String getLoadKey() {
        return propertyFactory.getStringProperty( LOAD_KEY, "none" ).get();
    }


    @Provides
    @Named( LOAD_TIME_KEY )
    String getLoadTimeKey() {
        return propertyFactory.getStringProperty( LOAD_TIME_KEY, "none" ).get();
    }
}
