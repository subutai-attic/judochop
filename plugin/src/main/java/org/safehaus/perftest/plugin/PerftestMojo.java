package org.safehaus.perftest.plugin;


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
public abstract class PerftestMojo extends AbstractMojo {


    @Parameter( defaultValue = "${project}", readonly = true )
    protected MavenProject project;


    @Parameter( defaultValue = "${plugin}", readonly = true )
    protected PluginDescriptor plugin;


    @Parameter(  defaultValue = "${settings.localRepository}" )
    protected String localRepository;


    /**
     * Leaving this parameter with the default 'true' value causes the plugin goal to fail when there are modified
     * sources in the local git repository.
     */
    @Parameter( property = "failIfCommitNecessary", defaultValue = "true" )
    protected boolean failIfCommitNecessary;


    /**
     * This parameter is written to the config.properties file in the created WAR and used by the runner at runtime
     */
    @Parameter( property = "perftestFormation", required = true )
    protected String perftestFormation;


    /**
     * Fully qualified CN property of the app once it's deployed to its container. This parameter will be put to the
     * config.properties file inside the WAR to be uploaded
     */
    @Parameter( property = "testModuleFQCN", required = true )
    protected String testModuleFQCN;


    /**
     * The bucket to upload into
     */
    @Parameter( property = "bucketName", required = true )
    protected String bucketName;


    /**
     * Access key for S3
     */
    @Parameter( property = "accessKey", required = true )
    protected String accessKey;


    /**
     * Secret key for S3
     */
    @Parameter( property = "secretKey", required = true)
    protected String secretKey;


    /**
     * sourceFile will be uploaded as $destinationParentDir$commitUUID/perftest.war in S3 bucket
     *
     * defaultValue is "tests/"
     */
    @Parameter( property = "destinationParentDir", defaultValue = "tests/" )
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


    @Override
    public abstract void execute() throws MojoExecutionException;


    public String getProjectOutputJarPath() {
        return PerftestUtils.forceSlashOnDir( project.getBuild().getDirectory() ) + project.getBuild().getFinalName() +
                "." + project.getPackaging();
    }


    /**
     * @return Returns the project base directory with a '/' at the end
     */
    public String getProjectBaseDirectory() {
        return PerftestUtils.forceSlashOnDir( project.getBasedir().getAbsolutePath() );
    }


    /**
     * @return Returns the extracted path of perftest-webapp.war file with a '/' at the end
     */
    public String getExtractedWarRootPath() {
        return getProjectBaseDirectory() + "target/perftest/";
    }


    /**
     * @return Returns the full path of created perftest.war file
     */
    public String getWarToUploadPath() {
        String projectBaseDirectory = PerftestUtils.forceNoSlashOnDir( project.getBasedir().getAbsolutePath() );

        return projectBaseDirectory + "/target/perftest.war";
    }


    /**
     * @return Returns the full path of created perftest.war file
     */
    public String getTestInfoToUploadPath() {
        String projectBaseDirectory = PerftestUtils.forceNoSlashOnDir( project.getBasedir().getAbsolutePath() );

        return projectBaseDirectory + "/target/test-info.json";
    }


    /**
     * @return Returns the file path of perftest.war file inside the S3 bucket, using the current last commit uuid;
     *         S3 bucketName is not included in the returned String
     * @throws MojoExecutionException
     */
    public String getWarOnS3Path() throws MojoExecutionException {
        return destinationParentDir + PerftestUtils.getLastCommitUuid( PerftestUtils
                .getGitConfigFolder( project.getBasedir().getParent() ) ) + "/perftest.war";
    }


    /**
     * @return Returns the file path of test-info.json file inside the S3 bucket, using the current last commit uuid;
     *         S3 bucketName is not included in the returned String
     * @throws MojoExecutionException
     */
    public String getTestInfoOnS3Path() throws MojoExecutionException {
        return destinationParentDir + PerftestUtils.getLastCommitUuid( PerftestUtils
                .getGitConfigFolder( project.getBasedir().getParent() ) ) + "/test-info.json";
    }


    /**
     * @return Returns the full path of the original perftest-webapp war file inside the local maven repository
     */
    public String getWebappWarPath() {
        String path = localRepository;
        Artifact perftestArtifact = plugin.getPluginArtifact();

        path += "/" + perftestArtifact.getGroupId().replace( '.', '/' ) + "/perftest-webapp/" +
                perftestArtifact.getVersion() + "/perftest-webapp-" + perftestArtifact.getVersion() + ".war";

        return path;
    }


}
