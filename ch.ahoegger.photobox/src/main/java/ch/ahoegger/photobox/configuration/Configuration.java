package ch.ahoegger.photobox.configuration;

import java.nio.file.Path;

/**
 * <h3>{@link Configuration}</h3>
 *
 * @author aho
 */
public final class Configuration {

  private static Path originalDirectory;
  private static Path workingDirectory;
  private static String dbConnectionUrl;

  // load SQL driver
  static {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * @param path
   */
  static void setOriginalDirectory(Path path) {
    Configuration.originalDirectory = path;
  }

  public static Path getOriginalDirectory() {
    return originalDirectory;
  }

  /**
   * @param workingDirectory
   */
  static void setWorkingDirectory(Path workingDirectory) {
    Configuration.workingDirectory = workingDirectory;
  }

  public static Path getWorkingDirectory() {
    return workingDirectory;
  }

  static void setDbLocation(String dbLocation) {
    dbConnectionUrl = "jdbc:derby:" + dbLocation;
  }

  public static String getDbConnectionUrl() {
    return dbConnectionUrl;
  }
}
