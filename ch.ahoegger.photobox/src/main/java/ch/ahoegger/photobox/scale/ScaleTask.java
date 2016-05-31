package ch.ahoegger.photobox.scale;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

public class ScaleTask {

  protected static Logger LOG = LogManager.getLogger(ScaleTask.class);
  private Path originalImage;
  private Path destFile;
  private int resolution;

  public ScaleTask(Path originalImage, Path destFile, int resolution) {
    this.originalImage = originalImage;
    this.destFile = destFile;
    this.resolution = resolution;
  }

  public void scale() {
    if (!Files.exists(getOriginalImage())) {
      LOG.error("the file '{}' does not exist!", getOriginalImage());
      return;
    }
    try {
      int maxEdge = getResolution();
      // BufferedImage bufferedImage =
      // ImageIO.read(Files.newInputStream(Paths.get(basePath + imageSource)));
      BufferedImage originalImage = ImageIO.read(Files.newInputStream(getOriginalImage()));
      int originalWidth = originalImage.getWidth();
      int originalHeight = originalImage.getHeight();
      // compute width/height
      int height = 0, width = 0;
      if (originalHeight > originalWidth) {
        height = maxEdge;
        double scaleFactor = (double) maxEdge / (double) originalHeight;
        width = (int) (scaleFactor * originalWidth);
      } else {
        width = maxEdge;
        double scaleFactor = (double) maxEdge / (double) originalWidth;
        height = (int) (scaleFactor * originalHeight);
      }

      BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);
      Graphics2D g = resizedImage.createGraphics();
      g.drawImage(originalImage, 0, 0, width, height, null);
      g.dispose();
      g.setComposite(AlphaComposite.Src);
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      // save
      Files.createDirectories(getDestFile().getParent());
      Files.createFile(getDestFile());
      ImageIO.write(resizedImage, "jpg", Files.newOutputStream(getDestFile()));

      if (LOG.isDebugEnabled()) {
        LOG.debug("Created image '{}'.", getDestFile());
      }
    } catch (IOException e) {
      LOG.error(new ParameterizedMessage("Could not scale image '{}'.", getOriginalImage()), e);
    }

  }

  public Path getOriginalImage() {
    return originalImage;
  }

  public Path getDestFile() {
    return destFile;
  }

  public int getResolution() {
    return resolution;
  }

}
