package org.apache.usergrid.chop.plugin;


import org.apache.usergrid.chop.plugin.Utils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.apache.maven.plugin.MojoExecutionException;


public class MainMojoTest {
    @Test
    @Ignore
    public void testGetS3Client() {

    }


    @Test
    @Ignore
    public void testExecute() {

    }


    @Test
    @Ignore
    public void testExtractWar() {

    }


    @Test
    @Ignore
    public void testGetGitRemoteUrl() {

    }


    @Test
    @Ignore
    public void testIsCommitNecessary() {

    }


    @Test
    public void testGetMD5() {
        String lastCommitUUID = "6807ae52eb67cf7c1a8d44e1ca291e23845595c7";
        String timestamp = "2013.12.08.01.52.51";

        try {
            String md5 = Utils.getMD5(timestamp, lastCommitUUID);
            Assert.assertEquals( "MD5 generator is not working properly", md5, "81a28bb20ca1f89ed41ce5c861fb7222" );
        }
        catch ( MojoExecutionException e ) {
            Assert.fail( e.getMessage() );
        }
    }
}