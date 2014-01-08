package org.safehaus.chop.api;


import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests serialization / de-serialization of ProjectFig.
 */
public class ProjectFigSerDeserTest {
    public static final String EXAMPLE = "{\"testPackageBase\":\"org.safehaus.chop.example\",\"chopVersion\":\"1.0-SNAPSHOT\"," +
            "\"createTimestamp\":\"2013.12.24.06.14.22\",\"vcsVersion\":\"3d2ccc1b2b96a45936e58b782c4c2d5f8c1ba76e\"," +
            "\"vcsRepoUrl\":\"https://jim.rybacki@stash.safehaus.org/scm/chop/main.git\"," +
            "\"groupId\":\"org.safehaus.chop\",\"artifactId\":\"chop-example\",\"projectVersion\":\"1.0-SNAPSHOT\"," +
            "\"warMd5\":\"1f31add62c0f1da0eb5aa74d4c441e2c\"," +
            "\"loadKey\":\"tests/3d2ccc1b2b96a45936e58b782c4c2d5f8c1ba76e/runner.war\"," +
            "\"loadTime\":\"2013.12.24.06.14.24\"}";

    @Test
    public void testBoth() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProjectFig value = mapper.readValue( EXAMPLE, ProjectFig.class );
        assertEquals( "org.safehaus.chop.example", value.getTestPackageBase() );
        assertEquals( "1.0-SNAPSHOT", value.getChopVersion() );
        assertEquals( "2013.12.24.06.14.22", value.getCreateTimestamp() );
        assertEquals( "3d2ccc1b2b96a45936e58b782c4c2d5f8c1ba76e", value.getVcsVersion() );
        assertEquals( "org.safehaus.chop", value.getGroupId() );
        assertEquals( "chop-example", value.getArtifactId() );
        assertEquals( "1.0-SNAPSHOT", value.getVersion() );
        assertEquals( "1f31add62c0f1da0eb5aa74d4c441e2c", value.getWarMd5() );
        assertEquals( "tests/3d2ccc1b2b96a45936e58b782c4c2d5f8c1ba76e/runner.war", value.getLoadKey() );
        assertEquals( "2013.12.24.06.14.24", value.getLoadTime() );

        String serialized = mapper.writeValueAsString( value );
        assertEquals( EXAMPLE, serialized );
    }
}
