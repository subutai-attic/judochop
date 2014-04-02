package org.safehaus.chop.api;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/**
 * Custom serializer for ProjectFigs.
 */
public class ProjectSerializer extends JsonSerializer<Project> {

    @Override
    public void serialize( final Project value, final JsonGenerator jgen, final SerializerProvider provider )
            throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField( "testPackageBase", value.getTestPackageBase() );

        jgen.writeStringField( "chopVersion", value.getChopVersion() );

        jgen.writeStringField( "createTimestamp", value.getCreateTimestamp() );

        jgen.writeStringField( "vcsVersion", value.getVcsVersion() );

        jgen.writeStringField( "vcsRepoUrl", value.getVcsRepoUrl() );

        jgen.writeStringField( "groupId", value.getGroupId() );

        jgen.writeStringField( "artifactId", value.getArtifactId() );

        jgen.writeStringField( "projectVersion", value.getVersion() );

        jgen.writeStringField( "md5", value.getMd5() );

        jgen.writeStringField( "loadKey", value.getLoadKey() );

        jgen.writeStringField( "loadTime", value.getLoadTime() );

        jgen.writeEndObject();
    }
}
