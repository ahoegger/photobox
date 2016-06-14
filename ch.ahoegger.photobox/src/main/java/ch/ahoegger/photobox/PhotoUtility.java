package ch.ahoegger.photobox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public final class PhotoUtility {
  private static final Logger LOG = LoggerFactory.getLogger(PhotoUtility.class);

  private PhotoUtility() {

  }

  public static String stripFileName(File prefix, File file) {
    return prefix.toPath().toAbsolutePath().relativize(file.toPath().toAbsolutePath()).toString().replaceAll("\\\\", "/");
  }

  public static String pathToString(Path path, String separator) {
    String strPath = StreamSupport.stream(path.spliterator(), false).map(p -> p.toString()).collect(Collectors.joining("/"));
    return strPath;
  }

  public static Path getExistingPath(String pathName) {
    if (pathName == null) {
      return null;
    }
    Path p = Paths.get(pathName);
    if (Files.exists(p)) {
      return p;
    }
    return null;
  }

  public static Date getCreationDate(Path path) {
    if (!Files.exists(path)) {
      return null;
    }
    InputStream stream = null;
    try {
      stream = Files.newInputStream(path);
      Metadata metadata = ImageMetadataReader.readMetadata(stream);

      // ExifIFD0Directory
      Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
      // directory.setDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, new
      // Date());
      if (directory != null) {
        // Read the date
        return directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
      }
    }
    catch (Exception e) {
      LOG.warn(String.format("Could not read creation date of '%s'.", path), e);
    }
    finally {
      if (stream != null) {
        try {
          stream.close();
        }
        catch (IOException e) {
          LOG.warn(String.format("Could not close input stream of '%s'.", path), e);
        }
      }
    }
    return null;

  }
}
