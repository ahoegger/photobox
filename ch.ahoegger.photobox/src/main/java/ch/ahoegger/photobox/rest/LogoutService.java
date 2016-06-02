package ch.ahoegger.photobox.rest;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * <h3>{@link LogoutService}</h3>
 *
 * @author aho
 */
@Singleton
@Path("/logout")
public class LogoutService {

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response logout(@Context HttpServletRequest request) {
    request.getSession().invalidate();
    return Response.status(204).build();
  }
}
