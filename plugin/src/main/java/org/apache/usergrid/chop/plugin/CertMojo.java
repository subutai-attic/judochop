package org.apache.usergrid.chop.plugin;


import org.safehaus.chop.api.ChopUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;


/** @todo is this still necessary? */
@Mojo( name = "cert" )
public class CertMojo extends MainMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            ChopUtils.installRunnerKey( null, "tomcat" );

            if ( certStorePassphrase == null ) {
                ChopUtils.installCert( endpoint, 443, null );
            }
            else {
                ChopUtils.installCert( endpoint, 443, certStorePassphrase.toCharArray() );
            }
        }
        catch ( Exception e ) {
            getLog().error( "Failed to install certificate!", e );
        }
    }
}