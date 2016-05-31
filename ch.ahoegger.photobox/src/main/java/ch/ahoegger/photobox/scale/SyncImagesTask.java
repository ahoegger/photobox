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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.ahoegger.photobox.IProperties;
import ch.ahoegger.photobox.PhotoUtility;
import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.DbFolder;
import ch.ahoegger.photobox.db.DbPicture;
import ch.ahoegger.photobox.db.DbSequence;

public class SyncImagesTask implements Runnable {
  protected static Logger LOG = LogManager.getLogger(SyncImagesTask.class);

  private Path m_orignalDirectory;
  private PathMatcher m_imageMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{jpg,gif,png}");
  private Path m_workingDirectory;

  public SyncImagesTask(Path orignalDirectory, Path workingDirectory) {
    m_orignalDirectory = orignalDirectory;
    m_workingDirectory = workingDirectory;

  }

  @Override
  public void run() {
    m_imageMatcher = FileSystems.getDefault().getPathMatcher("glob:**.{jpg,gif,png}");
    try {
      Files.walkFileTree(m_orignalDirectory, new SimpleFileVisitor<Path>() {
        private Stack<Folder> m_parents = new Stack<Folder>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          if (m_imageMatcher.matches(file)) {
            scaleImage(file, getParentId());
          }
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          Folder folder = syncDirectory(dir, getParentId());
          m_parents.push(folder);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          m_parents.pop();
          System.out.println("postVisit Dir: " + dir);
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
    catch (IOException e1) {
      LOG.error("Could not scale images.", e1);
    }
  }

  private Folder syncDirectory(Path original, Long parentId) {
    if (original.equals(m_orignalDirectory)) {
      return Folder.ROOT;
    }
    Path relPath = m_orignalDirectory.relativize(original);
    Folder folder = DbFolder.findByOrignalPath(PhotoUtility.pathToString(relPath, "/"));
    if (folder != null) {
      return folder;
    }
    folder = new Folder()
        .withId(DbSequence.getNextKey())
        .withParentId(parentId)
        .withName(relPath.getName(relPath.getNameCount() - 1).toString())
        .withActive(true).withPathOrignal(PhotoUtility.pathToString(relPath, "/"));
    DbFolder.create(folder);
    return folder;

  }

  private Picture scaleImage(Path original, Long parentId) {

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
        .withId(DbSequence.getNextKey())
        .withFolderId(parentId)
        .withName(original.getFileName().toString())
        .withActive(true)
        .withRotation(0)
        .withCaptureDate(PhotoUtility.getCreationDate(original))
        .withPathOrignal(PhotoUtility.pathToString(relPath, "/"))
        .withPathSmall(PhotoUtility.pathToString(m_workingDirectory.relativize(previewImg), "/"))
        .withPathMedium(PhotoUtility.pathToString(m_workingDirectory.relativize(mobileImg), "/"))
        .withPathLarge(PhotoUtility.pathToString(m_workingDirectory.relativize(desktopImg), "/"));

    DbPicture.create(picture);
    return picture;
  }
}
