package org.safehaus.chop.api;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;


/**
 * Custom serializer for ProjectFigs.
 */
public class RunnerSerializer extends JsonSerializer<Runner> {

    @Override
    public void serialize( final Runner value, final JsonGenerator jgen, final SerializerProvider provider )
            throws IOException {
        jgen.writeStartObject();

        jgen.writeStringField( "ipv4Address", value.getIpv4Address() );

        jgen.writeStringField( "hostname", value.getHostname() );

        jgen.writeStringField( "url", value.getUrl() );

        jgen.writeStringField( "serverPort", String.valueOf( value.getServerPort() ) );

        jgen.writeStringField( "tempDir", value.getTempDir() );

        jgen.writeEndObject();
    }
}
