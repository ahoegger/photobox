package ch.ahoegger.photobox.rest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.DbPicture;

@Singleton
@Path("/picture/{id}")
public class PictureService {

  @Context
  private ServletConfig config;
  private java.nio.file.Path m_workingDirectory;

  @PostConstruct
  protected void init() {

    // working directory
    String workingDirectoryName = config.getServletContext().getInitParameter(IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
    if (workingDirectoryName == null || workingDirectoryName.trim().length() == 0) {
      throw new IllegalArgumentException("Working directory is not defined.");
    }
    m_workingDirectory = Paths.get(workingDirectoryName);
    if (!Files.isDirectory(m_workingDirectory)) {
      throw new IllegalArgumentException(String.format("Working directory '%s' does not exist.", workingDirectoryName));
    }
  }

  @GET
  @Produces("image/jpg")
  public Response getFile(@PathParam("id") String idRaw, @QueryParam("size") String sizeRaw) {
    long id = Long.parseLong(idRaw);
    Picture picture = DbPicture.findById(id);
    if (picture == null) {
      return null;
    }
    final java.nio.file.Path picturePath;
    if ("s".equalsIgnoreCase(sizeRaw)) {
      picturePath = m_workingDirectory.resolve(picture.getPathSmall());
    }
    else if ("l".equalsIgnoreCase(sizeRaw)) {
      picturePath = m_workingDirectory.resolve(picture.getPathLarge());
    }
    else {
      picturePath = m_workingDirectory.resolve(picture.getPathMedium());
    }
    if (!Files.exists(picturePath) || Files.isDirectory(picturePath)) {
      return Response.status(Status.NOT_FOUND).build();
    }

    ResponseBuilder response = Response.ok(picturePath.toFile());
    Calendar c = Calendar.getInstance();
    c.add(Calendar.YEAR, 1);

    CacheControl cc = new CacheControl();
    cc.setPrivate(true);
    cc.setMaxAge(10000);
    response.cacheControl(cc);

    response.header("Content-Disposition", "attachment; filename=" + picturePath);
    return response.build();

  }
}
