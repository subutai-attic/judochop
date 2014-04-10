package org.apache.usergrid.chop.plugin;


import org.apache.usergrid.chop.api.ChopUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;


@Mojo( name = "setup" )
public class SetupMojo extends MainMojo {

    protected SetupMojo( MainMojo mojo ) {
        this.username = mojo.username;
        this.password = mojo.password;
        this.endpoint = mojo.endpoint;
        this.certStorePassphrase = mojo.certStorePassphrase;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.runnerCount = mojo.runnerCount;
    }


    @Override
    public void execute() throws MojoExecutionException {
        initCertStore();

    }


    protected void initCertStore() {
        if ( ChopUtils.isStoreInitialized() ) {
            return;
        }

        try {
            if ( certStorePassphrase == null ) {
                ChopUtils.installCert( endpoint, 443, null );
            }
            else {
                ChopUtils.installCert( endpoint, 443, certStorePassphrase.toCharArray() );
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
