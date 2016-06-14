package ch.ahoegger.photobox.scale;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaleTask {

  private static final Logger LOG = LoggerFactory.getLogger(ScaleTask.class);
  private Path m_originalImage;
  private Path m_destinationFile;
  private int m_resolution;

  public ScaleTask(Path originalImage, Path destFile, int resolution) {
    this.m_originalImage = originalImage;
    this.m_destinationFile = destFile;
    this.m_resolution = resolution;
  }

  public void scale() {
    if (!Files.exists(getOriginalImage())) {
      LOG.error("the file '{}' does not exist!", getOriginalImage());
      return;
    }
    int maxEdge = getResolution();
    BufferedImage originalImage = getBufferedImage(getOriginalImage());
    if (originalImage == null) {
      return;
    }

    int originalWidth = originalImage.getWidth();
    int originalHeight = originalImage.getHeight();
    // compute width/height
    int height = 0, width = 0;
    if (originalHeight > originalWidth) {
      height = maxEdge;
      double scaleFactor = (double) maxEdge / (double) originalHeight;
      width = (int) (scaleFactor * originalWidth);
    }
    else {
      width = maxEdge;
      double scaleFactor = (double) maxEdge / (double) originalWidth;
      height = (int) (scaleFactor * originalHeight);
    }

    BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);
    Graphics2D g = null;
    try {
      g = resizedImage.createGraphics();
      g.drawImage(originalImage, 0, 0, width, height, null);
      g.dispose();
      g.setComposite(AlphaComposite.Src);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      // save
      writeFile(resizedImage, getDestFile());
    }
    finally {
      if (g != null) {
        g.dispose();
      }
    }

    if (LOG.isDebugEnabled()) {
      LOG.debug("Created image '{}'.", getDestFile());
    }

  }

  private BufferedImage getBufferedImage(Path path) {
    InputStream is = null;
    try {
      is = Files.newInputStream(getOriginalImage());
      return ImageIO.read(is);
    }
    catch (Exception e) {
      LOG.error(String.format("Could not read file: '%s'.", path), e);
      return null;
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (Exception e) {
          LOG.error(String.format("Could not close input stream for file '%s'.", path), e);
        }
      }
    }
  }

  private void writeFile(BufferedImage image, Path path) {
    OutputStream os = null;
    try {
      Files.createDirectories(path.getParent());
      Files.createFile(path);
      os = Files.newOutputStream(path);
      ImageIO.write(image, "jpg", os);
    }
    catch (Exception e) {
      LOG.error(String.format("Could write image to file: '%s'.", path), e);
    }
    finally {
      if (os != null) {
        try {
          os.close();
        }
        catch (Exception e) {
          LOG.error(String.format("Could not close output stream for file '%s'.", path), e);
        }
      }
    }
  }

  public Path getOriginalImage() {
    return m_originalImage;
  }

  public Path getDestFile() {
    return m_destinationFile;
  }

  public int getResolution() {
    return m_resolution;
  }

}
