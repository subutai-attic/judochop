package org.safehaus.chop.api;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


/**
 * A Jackson JSON Deserializer for Project.
 */
public class RunnerDeserializer extends JsonDeserializer<Runner> {
    private static final Logger LOG = LoggerFactory.getLogger( RunnerDeserializer.class );


    @Override
    public Runner deserialize( final JsonParser jp, final DeserializationContext ctxt ) throws IOException {
        RunnerBuilder builder = new RunnerBuilder();

        String tmp = jp.getText();
        validate( jp, tmp, "{" );
        LOG.debug( "First token is {}", tmp );

        jp.nextToken();
        tmp = jp.getText();
        LOG.debug( "Second token is {}", tmp );

        while( jp.hasCurrentToken() ) {

            tmp = jp.getText();
            LOG.debug( "Current token text = {}", tmp );

            if ( tmp.equals( "ipv4Address" ) ) {
                jp.nextToken();
                builder.setIpv4Address( jp.getText() );
            }
            else if ( tmp.equals( "hostname" ) ) {
                jp.nextToken();
                builder.setHostname( jp.getText() );
            }
            else if ( tmp.equals( "url" ) ) {
                jp.nextToken();
                builder.setUrl( jp.getText() );
            }
            else if ( tmp.equals( "serverPort" ) ) {
                jp.nextToken();
                builder.setServerPort( jp.getValueAsInt() );
            }
            else if ( tmp.equals( "tempDir" ) ) {
                jp.nextToken();
                builder.setTempDir( jp.getText() );
            }

            jp.nextToken();

            if ( jp.getText().equals( "}" ) ) {
                break;
            }
        }

        return builder.getRunner();
    }


    private void validate( JsonParser jsonParser, String input, String expected ) throws JsonProcessingException {
        if ( ! input.equals( expected ) ) {
            throw new JsonParseException( "Unexpected token: " + input, jsonParser.getTokenLocation() );
        }
    }
}
