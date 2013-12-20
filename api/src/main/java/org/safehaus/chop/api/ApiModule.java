/*
 * Created by IntelliJ IDEA.
 * User: akarasulu
 * Date: 12/8/13
 * Time: 6:48 PM
 */
package org.safehaus.chop.api;


import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.netflix.config.DynamicPropertyFactory;


public class ApiModule extends AbstractModule implements ConfigKeys {
    private DynamicPropertyFactory propertyFactory = DynamicPropertyFactory.getInstance();


    protected void configure() {
        bind( Project.class );
    }


    @Provides
    @Named( CHOP_VERSION_KEY )
    String getChopVersion() {
        return propertyFactory.getStringProperty( CHOP_VERSION_KEY, "1.0" ).get();
    }


    @Provides
    @Named( CREATE_TIMESTAMP_KEY )
    String getCreateTimestamp() {
        return propertyFactory.getStringProperty( CREATE_TIMESTAMP_KEY, "none" ).get();
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
    @Named( TEST_PACKAGE_BASE )
    String getTestPackageBase() {
        return propertyFactory.getStringProperty( TEST_PACKAGE_BASE, DEFAULT_PACKAGE_BASE ).get();
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
