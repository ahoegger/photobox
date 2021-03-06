package ch.ahoegger.photobox.rest;

import javax.json.stream.JsonGenerator;
import javax.servlet.ServletConfig;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class PhotobookApplication extends ResourceConfig {

  @Context
  private ServletConfig config;

  public PhotobookApplication() {
    packages("ch.ahoegger.photobox.rest");
    register(JsonProcessingFeature.class);

//    register(MyObjectMapperProvider.class) // No need to register this provider if no special configuration is required.
//        .register(JacksonFeature.class);

    property(JsonGenerator.PRETTY_PRINTING, true);
  }

}
