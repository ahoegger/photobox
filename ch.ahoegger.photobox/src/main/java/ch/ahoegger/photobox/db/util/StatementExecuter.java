package ch.ahoegger.photobox.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.db.DbAccess;

public abstract class StatementExecuter<T> {
  private static Logger LOG = LoggerFactory.getLogger(StatementExecuter.class);

  public final T execute() {
    T retVal = null;
    Connection conn = null;
    Statement stmt = null;
    try {
      conn = DriverManager.getConnection(DbAccess.DB_URL);
      conn.setAutoCommit(true);
      stmt = conn.createStatement();
      retVal = executeQuery(conn, stmt);
    }
    catch (SQLException e) {
      LOG.error("Could not execute query.", e);
    }
    finally {
      try {
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (SQLException se2) {
      }// nothing we can do
      try {
        if (conn != null) {
          conn.close();
        }
      }
      catch (SQLException se) {
        LOG.error("Could not close connection.", se);
      }
    }
    return retVal;
  }

  protected abstract T executeQuery(Connection connection, Statement statement) throws SQLException;
}
