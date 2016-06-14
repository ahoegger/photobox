package ch.ahoegger.photobox.scale;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
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

  private Stack<DirectoryDesc> m_directories = new Stack<DirectoryDesc>();

  @Override
  protected void execute(IMonitor monitor) {

    LOG.info("Start sync image task. original:'{}', working:{}", m_orignalDirectory, m_workingDirectory);
    try {
      try {
        visitDirectory(m_orignalDirectory, 0l, monitor);
      }
      catch (IOException e) {
        LOG.error(String.format("Could not synchonize root directory '%s'.", m_orignalDirectory), e);
      }

      while (!m_directories.isEmpty()) {
        DirectoryDesc current = m_directories.pop();
        if (monitor.isCanceled()) {
          break;
        }
        Folder folder = syncDirectory(current.getPath(), current.getParentId());
        try {
          visitDirectory(current.getPath(), folder.getId(), monitor);
        }
        catch (IOException e) {
          LOG.error(String.format("Could not synchonize directory '%s'.", current), e);
        }
      }
    }
    catch (Exception e) {
      LOG.error("Could not scale images.", e);
    }
    LOG.info("End sync image task. original:'{}', working:{}", m_orignalDirectory, m_workingDirectory);

  }

  protected void visitDirectory(Path directory, Long id, IMonitor monitor) throws IOException {
    if (!Files.isDirectory(directory)) {
      throw new IllegalArgumentException(String.format("'%s' is not a directory.", directory));
    }
    DirectoryStream<Path> directoryStream = null;
    try {
      directoryStream = Files.newDirectoryStream(directory);
      for (Path p : directoryStream) {
        if (monitor.isCanceled()) {
          break;
        }
        if (Files.isDirectory(p)) {
          m_directories.push(new DirectoryDesc(p, id));
        }
        else {
          if (m_imageMatcher.matches(p)) {
            try {
              scaleImage(p, id);
            }
            catch (Exception e) {
              LOG.error(String.format("Could not scale image '%s'.", p), e);
            }
          }
        }
      }
    }
    finally {
      if (directoryStream != null) {
        directoryStream.close();
      }
    }

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
      folder = DbFolder.create(folder);
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

    Path relPath = m_orignalDirectory.relativize(original);
    Picture picture = DbPicture.findByOrignalPath(PhotoUtility.pathToString(relPath, "/"));
    if (picture != null) {
      return picture;
    }
    // scale preview
    LOG.debug("Scale image '{}'.", original);

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

  public static class DirectoryDesc {
    private Long m_parentId;
    private Path m_path;

    public DirectoryDesc(Path path, Long parentId) {
      m_path = path;
      m_parentId = parentId;
    }

    public Long getParentId() {
      return m_parentId;
    }

    public Path getPath() {
      return m_path;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("path:").append(getPath()).append(", parentid:").append(getParentId());
      return builder.toString();
    }
  }
}
