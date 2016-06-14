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

import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.db.DbFolder;

/**
 * <h3>{@link FolderService}</h3>
 *
 * @author aho
 */
@Singleton
@Path("/folder")
public class FolderService {

  @POST
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response storePicture(@PathParam("id") String folderIdRaw, Folder folder) throws IOException {
    long id = 0L;
    if (folderIdRaw != null) {
      id = Long.parseLong(folderIdRaw);
    }
    DbFolder.update(folder.withId(id));

    return Response.status(204).build();
  }

}
