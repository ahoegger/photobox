package ch.ahoegger.photobox.scale;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.ahoegger.photobox.IProperties;

public class ScaleDeamon implements ServletContextListener {
  public static final Logger LOG = LogManager.getLogger(ScaleDeamon.class);
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    // TODO Auto-generated method stub

    ServletContext ctx = sce.getServletContext();
    String originalDirectoryName = ctx.getInitParameter(IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY);
    if (originalDirectoryName == null || originalDirectoryName.trim().length() == 0) {
      LOG.warn("Missing context-param '{}'.", IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY);
      return;
    }
    Path originalDirectory = Paths.get(originalDirectoryName);
    if (!Files.isDirectory(originalDirectory)) {
      LOG.warn("Orinal directory '{}' does not exist. See context-param '{}'.", originalDirectoryName, IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY);
      return;
    }
    String workingDirectoryName = ctx.getInitParameter(IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
    if (workingDirectoryName == null || workingDirectoryName.trim().length() == 0) {
      LOG.warn("Missing context-param '{}'.", IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
      return;
    }
    Path workingDirectory = Paths.get(workingDirectoryName);
    if (!Files.isDirectory(workingDirectory)) {
      LOG.warn("Working directory '{}' does not exist. See context-param '{}'.", workingDirectoryName, IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
      return;
    }

    scheduler.scheduleWithFixedDelay(new SyncImagesTask(originalDirectory, workingDirectory), 2, 20, TimeUnit.SECONDS);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // TODO Auto-generated method stub
    System.out.println("stop ScaleDeamon");
    LOG.debug("Shotdown Executer");
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
        LOG.debug("Executer will be killed.");
        scheduler.shutdownNow(); // or process/wait until all pending jobs are
                                 // done
      }
      else {
        LOG.debug("Executer shutdown successfully.");
      }
    }
    catch (InterruptedException e) {

    }

  }

}
