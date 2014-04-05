package org.safehaus.chop.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/** Result interface from operations against runner API. */
@JsonDeserialize( as = BaseResult.class )
public interface Result {

    /**
     * True for success false otherwise.
     *
     * @return true if success, false otherwise
     */
    @JsonProperty
    boolean getStatus();

    /**
     * Gets the present state of the runner after an operation.
     *
     * @return the current state of the runner
     */
    @JsonProperty
    State getState();

    /**
     * Optional message response.
     *
     * @return a message if required, otherwise null
     */
    @JsonProperty
    String getMessage();

    /**
     * The full URL for the end point of the operation performed.
     *
     * @return the full URL for the end point
     */
    @JsonProperty
    String getEndpoint();


    @JsonProperty
    Project getProject();
}
