package org.safehaus.chop.plugin;


import java.util.List;

import org.safehaus.chop.client.ConfigKeys;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;


/**
 * This is the parent class for all Perftest plugin goal classes, takes the configuration parameters from caller
 * module's pom and provides extended get methods for several file paths that will be used by extended classes
 */
public class MainMojo extends AbstractMojo implements ConfigKeys {


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
     * config.properties file inside the WAR to be uploaded
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
    @Parameter( property = "destinationParentDir", defaultValue = CONFIGS_PATH + "/" )
    protected String destinationParentDir;


    /**
     * Container's (probably Tomcat) Manager user name. This parameter will be put to the config.properties file inside
     * the WAR to be uploaded
     */
    @Parameter( property = "managerAppUsername", required = true )
    protected String managerAppUsername;


    /**
     * Container's (probably Tomcat) Manager user name. This parameter will be put to the config.properties file inside
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


    @Parameter( property = "minimumRunners", defaultValue = "1" )
    protected Integer minimumRunners;


    @Parameter( property = "maximumRunners", defaultValue = "10" )
    protected Integer maximumRunners;


    @Parameter( property = "securityGroupExceptions" )
    protected List securityGroupExceptions;


    @Override
    public void execute() throws MojoExecutionException {
    }


    protected MainMojo( MainMojo mojo ) {
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
        this.plugin = mojo.plugin;
        this.project = mojo.project;
    }


    protected MainMojo() {

    }


    public String getProjectOutputJarPath() {
        return Utils.forceSlashOnDir( project.getBuild().getDirectory() ) + project.getBuild().getFinalName() +
                "." + project.getPackaging();
    }


    /** @return Returns the project base directory with a '/' at the end */
    public String getProjectBaseDirectory() {
        return Utils.forceSlashOnDir( project.getBasedir().getAbsolutePath() );
    }


    /** @return Returns the extracted path of RUNNER_WAR file with a '/' at the end */
    public String getExtractedWarRootPath() {
        return getProjectBaseDirectory() + "target/runner/";
    }


    /** @return Returns the full path of created perftest.war file */
    public String getWarToUploadPath() {
        String projectBaseDirectory = Utils.forceNoSlashOnDir( project.getBasedir().getAbsolutePath() );

        return projectBaseDirectory + "/target/runner.war";
    }


    /** @return Returns the full path of created perftest.war file */
    public String getProjectFileToUploadPath() {
        String projectBaseDirectory = Utils.forceNoSlashOnDir( project.getBasedir().getAbsolutePath() );

        return projectBaseDirectory + "/target/" + PROJECT_FILE;
    }


    /**
     * @return Returns the file path of perftest.war file inside the S3 bucket, using the current last commit uuid; S3
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


    /** @return Returns the full path of the original perftest-runner war file inside the local maven repository */
    public String getServerWarPath() {
        String path = localRepository;
        Artifact perftestArtifact = plugin.getPluginArtifact();

        path += "/" + perftestArtifact.getGroupId().replace( '.', '/' ) + "/chop-runner/" +
                perftestArtifact.getVersion() + "/chop-runner-" + perftestArtifact.getVersion() + ".war";

        return path;
    }
}