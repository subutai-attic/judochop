package org.safehaus.chop.plugin;


import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.safehaus.chop.api.Constants;
import org.safehaus.chop.api.Project;
import org.safehaus.chop.api.ProjectBuilder;
import org.safehaus.chop.spi.Store;
import org.safehaus.chop.api.SshValues;
import org.safehaus.chop.api.store.amazon.EC2Manager;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.amazonaws.services.ec2.model.Instance;
import com.google.inject.Guice;
import com.google.inject.Injector;


/**
 * This is the parent class for all chop plugin goal classes, takes the configuration parameters from caller
 * module's pom and provides extended get methods for several file paths that will be used by extended classes
 */
public class MainMojo extends AbstractMojo implements Constants {

    static {
        System.setProperty( "javax.net.ssl.trustStore", "jssecacerts" );
    }

    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;


    @Parameter( defaultValue = "${plugin}", readonly = true )
    protected PluginDescriptor plugin;


    @Parameter( defaultValue = "${settings.localRepository}" )
    protected String localRepository;


    /**
     * Leaving this parameter with the default 'true' value causes the plugin goal to fail when there are modified
     * sources in the local git repository.
     */
    @Parameter( property = "failIfCommitNecessary", defaultValue = "true" )
    protected boolean failIfCommitNecessary;


    /**
     * Fully qualified package base property of the app once it's deployed to its container. This parameter will be put to the
     * project.properties file inside the WAR to be uploaded
     */
    @Parameter( property = "testPackageBase", required = true )
    protected String testPackageBase;


    /** The bucket to upload into */
    @Parameter( property = "bucketName", required = true )
    protected String bucketName;


    /** Access key for S3 */
    @Parameter( property = "accessKey", required = true )
    protected String accessKey;


    /** Secret key for S3 */
    @Parameter( property = "secretKey", required = true )
    protected String secretKey;


    /**
     * sourceFile will be uploaded as $destinationParentDir$commitUUID/RUNNER_WAR in S3 bucket
     *
     * defaultValue is "tests/"
     */
    @Parameter( property = "destinationParentDir", defaultValue = Constants.TESTS_PATH + "/" )
    protected String destinationParentDir;


    /**
     * Container's (probably Tomcat) Manager user name. This parameter will be put to the project.properties file inside
     * the WAR to be uploaded
     */
    @Parameter( property = "managerAppUsername", required = true )
    protected String managerAppUsername;


    /**
     * Container's (probably Tomcat) Manager user name. This parameter will be put to the project.properties file inside
     * the WAR to be uploaded
     */
    @Parameter( property = "managerAppPassword", required = true )
    protected String managerAppPassword;


    @Parameter( property = "runnerSSHKeyFile", required = true )
    protected String runnerSSHKeyFile;


    @Parameter( property = "amiID", required = true )
    protected String amiID;


    @Parameter( property = "awsSecurityGroup", required = true )
    protected String awsSecurityGroup;


    @Parameter( property = "runnerKeyPairName", required = true )
    protected String runnerKeyPairName;


    @Parameter( property = "runnerName", defaultValue = "chop-runner" )
    protected String runnerName;


    @Parameter( property = "instanceType" )
    protected String instanceType;


    @Parameter( property = "setupTimeout", defaultValue = "75000" )
    protected Integer setupTimeout;


    @Parameter( property = "runnerCount", defaultValue = "3" )
    protected Integer runnerCount;


    @Parameter( property = "securityGroupExceptions" )
    protected List securityGroupExceptions;


    @Parameter( property = "availabilityZone" )
    protected String availabilityZone;


    @Parameter( property = "resetIfStopped", defaultValue = "true" )
    protected boolean resetIfStopped;


    @Parameter( property = "coldRestartTomcat", defaultValue = "true" )
    protected boolean coldRestartTomcat;


    /**
     * Leaving this null will default to the use of "changeit" for the passphrase.
     */
    @Parameter( property = "certStorePassphrase" )
    protected String certStorePassphrase;


    /**
     * This is the folder to use for dumping results. If it does not exist it will be
     * automatically created for you.
     */
    @Parameter( property = "resultsDirectory", defaultValue = "target/chopResults" )
    protected File resultsDirectory;


    /**
     * This is the type of results dump to perform. The following enumeration values are
     * valid and explained:
     *
     * <ul>
     * <li>FULL: Summary and raw results for the project are pulled down</li>
     * <li>SUMMARY: Just the summary results are pulled down if not already present</li>
     * <li>TEST: Summary and raw results are pulled for this test, not the whole project</li>
     * <li>RUN: Summary and raw results are pulled for the last run, not the whole test, or project</li>
     *
     * NOTE: Regardless of the dumpType used files that have already been downloaded
     * from the store will NOT be downloaded again since results files essentially
     * do not change.
     */
    @Parameter( property = "dumpType", defaultValue = "FULL" )
    protected String dumpType;


