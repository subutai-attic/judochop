package org.safehaus.chop.api;


import com.google.common.base.Preconditions;

import static org.safehaus.chop.api.Constants.RUNNER_WAR;


/**
 * General useful utility methods used in the Chop System.
 */
public class ChopUtils {

    /**
     * Calculates the testBase: the portion of the key or the path to the test's
     * runner.war but not including it. This usually has the 'tests'
     * container/folder in it followed by the shortened version UUID: for
     * example a project whose war is tests/70a4673b/runner.war will have
     * the testBase of tests/70a4673b/. The last '/' will always be included.
     *
     * @param project the project who's testBase to calculate
     * @return the testBase of the project
     * @throws NullPointerException if the project is null or it's loadKey property is null
     */
    public static String getTestBase( Project project ) {
        Preconditions.checkNotNull( project, "The project cannot be null." );
        return getTestBase( project.getLoadKey() );
    }


    /**
     * Calculates the testBase: the portion of the key or the path to the test's
     * runner.war but not including it. This usually has the 'tests'
     * container/folder in it followed by the shortened version UUID: for
     * example a project whose war is 'tests/70a4673b/runner.war' will have
     * the testBase of tests/70a4673b/. The last '/' will always be included.
     *
     * @param loadKey the loadKey of a project: i.e. 'tests/70a4673b/runner.war'
     * @return the testBase of the project
     * @throws NullPointerException if the loadKey is null
     */
    public static String getTestBase( String loadKey ) {
        Preconditions.checkNotNull( loadKey, "The loadKey argument cannot be null." );
        return loadKey.substring( 0, loadKey.length() - RUNNER_WAR.length() );
    }
}
