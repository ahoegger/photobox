package ch.ahoegger.photobox.db.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h3>{@link DbStatement}</h3>
 *
 * @author aho
 */
public abstract class DbStatement<T> {
  protected static Logger LOG = LoggerFactory.getLogger(DbStatement.class);
  private T m_result;

  public T execute() {
    PreparedStatement statement = null;
    try {
      Connection connection = DbConnection.getOrCreate();
      String sqlString = getStatement();
      LOG.debug("Execute statement: {}", sqlString);
      statement = connection.prepareStatement(sqlString);
      m_result = bindAndExecute(connection, statement);
      return m_result;

    }
    catch (SQLException e) {
      throw new DbAccessException(e);
    }
    finally {
      if (statement != null) {
        try {
          statement.close();
        }
        catch (SQLException e) {
          // void
        }
      }
    }
  }

  protected String getStatement() {
    return null;
  }

  protected abstract T bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException;

  public T getResult() {
    return m_result;
  }
}