    /**
     * The amount of time to sleep after creating instances to wait until they're up
     * and ready to be connected to.
     */
    @Parameter( property = "sleepAfterCreation", defaultValue = "30000" )
    protected long sleepAfterCreation;


    @Parameter( property = "blockUntilComplete", defaultValue = "true" )
    protected boolean blockUntilComplete;


    // ------------------------------------------------------------------------

    protected SshValues<Instance> list;
    protected SshValues<Instance> passwordTomcat;
    protected SshValues<Instance> startTomcat;
    protected SshValues<Instance> statusTomcat;
    protected SshValues<Instance> stopPasswordStartStatusTomcat;
    protected SshValues<Instance> stopTomcat;

    protected String endpoint;
    protected Store store;
    protected boolean setupCreatedInstances = false;

    protected static EC2Manager ec2Manager;
    protected static Collection<Instance> instances;
    protected static ExecutorService executor;


    @Override
    public void execute() throws MojoExecutionException {
    }


    @SuppressWarnings( "UnusedDeclaration" )
    protected MainMojo( MainMojo mojo ) {
        this.accessKey = mojo.accessKey;
        this.amiID = mojo.amiID;
        this.availabilityZone = mojo.availabilityZone;
        this.awsSecurityGroup = mojo.awsSecurityGroup;
        this.blockUntilComplete = mojo.blockUntilComplete;
        this.bucketName = mojo.bucketName;
        this.certStorePassphrase = mojo.certStorePassphrase;
        this.coldRestartTomcat = mojo.coldRestartTomcat;
        this.destinationParentDir = mojo.destinationParentDir;
        this.dumpType = mojo.dumpType;
        this.endpoint = mojo.endpoint;
        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
        this.instanceType = mojo.instanceType;
        this.localRepository = mojo.localRepository;
        this.managerAppPassword = mojo.managerAppPassword;
        this.managerAppUsername = mojo.managerAppUsername;
        this.plugin = mojo.plugin;
        this.project = mojo.project;
        this.resetIfStopped = mojo.resetIfStopped;
        this.resultsDirectory = mojo.resultsDirectory;
        this.runnerCount = mojo.runnerCount;
        this.runnerKeyPairName = mojo.runnerKeyPairName;
        this.runnerName = mojo.runnerName;
        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
        this.secretKey = mojo.secretKey;
        this.securityGroupExceptions = mojo.securityGroupExceptions;
        this.setupCreatedInstances = mojo.setupCreatedInstances;
        this.setupTimeout = mojo.setupTimeout;
        this.sleepAfterCreation = mojo.sleepAfterCreation;
        this.store = mojo.store;
        this.testPackageBase = mojo.testPackageBase;

    }


    protected MainMojo() {
    }


//    protected void copyFields( MainMojo mojo ) {
//        this.accessKey = mojo.accessKey;
//        this.amiID = mojo.amiID;
//        this.availabilityZone = mojo.availabilityZone;
//        this.awsSecurityGroup = mojo.awsSecurityGroup;
//        this.blockUntilComplete = mojo.blockUntilComplete;
//        this.bucketName = mojo.bucketName;
//        this.certStorePassphrase = mojo.certStorePassphrase;
//        this.coldRestartTomcat = mojo.coldRestartTomcat;
//        this.destinationParentDir = mojo.destinationParentDir;
//        this.dumpType = mojo.dumpType;
//        this.endpoint = mojo.endpoint;
//        this.failIfCommitNecessary = mojo.failIfCommitNecessary;
//        this.instanceType = mojo.instanceType;
//        this.localRepository = mojo.localRepository;
//        this.managerAppPassword = mojo.managerAppPassword;
//        this.managerAppUsername = mojo.managerAppUsername;
//        this.plugin = mojo.plugin;
//        this.project = mojo.project;
//        this.resetIfStopped = mojo.resetIfStopped;
//        this.resultsDirectory = mojo.resultsDirectory;
//        this.runnerCount = mojo.runnerCount;
//        this.runnerKeyPairName = mojo.runnerKeyPairName;
//        this.runnerName = mojo.runnerName;
//        this.runnerSSHKeyFile = mojo.runnerSSHKeyFile;
//        this.secretKey = mojo.secretKey;
//        this.securityGroupExceptions = mojo.securityGroupExceptions;
//        this.setupCreatedInstances = mojo.setupCreatedInstances;
//        this.setupTimeout = mojo.setupTimeout;
//        this.sleepAfterCreation = mojo.sleepAfterCreation;
//        this.store = mojo.store;
//        this.testPackageBase = mojo.testPackageBase;
//    }
//

//    protected void setSshValues() {
//        Preconditions.checkNotNull( runnerSSHKeyFile );
//        Preconditions.checkNotNull( managerAppPassword );
//
//        list = new InstanceValues( "ls", runnerSSHKeyFile );
//
//        String stop = "sudo service tomcat7 stop; ";
//        stopTomcat = new InstanceValues( stop, runnerSSHKeyFile );
//
//        String start = "sudo service tomcat7 start; ";
//        startTomcat = new InstanceValues( start, runnerSSHKeyFile );
//
//        String status = "sudo service tomcat7 status; ";
//        statusTomcat = new InstanceValues( status, runnerSSHKeyFile );
//
//        String password = "sudo sed -i 's/WtPgEXEwioa0LXUtqGwtkC4YUSgl2w4BF9VXsBviT/" + managerAppPassword
//                + "/g' /etc/tomcat7/tomcat-users.xml; ";
//        passwordTomcat = new InstanceValues( password, runnerSSHKeyFile );
//
//        String combined = stop + password + start + status;
//        stopPasswordStartStatusTomcat = new InstanceValues( combined, runnerSSHKeyFile );
//    }




