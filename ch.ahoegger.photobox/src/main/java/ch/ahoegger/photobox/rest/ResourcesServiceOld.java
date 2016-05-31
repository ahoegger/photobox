package ch.ahoegger.photobox.rest;
//package ch.ahoegger.photobook.rest;
//
//import java.io.File;
//import java.util.List;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.ServletConfig;
//import javax.ws.rs.GET;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.WebApplicationException;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.PathSegment;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.UriInfo;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import ch.ahoegger.photobook.ImageFileDesc;
//import ch.ahoegger.photobook.PhotoUtility;
//import ch.ahoegger.photobook.dao.FolderResource;
//import ch.ahoegger.photobook.dao.PictureResource;
//import ch.ahoegger.photobook.dao.Resource;
//import ch.ahoegger.photobook.dao.Resources;
//
////@Path("/resources/{resourcePath: [\\-a-zA-Z0-9\\_\\/]*}")
//public class ResourcesServiceOld {
//
//  public static final Logger LOG = LogManager.getLogger(ResourcesServiceOld.class);
//  @Context
//  private ServletConfig config;
//  @Context
//  private UriInfo uriInfo;
//
//  private File pictureDirectory;
//
//  @PostConstruct
//  protected void init() {
//    // pircureDirectory
//    setPictureDirectory(new File(config.getInitParameter("pictureDirectory")));
//
//  }
//
//  @GET
//  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, })
//  public Resources getResources(@QueryParam("id") String description) {
//    return getResourcesInternal();
//  }
//
//  protected Resources getResourcesInternal() {
//    List<PathSegment> pathSegments = uriInfo.getPathSegments();
//    StringBuilder fileNameBuilder = new StringBuilder(pictureDirectory.getAbsolutePath());
//    if (pathSegments.size() > 1) {
//      for (int i = 1; i < pathSegments.size(); i++) {
//        fileNameBuilder.append("/").append(pathSegments.get(i));
//      }
//    }
//    File rootFile = new File(fileNameBuilder.toString());
//    if (!rootFile.exists()) {
//      throw new WebApplicationException("Resource '" + uriInfo.getPath() + "' could not be found [0].", Response.Status.NOT_FOUND);
//    } else if (!rootFile.isDirectory()) {
//      throw new WebApplicationException("Resource '" + uriInfo.getPath() + "' could not be found [1].", Response.Status.NOT_FOUND);
//    }
//    Resources resources = new Resources(PhotoUtility.stripFileName(pictureDirectory, rootFile));
//    if (rootFile.isDirectory()) {
//      File[] listFiles = rootFile.listFiles();
//      for (int i = 0; i < listFiles.length; i++) {
//        File f = listFiles[i];
//        Resource resource = null;
//        if (f.isDirectory()) {
//          resource = new FolderResource(f.getName(), PhotoUtility.stripFileName(pictureDirectory, f));
//          resources.addFolder((FolderResource) resource);
//        } else {
//          try {
//            ImageFileDesc desc = new ImageFileDesc(PhotoUtility.stripFileName(pictureDirectory, f));
//            resource = new PictureResource(desc);
//            resources.addPicture((PictureResource) resource);
//          } catch (IllegalArgumentException e) {
//            LOG.error(e);
//          }
//        }
//
//      }
//    } else {
//      try {
//        ImageFileDesc desc = new ImageFileDesc(PhotoUtility.stripFileName(pictureDirectory, rootFile));
//        PictureResource picture = new PictureResource(desc);
//
//        resources.addPicture(picture);
//      } catch (IllegalArgumentException e) {
//        LOG.error(e);
//      }
//    }
//    return resources;
//  }
//
//  public File getPictureDirectory() {
//    return pictureDirectory;
//  }
//
//  public void setPictureDirectory(File pictureDirectory) {
//    this.pictureDirectory = pictureDirectory;
//  }
// }
