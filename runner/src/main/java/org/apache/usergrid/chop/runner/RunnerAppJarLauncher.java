package org.apache.usergrid.chop.runner;


import org.safehaus.jettyjam.utils.JarJarClassLoader;


/**
 * Launches the main() of the RunnerAppJettyRunner.
 */
public class RunnerAppJarLauncher {

    public static void main( String[] args ) throws Throwable {
        JarJarClassLoader cl = new JarJarClassLoader();
        cl.invokeMain( "org.safehaus.chop.runner.RunnerAppJettyRunner", args );
    }
}
