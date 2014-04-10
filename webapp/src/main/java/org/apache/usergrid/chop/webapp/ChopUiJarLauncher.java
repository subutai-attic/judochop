package org.apache.usergrid.chop.webapp;


import org.safehaus.jettyjam.utils.JarJarClassLoader;


/**
 * Launches the main() of the ChopUiJettyRunner
 */
public class ChopUiJarLauncher {

    public static void main( String[] args ) throws Throwable {
        JarJarClassLoader cl = new JarJarClassLoader();
        cl.invokeMain( "org.apache.usergrid.chop.webapp.ChopUiJettyRunner", args );
    }
}
