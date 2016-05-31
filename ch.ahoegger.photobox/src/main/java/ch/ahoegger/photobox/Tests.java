package ch.ahoegger.photobox;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class Tests {

  public static void main(String[] args) {
    t02();
  }

  private static void t01() {
    System.out.println(PhotoUtility.getExistingPath(null));
    Path p = PhotoUtility.getExistingPath("");
    System.out.println(p + " - " + Files.exists(p) + " " + p.toAbsolutePath());

    System.out.println(PhotoUtility.getExistingPath("D:/temp"));
  }

  private static void t02() {
    String filename = "D:/temp/max24h/weihnachtskartenBilder/2015-11-08 14.27.19.jpg";
    System.out.println("Filename: " + filename);

    try {
      File jpgFile = new File(filename);
      Metadata metadata = ImageMetadataReader.readMetadata(jpgFile);

      // Read Exif Data
      metadata.getDirectories().forEach(d -> System.out.println(d.getClass()));
      // ExifIFD0Directory
      Directory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
      // directory.setDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL, new
      // Date());
      if (directory != null) {
        // Read the date
        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        System.out.println(date);
        DateFormat df = DateFormat.getDateInstance();
        df.format(date);
        int year = df.getCalendar().get(Calendar.YEAR);
        int month = df.getCalendar().get(Calendar.MONTH) + 1;

        System.out.println("Year: " + year + ", Month: " + month);

        System.out.println("Date: " + date);

        System.out.println("Tags");
        for (Tag tag : directory.getTags()) {
          System.out.println("\t" + tag.getTagName() + " = " + tag.getDescription());

        }
      } else {
        System.out.println("EXIF is null");
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
