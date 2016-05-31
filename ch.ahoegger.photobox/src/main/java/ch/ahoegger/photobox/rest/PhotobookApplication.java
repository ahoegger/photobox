package ch.ahoegger.photobox.rest;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.json.stream.JsonGenerator;
import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;

import ch.ahoegger.photobox.db.DbAccess;

@ApplicationPath("/")
public class PhotobookApplication extends ResourceConfig {

  @Context
  private ServletConfig config;
  private File pictureDirectory;

  public PhotobookApplication() {
    packages("ch.ahoegger.photobox.rest");
    System.out.println("APP START");
    register(JsonProcessingFeature.class);

//    register(MyObjectMapperProvider.class) // No need to register this provider if no special configuration is required.
//        .register(JacksonFeature.class);

    property(JsonGenerator.PRETTY_PRINTING, true);
  }

  @PostConstruct
  protected void init() {
    // pircureDirectory
    DbAccess.setDbLocation(config.getServletContext().getInitParameter("ch.ahoegger.picturebox.dbLocation"));
    System.out.println("APP INIT: " + config.getInitParameter("ch.ahoegger.pictureDirectory"));
    setPictureDirectory(new File(config.getInitParameter("ch.ahoegger.pictureDirectory")));

  }

  public File getPictureDirectory() {
    return pictureDirectory;
  }

  private void setPictureDirectory(File pictureDirectory) {
    this.pictureDirectory = pictureDirectory;
  }

  // @Override
  // public Set<Class<?>> getClasses() {
  // final Set<Class<?>> classes = new HashSet<Class<?>>();
  // // register root resource
  // // classes.add(ResourceService.class);
  // classes.add(PictureService.class);
  // return classes;
  // }
  //
  // @Override
  // public Set<Object> getSingletons() {
  // Set<Object> singletons = new HashSet<Object>();
  // singletons.add(new ResourceService());
  // return singletons;
  // }
}
