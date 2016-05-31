package ch.ahoegger.photobox.servlet;
//package ch.ahoegger.photobook.servlet;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.OutputStream;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.glassfish.grizzly.filterchain.Filter;
//import org.glassfish.grizzly.filterchain.FilterChain;
//import org.glassfish.grizzly.filterchain.FilterChainContext;
//import org.glassfish.grizzly.http.server.FileCacheFilter;
//import org.glassfish.grizzly.http.server.Request;
//import org.glassfish.grizzly.http.server.filecache.FileCache;
//import org.glassfish.grizzly.servlet.HttpServletRequestImpl;
//
//import ch.ahoegger.photobook.ImageFileDesc;
//import ch.ahoegger.photobook.scale.ImageRepository;
//
//public class PictureServlet extends HttpServlet {
//
//  private static final long serialVersionUID = 1L;
//  private String workingDirectory;
//  private String pictureDirectory;
//
//  private volatile int fileCacheFilterIdx = -1;
//  // private File loaderGif;
//
//  protected Logger LOG = LogManager.getLogger(PictureServlet.class);
//
//  @Override
//  public void init() throws ServletException {
//    workingDirectory = getInitParameter("workingDirectory");
//    pictureDirectory = getInitParameter("pictureDirectory");
//    if (LOG.isInfoEnabled()) {
//      LOG.info("working directory: " + workingDirectory);
//      LOG.info("picture directory: " + pictureDirectory);
//    }
//    // String relPath = getServletContext().getRealPath("/loader.gif");
//    // loaderGif = new File(relPath);
//
//  }
//
//  @Override
//  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//    String pathInfo = req.getPathInfo();
//
//    // get extension
//    ImageFileDesc desc = null;
//    try {
//      desc = new ImageFileDesc(pathInfo);
//    } catch (IllegalArgumentException e) {
//      LOG.error("Could not create filedesc of '" + pathInfo + "'.", e);
//      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
//      return;
//    }
//    File requestedFile = ImageRepository.resolveImage(desc, pictureDirectory, workingDirectory);
//    sendFile(req, resp, requestedFile);
//  }
//
//  // private synchronized File getRequestedFile(ImageFileDesc desc) {
//  // File workingDir = new File(workingDirectory);
//  // File res = new File(workingDir, desc.getFilePath());
//  // System.out.println(res.getAbsolutePath() + " " + res.exists());
//  // File file = new File(workingDirectory, desc.getFilePath());
//  // // file.
//  // if (file.exists()) {
//  // return file;
//  // }
//  // // add image to the loader
//  //
//  // return null;
//  // }
//
//  private void sendFile(final HttpServletRequest request, final HttpServletResponse response, final File file) throws IOException {
//    final String path = file.getPath();
//    FileInputStream fis = null;
//    try {
//      fis = new FileInputStream(file);
//    } catch (FileNotFoundException fnfe) {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      return;
//    }
//
//    try {
//      response.setStatus(HttpServletResponse.SC_OK);
//      String substr;
//      int dot = path.lastIndexOf('.');
//      if (dot < 0) {
//        substr = file.toString();
//        dot = substr.lastIndexOf('.');
//      } else {
//        substr = path;
//      }
//      // if (dot > 0) {
//      // String ext = substr.substring(dot + 1);
//      // String ct = MimeTypea.get(ext);
//      // if (ct != null) {
//      // response.setContentType(ct);
//      // }
//      // } else {
//      // response.setContentType(MimeType.get("html"));
//      // }
//
//      final long length = file.length();
//      // if length is larger than Integer.MAX_VALUE, then we have
//      // to rely on chunking.
//      if (length <= Integer.MAX_VALUE) {
//        response.setContentLength((int) length);
//      }
//      addToFileCache(request, file);
//
//      final OutputStream out = response.getOutputStream();
//
//      byte b[] = new byte[8192];
//      int rd;
//      while ((rd = fis.read(b)) > 0) {
//        // chunk.setBytes(b, 0, rd);
//        out.write(b, 0, rd);
//      }
//    } finally {
//      try {
//        fis.close();
//      } catch (IOException ignore) {
//      }
//    }
//  }
//
//  private void addToFileCache(HttpServletRequest req, File resource) {
//    if (req instanceof HttpServletRequestImpl) {
//      final Request request = ((HttpServletRequestImpl) req).getRequest();
//      final FilterChainContext fcContext = request.getContext();
//      final FileCacheFilter fileCacheFilter = lookupFileCache(fcContext);
//      if (fileCacheFilter != null) {
//        final FileCache fileCache = fileCacheFilter.getFileCache();
//        fileCache.add(request.getRequest(), resource);
//      }
//    }
//  }
//
//  private FileCacheFilter lookupFileCache(final FilterChainContext fcContext) {
//    final FilterChain fc = fcContext.getFilterChain();
//    final int lastFileCacheIdx = fileCacheFilterIdx;
//
//    if (lastFileCacheIdx != -1) {
//      final Filter filter = fc.get(lastFileCacheIdx);
//      if (filter instanceof FileCacheFilter) {
//        return (FileCacheFilter) filter;
//      }
//    }
//
//    final int size = fc.size();
//    for (int i = 0; i < size; i++) {
//      final Filter filter = fc.get(i);
//
//      if (filter instanceof FileCacheFilter) {
//        fileCacheFilterIdx = i;
//        return (FileCacheFilter) filter;
//      }
//    }
//
//    fileCacheFilterIdx = -1;
//    return null;
//  }
//}
