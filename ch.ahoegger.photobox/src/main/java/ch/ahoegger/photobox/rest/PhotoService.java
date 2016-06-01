package ch.ahoegger.photobox.rest;

import java.io.IOException;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.DbPicture;

/**
 * <h3>{@link PhotoService}</h3>
 *
 * @author aho
 */
@Singleton
@Path("/photo")
public class PhotoService {

  @POST
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response newTodo(@PathParam("id") String imageIdRaw, Picture picture) throws IOException {
    long id = 0L;
    if (imageIdRaw != null) {
      id = Long.parseLong(imageIdRaw);
    }
    DbPicture.update(picture.withId(id));

    return Response.status(204).build();
  }

}
