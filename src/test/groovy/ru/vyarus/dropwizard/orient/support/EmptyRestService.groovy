package ru.vyarus.dropwizard.orient.support

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Response

/**
 * @author Vyacheslav Rusakov 
 * @since 23.08.2014
 */
@Path("/dummy")
@Produces('application/json')
class EmptyRestService {

    @GET
    @Path("/")
    public Response latest() {
        return Response.ok().build();
    }
}
