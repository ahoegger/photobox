package ch.ahoegger.photobox.db;

public interface IDbFolder {
  String TABLE_NAME = "PHOTOBOX_FOLDER";
  String TABLE_ALIAS = "F";

  String COL_ID = "ID";
  String COL_NAME = "NAME";
  String COL_ACTIVE = "ACTIVE";
  String COL_PATH_ORIGINAL = "PATH_ORIGINAL";
}
