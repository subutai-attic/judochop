package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A run of a specific version of a Maven Module under test.
 */
public interface Run {

    String getId();

    /**
     * Returns a specific commit version of a Maven Module under test.
     */
    @JsonProperty
    String getCommitId();

    /**
     * Returns a runner.
     */
    @JsonProperty
    String getRunner();

    /**
     * Gets the fully qualified name of the Class that was chopped.
     *
     * @return the name of the chopped test
     */
    @JsonProperty
    String getTestName();

    /**
     * Gets the run number. Each start that is issued against the same
     * Runner will increment the run number. The results of each run
     * are kept separate with a path component being the run number.
     *
     * @return the run number
     */
    @JsonProperty
    int getRunNumber();

}
