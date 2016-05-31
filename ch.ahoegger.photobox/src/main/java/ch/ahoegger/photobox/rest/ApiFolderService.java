package ch.ahoegger.photobox.rest;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.db.DbFolder;
import ch.ahoegger.photobox.db.DbPicture;

/**
 * <h3>{@link ApiFolderService}</h3>
 *
 * @author aho
 */
@Singleton
@Path("/api")
public class ApiFolderService {

  public ApiFolderService() {
    System.out.println("API FOLDER SERVICE CONST");
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  public Folder getRoot() {
    return get(null);
  }

  @GET
  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
  @Path("/{folder}")
  public Folder get(@PathParam("folder") String folderIdRaw) {
    long id = 0L;
    if (folderIdRaw != null) {
      id = Long.parseLong(folderIdRaw);
    }
    Folder folder = null;
    if (id == 0l) {
      folder = new Folder().withId(0L).withActive(true);
    }
    else {
      folder = DbFolder.findById(id);
    }
    if (folder == null) {
      return null;
    }

    // load parent
    if (folder.getParentId() != null) {
      if (folder.getParentId() == 0l) {
        folder.setParent(IProperties.ROOT_FOLDER);
      }
      else {
        folder.setParent(DbFolder.findById(folder.getParentId()));
      }
    }

    // load subfolders
    folder.setSubFolders(DbFolder.findByParentId(folder.getId(), Boolean.TRUE));
    folder.setPictures(DbPicture.findByParentId(folder.getId(), Boolean.TRUE));
    return folder;

  }
}
