package ch.ahoegger.photobox.db;

public interface IDbFolder {
  String TABLE_NAME = "PHOTOBOX_FOLDER";

  String COL_ID = "ID";
  String COL_PARENT_ID = "PARENT_ID";
  String COL_NAME = "NAME";
  String COL_ACTIVE = "ACTIVE";
  String COL_PATH_ORIGINAL = "PATH_ORIGINAL";
}
