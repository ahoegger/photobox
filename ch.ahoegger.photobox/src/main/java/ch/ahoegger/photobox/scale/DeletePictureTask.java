package ch.ahoegger.photobox.scale;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.configuration.Configuration;
import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.DbPicture;

/**
 * <h3>{@link DeletePictureTask}</h3>
 *
 * @author aho
 */
public class DeletePictureTask implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(DeletePictureTask.class);

  private final Picture m_picture;

  public DeletePictureTask(Picture picture) {
    m_picture = picture;
  }

  public Picture getPicture() {
    return m_picture;
  }

  @Override
  public void run() {
    LOG.info("Delete picture '{}'", getPicture());
    try {
      deleteFile(Configuration.getWorkingDirectory().resolve(IProperties.ImageType.Small.toString()).resolve(getPicture().getPathOrignal()));
      deleteFile(Configuration.getWorkingDirectory().resolve(IProperties.ImageType.Medium.toString()).resolve(getPicture().getPathOrignal()));
      deleteFile(Configuration.getWorkingDirectory().resolve(IProperties.ImageType.Large.toString()).resolve(getPicture().getPathOrignal()));

      // delete from db
      DbPicture.delete(getPicture().getId());
    }
    catch (Exception e) {
      LOG.error(String.format("Could not delete picture '%s'", getPicture()), e);
    }
  }

  private void deleteFile(Path file) throws IOException {
    if (!Files.exists(file)) {
      LOG.warn("'{}' is not a file. Could not delete it.", file);
      return;
    }
    // delete files
    Files.delete(file);
  }

}
