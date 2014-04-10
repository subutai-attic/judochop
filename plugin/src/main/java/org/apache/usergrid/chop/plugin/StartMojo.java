package org.apache.usergrid.chop.plugin;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;


@Mojo ( name = "start", requiresDependencyResolution = ResolutionScope.TEST,
        requiresDependencyCollection = ResolutionScope.TEST )
public class StartMojo extends MainMojo {


    @Override
    public void execute() throws MojoExecutionException {


    }
}