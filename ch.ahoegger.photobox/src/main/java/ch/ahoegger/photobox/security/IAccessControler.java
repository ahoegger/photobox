package ch.ahoegger.photobox.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h3>{@link IAccessControler}</h3>
 *
 * @author aho
 */
public interface IAccessControler {

  /**
   * @param request
   * @param response
   * @param chain
   * @return
   * @throws IOException
   * @throws ServletException
   */
  boolean handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException;
}
