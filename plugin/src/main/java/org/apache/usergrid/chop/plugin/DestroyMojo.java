package org.apache.usergrid.chop.plugin;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;


/** Hits the coordinator endpoint to destroy all runner instances */
@Mojo ( name = "destroy" )
public class DestroyMojo extends MainMojo {

    @Override
    public void execute() throws MojoExecutionException {

    }
}

