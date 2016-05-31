package ch.ahoegger.photobox.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SimpleServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  protected Logger LOG = LogManager.getLogger(SimpleServlet.class);

  @Override
  public void init() throws ServletException {
    String workingDirectory = getInitParameter("workingDirectory");
    String pictureDirectory = getInitParameter("pictureDirectory");
    if (LOG.isInfoEnabled()) {
      LOG.info("working directory: " + workingDirectory);
      LOG.info("picture directory: " + pictureDirectory);
    }
    // String relPath = getServletContext().getRealPath("/loader.gif");
    // loaderGif = new File(relPath);

  }

  public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
    response.getWriter().write("<html><body>You said yes!</body></html>");
    // do something in here
  }
}