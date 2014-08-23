package ru.vyarus.dropwizard.orient.support

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

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
