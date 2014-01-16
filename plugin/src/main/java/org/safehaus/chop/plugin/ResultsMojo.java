package org.safehaus.chop.plugin;


import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

import com.google.common.base.Preconditions;


/**
 * This mojo implements the results goal, which downloads all or a partial
 * set of results from the store.
 */
@Mojo( name = "results" )
public class ResultsMojo extends MainMojo {

    enum DumpType {
        FULL, SUMMARY, TEST, RUN
    }

    private DumpType type;


    protected ResultsMojo( MainMojo mojo ) {
        this.resultsDirectory = mojo.resultsDirectory;
        this.dumpType = mojo.dumpType;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.localRepository = mojo.localRepository;
        this.accessKey = mojo.accessKey;
        this.secretKey = mojo.secretKey;
        this.bucketName = mojo.bucketName;
        this.destinationParentDir = mojo.destinationParentDir;
        this.managerAppUsername = mojo.managerAppUsername;
        this.managerAppPassword = mojo.managerAppPassword;
        this.testPackageBase = mojo.testPackageBase;
        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
        this.amiID = mojo.amiID;
        this.awsSecurityGroup = mojo.awsSecurityGroup;
        this.runnerKeyPairName = mojo.runnerKeyPairName;
        this.runnerName = mojo.runnerName;
        this.instanceType = mojo.instanceType;
        this.setupTimeout = mojo.setupTimeout;
        this.minimumRunners = mojo.minimumRunners;
        this.maximumRunners = mojo.maximumRunners;
        this.securityGroupExceptions = mojo.securityGroupExceptions;
        this.availabilityZone = mojo.availabilityZone;
        this.resetIfStopped = mojo.resetIfStopped;
        this.coldRestartTomcat = mojo.coldRestartTomcat;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }


    protected ResultsMojo() {

    }


    @Override
    public void execute() throws MojoExecutionException {
        preconditions();
    }


    private void preconditions() throws MojoExecutionException {
        Preconditions.checkNotNull( resultsDirectory );
        Preconditions.checkNotNull( dumpType );

        if ( ! resultsDirectory.exists() ) {
            if ( ! resultsDirectory.mkdirs() ) {
                throw new MojoExecutionException( "Cannot create resultsDirectory "
                        + resultsDirectory.getAbsolutePath() );
            }
        }
        else if ( ! resultsDirectory.isDirectory() ) {
            throw new MojoExecutionException( "Path to resultsDirectory " + resultsDirectory.getAbsolutePath()
                    + " is not a directory." );
        }
        else if ( ! resultsDirectory.canWrite() ) {
            throw new MojoExecutionException( "No permission to write to resultsDirectory "
                    + resultsDirectory.getAbsolutePath() );
        }

        try {
            type = DumpType.valueOf( dumpType );
        }
        catch ( IllegalArgumentException iae ) {
            throw new MojoExecutionException( "Provided value '" + dumpType
                    + "' for dumpType does not match a valid dumpType enum: "
                    + Arrays.toString( DumpType.values() ), iae );
        }
    }
}

