package org.safehaus.chop.api;


/**
 * This represents the Maven Module under test.
 */
public interface Module {
    String getGroupId();

    String getArtifactId();

    String getVersion();

    String getVcsRepoUrl();

    // Enum for vcs type later

    String getTestPackageBase();
}
