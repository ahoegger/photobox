package ch.ahoegger.photobox.db;

public interface IDbPicture {
  String TABLE_NAME = "PHOTOBOX_PICTURE";
  String TABLE_ALIAS = "P";

  String COL_ID = "ID";
  String COL_CAPTURE_DATE = "CAPTURE_DATE";
  String COL_NAME = "NAME";
  String COL_ROTATION = "ROTATION";
  String COL_PATH_ORIGINAL = "PATH_ORIGINAL";
  String COL_PATH_SMALL = "PATH_SMALL";
  String COL_PATH_MEDIUM = "PATH_MEDIUM";
  String COL_PATH_LARGE = "PATH_LARGE";
  String COL_ACTIVE = "ACTIVE";
}