    public String getProjectOutputJarPath() {
        return Utils.forceSlashOnDir( project.getBuild().getDirectory() ) + project.getBuild().getFinalName() +
                "." + project.getPackaging();
    }


    public String getProjectTestOutputJarPath() {
        return Utils.forceSlashOnDir( project.getBuild().getDirectory() ) + project.getBuild().getFinalName() +
                "-tests.jar";
    }


    /** @return Returns the project base directory with a '/' at the end */
    public String getProjectBaseDirectory() {
        return Utils.forceSlashOnDir( project.getBasedir().getAbsolutePath() );
    }


    /** @return Returns the extracted path of RUNNER_WAR file with a '/' at the end */
    public String getExtractedWarRootPath() {
        return getProjectBaseDirectory() + "target/runner/";
    }


    /** @return Returns the full path of created runner.war file */
    public String getWarToUploadPath() {
        String projectBaseDirectory = Utils.forceNoSlashOnDir( project.getBasedir().getAbsolutePath() );

        return projectBaseDirectory + "/target/" + RUNNER_WAR;
    }


    /** @return Returns the full path of created project.json file */
    public String getProjectFileToUploadPath() {
        return getExtractedWarRootPath() + "WEB-INF/classes/" + PROJECT_FILE;
    }


    /**
     * @return Returns the file path of runner.war file inside the S3 bucket, using the current last commit uuid; S3
     *         bucketName is not included in the returned String
     */
    public String getWarOnS3Path() throws MojoExecutionException {
        return destinationParentDir + getShortUuid() + "/" + RUNNER_WAR;
    }


    private String getShortUuid() throws MojoExecutionException {
        String uuid = Utils.getLastCommitUuid( Utils.getGitConfigFolder( project.getBasedir().getParent() ) );
        return uuid.substring( 0, CHARS_OF_UUID/2 ) + uuid.substring( uuid.length() - CHARS_OF_UUID/2 );
    }


    /**
     * @return Returns the file path of project file inside the store, using the current last commit uuid; S3
     *         bucketName is not included in the returned String
     */
    public String getProjectFilePath() throws MojoExecutionException {
        return destinationParentDir + getShortUuid() + "/" + PROJECT_FILE;
    }


    /** @return Returns the full path of the original chop-runner war file inside the local maven repository */
    public String getServerWarPath() {
        String path = localRepository;
        Artifact perftestArtifact = plugin.getPluginArtifact();

        path += "/" + perftestArtifact.getGroupId().replace( '.', '/' ) + "/chop-runner/" +
                perftestArtifact.getVersion() + "/chop-runner-" + perftestArtifact.getVersion() + ".war";

        return path;
    }


    /**
     * Loads the project configuration data if available.
     *
     * @return the Project for this project or blow chunks
     * @throws MojoExecutionException the chunks we blow
     */
    public Project loadProjectConfiguration() throws MojoExecutionException {
        File projectFile = new File( getProjectFileToUploadPath() );
        if ( ! projectFile.exists() ) {
            getLog().warn( "It seems as though the project properties file " + projectFile
                    + " does not exist. Creating it and the war now." );
            WarMojo warMojo = new WarMojo( this );
            warMojo.execute();

            if ( projectFile.exists() ) {
                getLog().info( "War is generated and project file exists." );
            }
            else {
                throw new MojoExecutionException( "Failed to generate the project.properties." );
            }
        }

        // Load the project configuration from the file system
        Project project;
        try {
            Properties props = new Properties();
            props.load( new FileInputStream( projectFile ) );
            ProjectBuilder builder = new ProjectBuilder( props );
            project = builder.getProject();
        }
        catch ( Exception e ) {
            getLog().warn( "Error accessing project information from local filesystem: " + getProjectFileToUploadPath(),
                    e );
            throw new MojoExecutionException(
                    "Cannot access local file system based project information: " + getProjectFileToUploadPath(), e );
        }

        return project;
    }
}
