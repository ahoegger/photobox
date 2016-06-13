package ch.ahoegger.photobox.scale;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Stack;

import org.quartz.DisallowConcurrentExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.PhotoUtility;
import ch.ahoegger.photobox.configuration.Configuration;
import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.DbFolder;
import ch.ahoegger.photobox.db.DbPicture;
import ch.ahoegger.photobox.db.util.DbConnection;
import ch.ahoegger.photobox.quarz.AbstractCanceableJob;

/**
 * <h3>{@link ScaleJob}</h3>
 *
 * @author aho
 */
@DisallowConcurrentExecution
public class ScaleJob extends AbstractCanceableJob {
  private static final Logger LOG = LoggerFactory.getLogger(ScaleJob.class);

  private Path m_orignalDirectory = Configuration.getOriginalDirectory();
  private Path m_workingDirectory = Configuration.getWorkingDirectory();
  private PathMatcher m_imageMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{jpg,JPG}");

  @Override
  protected void execute(IMonitor monitor) {

    LOG.debug("Start sync image task. original:'{}', working:{}", m_orignalDirectory, m_workingDirectory);
    try {
      Files.walkFileTree(m_orignalDirectory, new SimpleFileVisitor<Path>() {
        private Stack<Folder> m_parents = new Stack<Folder>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (monitor.isCanceled()) {
            return FileVisitResult.TERMINATE;
          }
          LOG.debug("Visit file '{}'", file);
          if (m_imageMatcher.matches(file)) {
            try {
              scaleImage(file, getParentId());
            }
            catch (Exception e) {
              LOG.error(String.format("Could not scale image '%s'.", file), e);
            }
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          if (monitor.isCanceled()) {
            return FileVisitResult.TERMINATE;
          }
          LOG.debug("Pre visit directory '{}'", dir);
          Folder folder = syncDirectory(dir, getParentId());
          m_parents.push(folder);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          if (monitor.isCanceled()) {
            return FileVisitResult.TERMINATE;
          }
          LOG.debug("Post visit directory '{}'", dir);
          m_parents.pop();
          return FileVisitResult.CONTINUE;
        }

        private long getParentId() {
          if (m_parents.isEmpty()) {
            return 0l;
          }
          return m_parents.peek().getId();
        }

      });
    }
    catch (Exception e1) {
      LOG.error("Could not scale images.", e1);
    }
    LOG.debug("End sync image task. original:'{}', working:{}", m_orignalDirectory, m_workingDirectory);
  }

  private Folder syncDirectory(Path original, Long parentId) {
    LOG.debug("sync directory '{}'", original);
    if (original.equals(m_orignalDirectory)) {
      return Folder.ROOT;
    }
    Path relPath = m_orignalDirectory.relativize(original);
    Folder folder = DbFolder.findByOrignalPath(PhotoUtility.pathToString(relPath, "/"));
    if (folder != null) {
      return folder;
    }
    folder = new Folder()
        .withParentId(parentId)
        .withName(relPath.getName(relPath.getNameCount() - 1).toString())
        .withActive(true).withPathOrignal(PhotoUtility.pathToString(relPath, "/"));

    try {
      DbFolder.create(folder);
      DbConnection.commit();
    }
    catch (Exception e) {
      DbConnection.rollback();
      throw e;
    }
    finally {
      DbConnection.release();
    }

    return folder;

  }

  private Picture scaleImage(Path original, Long parentId) {
    LOG.debug("Scale image '{}'.", original);

    Path relPath = m_orignalDirectory.relativize(original);
    Picture picture = DbPicture.findByOrignalPath(PhotoUtility.pathToString(relPath, "/"));
    if (picture != null) {
      return picture;
    }
    // scale preview

    Path previewImg = m_workingDirectory.resolve(IProperties.ImageType.Small.toString()).resolve(relPath);
    if (!Files.exists(previewImg)) {
      new ScaleTask(original, previewImg, IProperties.RESOLUTION_SMALL).scale();
    }
    // mobile
    Path mobileImg = m_workingDirectory.resolve(IProperties.ImageType.Medium.toString()).resolve(relPath);
    if (!Files.exists(mobileImg)) {
      new ScaleTask(original, mobileImg, IProperties.RESOLUTION_MEDIUM).scale();
    }
    // desktop
    Path desktopImg = m_workingDirectory.resolve(IProperties.ImageType.Large.toString()).resolve(relPath);
    if (!Files.exists(desktopImg)) {
      new ScaleTask(original, desktopImg, IProperties.RESOLUTION_LARGE).scale();
    }
    picture = new Picture()
        .withFolderId(parentId)
        .withName(original.getFileName().toString())
        .withActive(true)
        .withRotation(0)
        .withCaptureDate(PhotoUtility.getCreationDate(original))
        .withPathOrignal(PhotoUtility.pathToString(relPath, "/"))
        .withPathSmall(PhotoUtility.pathToString(m_workingDirectory.relativize(previewImg), "/"))
        .withPathMedium(PhotoUtility.pathToString(m_workingDirectory.relativize(mobileImg), "/"))
        .withPathLarge(PhotoUtility.pathToString(m_workingDirectory.relativize(desktopImg), "/"));

    try {
      DbPicture.create(picture);
      DbConnection.commit();
    }
    catch (Exception e) {
      DbConnection.rollback();
      throw e;
    }
    finally {
      DbConnection.release();
    }

    return picture;
  }
}
