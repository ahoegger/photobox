package ch.ahoegger.photobox.db;

public class DbAccess {

  public static String DB_URL;

  static {
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    }
    catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void setDbLocation(String dbLocation) {
    DB_URL = "jdbc:derby:" + dbLocation;
  }

}
