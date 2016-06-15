package ch.ahoegger.photobox.scale;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.configuration.Configuration;
import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.db.DbFolder;

/**
 * <h3>{@link DeleteFolderTask}</h3>
 *
 * @author aho
 */
public class DeleteFolderTask implements Runnable {
  private static final Logger LOG = LoggerFactory.getLogger(DeleteFolderTask.class);

  private final Folder m_folder;

  public DeleteFolderTask(Folder folder) {
    m_folder = folder;
  }

  public Folder getFolder() {
    return m_folder;
  }

  @Override
  public void run() {

    LOG.info("Delete folder '{}'", getFolder());
    try {
      deleteDirectory(Configuration.getWorkingDirectory().resolve(IProperties.ImageType.Small.toString()).resolve(getFolder().getPathOrignal()));
      deleteDirectory(Configuration.getWorkingDirectory().resolve(IProperties.ImageType.Medium.toString()).resolve(getFolder().getPathOrignal()));
      deleteDirectory(Configuration.getWorkingDirectory().resolve(IProperties.ImageType.Large.toString()).resolve(getFolder().getPathOrignal()));

      // delete from db
      DbFolder.delete(getFolder().getId());
    }
    catch (Exception e) {
      LOG.error(String.format("Could not delete folder '%s'", getFolder()), e);
    }
  }

  private void deleteDirectory(Path directory) throws IOException {
    if (!Files.isDirectory(directory)) {
      LOG.warn("'{}' is not a directory. Could not delete it.", directory);
      return;
    }
    // delete files
    Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }

    });

  }

}
