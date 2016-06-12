package ch.ahoegger.photobox.configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.IProperties;

/**
 * <h3>{@link Configuration}</h3>
 *
 * @author aho
 */
public class ConfigurationListener implements ServletContextListener {

  public static final Logger LOG = LoggerFactory.getLogger(ConfigurationListener.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    // setup configuration

    ServletContext ctx = sce.getServletContext();
    String originalDirectoryName = ctx.getInitParameter(IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY);
    if (originalDirectoryName == null || originalDirectoryName.trim().length() == 0) {
      LOG.warn("Missing context-param '{}'.", IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY);
      throw new RuntimeException(String.format("Missing context-param '%s'.", IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY));
    }
    Path originalDirectory = Paths.get(originalDirectoryName);
    if (!Files.isDirectory(originalDirectory)) {
      LOG.warn("Orinal directory '{}' does not exist. See context-param '{}'.", originalDirectoryName, IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY);
      throw new RuntimeException(String.format("Orinal directory '%s' does not exist. See context-param '%s'.", originalDirectoryName, IProperties.CONTEXT_PARAM_ORIGINAL_DIRECTORY));
    }
    Configuration.setOriginalDirectory(originalDirectory);
    String workingDirectoryName = ctx.getInitParameter(IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
    if (workingDirectoryName == null || workingDirectoryName.trim().length() == 0) {
      LOG.warn("Missing context-param '{}'.", IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
      throw new RuntimeException(String.format("Missing context-param '%s'.", IProperties.CONTEXT_PARAM_WORKING_DIRECTORY));
    }
    Path workingDirectory = Paths.get(workingDirectoryName);
    if (!Files.isDirectory(workingDirectory)) {
      LOG.warn("Working directory '{}' does not exist. See context-param '{}'.", workingDirectoryName, IProperties.CONTEXT_PARAM_WORKING_DIRECTORY);
      throw new RuntimeException(String.format("Working directory '%s' does not exist. See context-param '%S'.", workingDirectoryName, IProperties.CONTEXT_PARAM_WORKING_DIRECTORY));
    }
    Configuration.setWorkingDirectory(workingDirectory);
    // db connection url

    String dbLocation = ctx.getInitParameter(IProperties.CONTEXT_PARAM_DB_LOCATION);
    if (dbLocation == null || dbLocation.isEmpty()) {
      LOG.warn("Missing config property DB location '{}' . See context-param '{}'.", dbLocation, IProperties.CONTEXT_PARAM_DB_LOCATION);
      throw new RuntimeException(String.format("Missing config property DB location '%s' . See context-param '%s'.", dbLocation, IProperties.CONTEXT_PARAM_DB_LOCATION));
    }
    Configuration.setDbLocation(dbLocation);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }

}
