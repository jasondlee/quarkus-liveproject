package io.chillplus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/tv")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TvShowResource {
    private AtomicLong sequence = new AtomicLong();
    private Map<Long, TvShow> shows = new HashMap<>();

    @GET
    public Collection<TvShow> getAll() {
       return shows.values();
    }

    @POST
    public Response create(TvShow show) {
        if (show.getId() != null) {
            throw new BadRequestException();
        }
        if (show.getTitle() == null || show.getTitle().isEmpty()) {
            throw new BadRequestException();
        }
        show.setId(sequence.getAndIncrement());
        shows.put(show.getId(), show);

        return Response.status(Response.Status.CREATED)
                .entity(show)
                .build();
    }

    @GET
    @Path("{id}")
    public TvShow getOneById(@PathParam("id") Long id) {
        TvShow show = shows.get(id);
        if (show == null) {
            throw new NotFoundException();
        }
        return show;
    }

    @DELETE
    @Path("{id}")
    public Response deleteOne(@PathParam("id") Long id) {
        shows.remove(id);
        return Response.ok().build();
    }

    @DELETE
    public Response deleteAll() {
        shows.clear();
        return Response.ok().build();
    }
}
