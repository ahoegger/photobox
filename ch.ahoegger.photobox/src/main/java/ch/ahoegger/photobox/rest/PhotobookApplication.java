package ch.ahoegger.photobox.rest;

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

  }

}
