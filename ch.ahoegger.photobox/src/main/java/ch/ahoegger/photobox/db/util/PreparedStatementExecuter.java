package ch.ahoegger.photobox.db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.ahoegger.photobox.db.DbAccess;

public abstract class PreparedStatementExecuter<T> {
  protected static Logger LOG = LogManager.getLogger(PreparedStatementExecuter.class);

  public final T execute() {
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(DbAccess.DB_URL);
      conn.setAutoCommit(true);
      return executeQuery(conn);
    } catch (SQLException e) {
      LOG.error("Could not execute query.", e);
      return null;
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException se) {
        LOG.error("Could not close connection.", se);
      }
    }
  }

  protected T executeQuery(Connection connection) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(getStatement());
    try {
      return bindAndExecute(connection, statement);

    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  protected String getStatement() {
    return null;
  }

  protected abstract T bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException;
}
