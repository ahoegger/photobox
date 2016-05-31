package ch.ahoegger.photobox;
//package ch.ahoegger.photobook;
//
//import java.io.File;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class ImageFileDesc {
//
//  public static enum ImageType {
//    Large, Medium, Small, Unknown
//  }
//
//  public static final String POSTFIX_PREVIEW = "_preview";
//  public static final String POSTFIX_MOBILE = "_mobile";
//  public static final String POSTFIX_DESKTOP = "_desktop";
//
//  private final String m_relativeDirectory;
//  private final String m_fileName;
//  private final String m_fileExtension;
//  private final String m_filePath;
//  private final ImageType m_imageType;
//
//  public ImageFileDesc(String filePath) throws IllegalArgumentException {
//    String fileName = null;
//    this.m_filePath = filePath;
//    String regex = "^(.+)(\\/|\\\\)([a-zA-Z0-9\\_\\-]+)\\.(jpg|png|gif|bmp)$";
//    Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(filePath);
//    if (matcher.find()) {
//      m_relativeDirectory = matcher.group(1);
//      fileName = matcher.group(3);
//      m_fileExtension = matcher.group(4);
//    }
//    else {
//      throw new IllegalArgumentException("filepath '" + filePath + "' could not be parsed.");
//    }
//
//    // compute image type
//    if (fileName.endsWith(POSTFIX_DESKTOP)) {
//      fileName = fileName.substring(0, fileName.length() - POSTFIX_DESKTOP.length());
//      m_imageType = ImageType.Desktop;
//    }
//    else if (fileName.endsWith(POSTFIX_MOBILE)) {
//      fileName = fileName.substring(0, fileName.length() - POSTFIX_MOBILE.length());
//      m_imageType = ImageType.Mobile;
//    }
//    else if (fileName.endsWith(POSTFIX_PREVIEW)) {
//      fileName = fileName.substring(0, fileName.length() - POSTFIX_PREVIEW.length());
//      m_imageType = ImageType.Preview;
//    }
//    else {
//      m_imageType = ImageType.Unknown;
//    }
//    m_fileName = fileName;
//  }
//
//  public String getRelativeDirectory() {
//    return m_relativeDirectory;
//  }
//
//  public String getFileName() {
//    return m_fileName;
//  }
//
//  public String getFileExtension() {
//    return m_fileExtension;
//  }
//
//  public String getFilePath() {
//    return m_filePath;
//  }
//
//  public ImageType getImageType() {
//    return m_imageType;
//  }
//
//  public String getOriginalFilePath() {
//    StringBuilder builder = new StringBuilder(getRelativeDirectory());
//    builder.append(File.separatorChar).append(getFileName());
//    builder.append(".").append(getFileExtension());
//    return builder.toString();
//  }
//
//  @SuppressWarnings("incomplete-switch")
//  public String getFilePath(ImageType type) {
//    StringBuilder pathBuilder = new StringBuilder("pictures/");
//    pathBuilder.append(getRelativeDirectory());
//    pathBuilder.append("/").append(getFileName());
//    switch (type) {
//      case Desktop:
//        pathBuilder.append(POSTFIX_DESKTOP);
//        break;
//      case Mobile:
//        pathBuilder.append(POSTFIX_MOBILE);
//        break;
//      case Preview:
//        pathBuilder.append(POSTFIX_PREVIEW);
//        break;
//    }
//    pathBuilder.append(".").append(getFileExtension());
//    return pathBuilder.toString();
//  }
//
//}
