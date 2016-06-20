package ch.ahoegger.photobox.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.dao.Picture;
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
    boolean sortAsc = true;
    long id = 0L;
    if (folderIdRaw != null) {
      id = Long.parseLong(folderIdRaw);
    }
    Folder folder = null;
    if (id == 0l) {
      sortAsc = false;
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
    List<Folder> subfolders = DbFolder.findByParentId(folder.getId(), Boolean.TRUE, sortAsc);
    subfolders.forEach(subFolder -> {
      List<Long> pictureIds = DbPicture.getPictureIds(subFolder.getId(), true);
      subFolder.setPreviewPictures(createPreviewPictures(pictureIds));
      subFolder.setTotalPictureCount(pictureIds.size());
    });

    folder.setSubFolders(subfolders);
    folder.setPictures(DbPicture.findByParentId(folder.getId(), Boolean.TRUE));
    // preview pictures
    List<Long> pictureIds = DbPicture.getPictureIds(folder.getId(), true);
    folder.setPreviewPictures(createPreviewPictures(pictureIds));
    folder.setTotalPictureCount(pictureIds.size());
    return folder;
  }

  private List<Picture> createPreviewPictures(List<Long> allPictureIds) {
    if (allPictureIds.size() <= 5) {
      return DbPicture.findByIds(allPictureIds);
    }
    List<Long> previewIds = new ArrayList<Long>(5);
    if (allPictureIds.size() > 100) {
      while (previewIds.size() < 5) {
        Random random = new Random();
        Long nextId = allPictureIds.get(random.nextInt(allPictureIds.size()));
        if (!previewIds.contains(nextId)) {
          previewIds.add(nextId);
        }
      }

    }
    else {
      List<Long> copy = new ArrayList<Long>(allPictureIds);
      while (previewIds.size() < 5 && copy.size() > 0) {
        Random random = new Random();
        previewIds.add(copy.remove(random.nextInt(copy.size())));
      }
    }
    return DbPicture.findByIds(previewIds);
  }
}
