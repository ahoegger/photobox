package ch.ahoegger.photobox.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h3>{@link EnsureAuthenticatedController}</h3>
 *
 * @author aho
 */
public class EnsureAuthenticatedController implements IAccessControler {

  @Override
  public boolean handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
    switch (getTarget(request)) {
      case "/login":
        forwardTo(request, response, "/login.html");
        return true;
      case "/logout":
        forwardTo(request, response, "/logout.html");
        return true;
      case "/auth":
        return false;
      default:
        return handleRequest(request, response, chain);
    }

  }

  protected boolean handleRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
    return true;
  }

  protected void forwardTo(HttpServletRequest request, HttpServletResponse response, String targetLocation) throws ServletException, IOException {
    request.getRequestDispatcher(targetLocation).forward(request, response);
  }

  protected String getTarget(final HttpServletRequest request) {
    final String pathInfo = request.getPathInfo();
    if (pathInfo != null) {
      return pathInfo;
    }

    final String requestURI = request.getRequestURI();
    return requestURI.substring(requestURI.lastIndexOf('/'));
  }

}
