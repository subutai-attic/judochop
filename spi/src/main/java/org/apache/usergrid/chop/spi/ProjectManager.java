package org.apache.usergrid.chop.spi;


import java.io.IOException;
import java.util.Set;

import org.apache.usergrid.chop.api.Project;


/**
 * Manages projects.
 */
public interface ProjectManager {

    /**
     * Scans for projects with test information using key like:
     * </p>
     * "$TESTS_PATH/.*\/$PROJECT_FILE
     *
     * @return a set of projects
     */
    Set<Project> getProjects() throws IOException;

    /**
     * Deletes all the projects.
     */
    void deleteProjects();

    /**
     * Stores the project test information.
     *
     * @param project the Project object to be serialized and stored
     */
    void store( Project project );

    /**
     * Tries to load a Project file based on prepackaged runner metadata: the runner's
     * loadKey. If it cannot find it, null is returned.
     *
     * @param runnerWar the load key for the runner war
     * @return the Project object if it exists in the store or null if it does not
     */
    Project getProject( String runnerWar );

}
