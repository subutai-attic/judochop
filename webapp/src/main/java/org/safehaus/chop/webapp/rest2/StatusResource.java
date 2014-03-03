package org.safehaus.chop.webapp.rest2;

import com.google.inject.Singleton;
import org.safehaus.chop.api.BaseResult;
import org.safehaus.chop.api.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Singleton
@Produces( MediaType.APPLICATION_JSON )
@Path("/status")
public class StatusResource {

    private final static Logger log = LoggerFactory.getLogger(StatusResource.class);

    @GET
    public BaseResult status() {
        return new BaseResult("/status-point", false, "Status Testing message", State.INACTIVE);
    }
}
