package ch.ahoegger.photobox.rest;
//package ch.ahoegger.photobook.rest;
//
//import java.io.IOException;
//import java.nio.file.DirectoryStream;
//import java.nio.file.DirectoryStream.Filter;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//
//import javax.annotation.PostConstruct;
//import javax.inject.Singleton;
//import javax.servlet.ServletConfig;
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.message.ParameterizedMessage;
//
//import ch.ahoegger.photobook.IProperties;
//import ch.ahoegger.photobook.PhotoUtility;
//import ch.ahoegger.photobook.dao.Link;
//import ch.ahoegger.photobook.dao.Resource;
//
///**
// * url: /rest/api[?path=filePath]?
// *
// * @author aho
// */
//@Singleton
//@Path("/apiold")
//// /{resourcePath: [\\-a-zA-Z0-9\\_\\/]*}")
//public class ResourceService {
//
//  protected static Logger LOG = LogManager.getLogger(ResourceService.class);
//
//  private static Filter<java.nio.file.Path> DIRECTORY_FILTER = new DirectoryStream.Filter<java.nio.file.Path>() {
//    @Override
//    public boolean accept(java.nio.file.Path entry) throws IOException {
//      return Files.isDirectory(entry);
//    }
//  };
//
//  @Context
//  private ServletConfig config;
//  private java.nio.file.Path m_workingDirectory;
//  private java.nio.file.Path m_previewDirectory; // workingDir/preview
//  private java.nio.file.Path m_desktopDirectory; // workingDir/desktop
//  private java.nio.file.Path m_mobileDirectory; // workingDir/mobile
//
//  @PostConstruct
//  protected void init() {
//    // working directory
//    String workingDirectoryName = config.getServletContext().getInitParameter(IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
//    if (workingDirectoryName == null || workingDirectoryName.trim().length() == 0) {
//      throw new IllegalArgumentException("Working directory is not defined.");
//    }
//    m_workingDirectory = Paths.get(workingDirectoryName);
//    if (!Files.isDirectory(m_workingDirectory)) {
//      throw new IllegalArgumentException(String.format("Working directory '%s' does not exist.", workingDirectoryName));
//    }
//    m_previewDirectory = m_workingDirectory.resolve(IProperties.ImageType.Preview.toString());
//    m_desktopDirectory = m_workingDirectory.resolve(IProperties.ImageType.Desktop.toString());
//    m_mobileDirectory = m_workingDirectory.resolve(IProperties.ImageType.Mobile.toString());
//
//  }
//
//  @GET
//  @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
//  public Resource getResource(@QueryParam("path") String path) {
//    if ("ROOT".equals(path)) {
//      path = null;
//    }
//    java.nio.file.Path requestedPath = m_desktopDirectory;
//    if (path != null && path.length() > 0) {
//      requestedPath = requestedPath.resolve(path);
//    }
//    if (!Files.exists(requestedPath)) {
//      LOG.warn("Could not find requested resource '{}'.", requestedPath);
//      return null;
//    }
//    try {
//      return createResource(requestedPath, true);
//    }
//    catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//      return null;
//    }
//  }
//
//  protected Resource createResource(java.nio.file.Path resourcePath, boolean loadChildren) throws IOException {
//    Resource resource = null;
//    if (Files.isDirectory(resourcePath)) {
//      resource = createDirectoryResource(resourcePath, loadChildren);
//    }
//    else if (Files.exists(resourcePath)) {
//      resource = createFileResource(resourcePath);
//    }
//    else {
//      LOG.warn("Requested resource '{}' does not exist.", resourcePath);
//    }
//    if (resource != null) {
//      if (resourcePath.getNameCount() > m_desktopDirectory.getNameCount()) {
//        resource.setPath(PhotoUtility.pathToString(m_desktopDirectory.relativize(resourcePath), "/"));
//        if (resourcePath.getParent().getNameCount() == m_desktopDirectory.getNameCount()) {
//          // java.nio.file.Path relativize =
//          // m_desktopDirectory.relativize(resourcePath.getParent());
//          resource.setParentPath("ROOT");
//        }
//        else {
//          resource.setParentPath(PhotoUtility.pathToString(m_desktopDirectory.relativize(resourcePath.getParent()), "/"));
//        }
//
//      }
//
//    }
//    return resource;
//  }
//
//  protected Resource createDirectoryResource(java.nio.file.Path path, boolean loadChildren) throws IOException {
//    Resource resource = new Resource(path.getFileName().toString(), true);
//    if (loadChildren) {
//      Files.newDirectoryStream(path, DIRECTORY_FILTER).forEach(p -> {
//        try {
//          Resource r = createResource(p, false);
//          if (r != null) {
//            resource.addChild(r);
//          }
//        }
//        catch (Exception e) {
//          LOG.warn(new ParameterizedMessage("Error create resource '{}'.", p), e);
//        }
//      });
//      // images
//      Files.newDirectoryStream(path, "*.{jpg,gif,png}").forEach(p -> {
//        try {
//          Resource r = createResource(p, false);
//          if (r != null) {
//            resource.addChild(r);
//          }
//        }
//        catch (Exception e) {
//          LOG.warn(new ParameterizedMessage("Error create resource '{}'.", p), e);
//        }
//      });
//      resource.setChildrenLoaded(true);
//    }
//    resource.addLink(new Link("api", "rest/api?path=" + PhotoUtility.pathToString(m_desktopDirectory.relativize(path), "/")));
//    return resource;
//
//  }
//
//  protected Resource createFileResource(java.nio.file.Path path) throws IOException {
//    Resource resource = new Resource(path.getFileName().toString(), false);
//    resource.setChildrenLoaded(true);
//    // add links
//    java.nio.file.Path previewPath = m_previewDirectory.resolve(m_desktopDirectory.relativize(path));
//    if (Files.exists(previewPath)) {
//      resource.addLink(new Link(IProperties.ImageType.Preview.toString(), "rest/image?path="
//          + PhotoUtility.pathToString(m_workingDirectory.relativize(previewPath), "/")));
//    }
//    java.nio.file.Path desktopPath = path;
//    if (Files.exists(desktopPath)) {
//      resource.addLink(new Link(IProperties.ImageType.Desktop.toString(), "rest/image?path="
//          + PhotoUtility.pathToString(m_workingDirectory.relativize(desktopPath), "/")));
//    }
//    java.nio.file.Path mobilePath = m_mobileDirectory.resolve(m_desktopDirectory.relativize(path));
//    if (Files.exists(mobilePath)) {
//      resource.addLink(new Link(IProperties.ImageType.Mobile.toString(), "rest/image?path="
//          + PhotoUtility.pathToString(m_workingDirectory.relativize(mobilePath), "/")));
//    }
//    // meta data
//
//    // try {
//    // Metadata metadata = ImageMetadataReader.readMetadata(path.toFile());
//    // for (Directory directory : metadata.getDirectories()) {
//    // for (Tag tag : directory.getTags()) {
//    // System.out.format("[%s] - %s = %s", directory.getName(),
//    // tag.getTagName(), tag.getDescription());
//    // }
//    // if (directory.hasErrors()) {
//    // for (String error : directory.getErrors()) {
//    // System.err.format("ERROR: %s", error);
//    // }
//    // }
//    // }
//    //
//    // ExifSubIFDDirectory directory =
//    // metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
//    //
//    // Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
//    // resource.setCapturedDate(date);
//    // } catch (ImageProcessingException e) {
//    // // TODO Auto-generated catch block
//    // e.printStackTrace();
//    // }
//
//    return resource;
//  }
//}
