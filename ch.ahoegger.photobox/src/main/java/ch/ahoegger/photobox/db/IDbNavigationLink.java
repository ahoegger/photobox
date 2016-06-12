package ch.ahoegger.photobox.db;

/**
 * <h3>{@link IDbNavigationLink}</h3>
 *
 * @author aho
 */
public interface IDbNavigationLink {
  String TABLE_NAME = "PHOTOBOX_NAVIGATION_LINK";
  String TABLE_ALIAS = "NL";

  String COL_PARENT_ID = "PARENT_ID";
  String COL_CHILD_ID = "CHILD_ID";
  String COL_DISTANCE = "DISTANCE";
}
