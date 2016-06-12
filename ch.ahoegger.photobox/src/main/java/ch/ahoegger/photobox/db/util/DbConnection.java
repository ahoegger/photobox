package ch.ahoegger.photobox.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.configuration.Configuration;

/**
 * <h3>{@link DbConnection}</h3>
 *
 * @author aho
 */
public class DbConnection {
  protected static Logger LOG = LoggerFactory.getLogger(DbConnection.class);

  public static ThreadLocal<Connection> connection = new ThreadLocal<Connection>();

  public static Connection getOrCreate() throws SQLException {
    Connection conn = connection.get();
    if (conn == null) {
      conn = DriverManager.getConnection(Configuration.getDbConnectionUrl());
      conn.setAutoCommit(false);
      connection.set(conn);
    }
    return conn;
  }

  public static void commitAndRelease() {
    commit();
    release();
  }

  public static void commit() {
    Connection conn = connection.get();
    if (conn != null) {
      try {
        conn.commit();
      }
      catch (Exception e) {
        LOG.error("Could not commit connection.", e);
      }
    }
  }

  public static void rollback() {
    Connection conn = connection.get();
    if (conn != null) {
      try {
        conn.rollback();
      }
      catch (Exception e) {
        LOG.error("Could not rollback connection.", e);
      }
    }
  }

  public static void release() {
    Connection conn = connection.get();
    if (conn != null) {
      try {
        conn.close();
      }
      catch (SQLException se) {
        LOG.error("Could not close connection.", se);
      }
      connection.set(null);
    }
  }

  public static void rollbackAndRelease() {
    rollback();
    release();
  }
}
