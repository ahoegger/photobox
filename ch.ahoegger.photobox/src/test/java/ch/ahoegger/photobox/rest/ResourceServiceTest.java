package ch.ahoegger.photobox.rest;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ResourceServiceTest extends JerseyTest {

  @Override
  protected Application configure() {
    return new ResourceConfig(ApiFolderService.class);
  }

  @Test
  public void test() {
//    final String json = target("/api").request().get(String.class);
//    Resource resource = new Gson().fromJson(json, Resource.class);
//    Assert.assertEquals("root", resource.getName());
//    Assert.assertEquals(1, resource.getLinks().size());
  }
}
