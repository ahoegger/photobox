package ch.ahoegger.photobox.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.db.util.DbStatement;

public final class DbSequence implements IDbSequence {
  protected static Logger LOG = LoggerFactory.getLogger(DbSequence.class);

  public synchronized static Long getNextKey() {

    Long result = new DbStatement<Long>() {

      @Override
      protected String getStatement() {
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("SELECT ").append(COL_LAST_VAL).append(" FROM ").append(TABLE_NAME);
        return sqlStatement.toString();
      }

      @Override
      protected Long bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
          return resultSet.getLong(COL_LAST_VAL);
        }
        return null;
      }
    }.execute();

    // update
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("UPDATE ").append(TABLE_NAME).append(" SET ").append(COL_LAST_VAL).append(" = ?");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setLong(1, result + 1);
        if (statement.executeUpdate() != 1) {
          LOG.error("Could not update sequence.");
        }
        return null;
      }
    }.execute();

    return result;

  }
}
