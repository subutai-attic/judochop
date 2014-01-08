package org.safehaus.chop.api;


import java.io.IOException;

import org.safehaus.guicyfig.GuicyFigModule;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.inject.Guice;


/**
 * A Jackson JSON Deserializer for ProjectFig.
 */
public class ProjectDeserializer extends JsonDeserializer<ProjectFig> {
    @Override
    public ProjectFig deserialize( final JsonParser jp, final DeserializationContext ctxt )
            throws IOException, JsonProcessingException {
        ProjectFig projectFig = Guice.createInjector(
                new GuicyFigModule( ProjectFig.class ) ).getInstance( ProjectFig.class );


        return projectFig;
    }
}
