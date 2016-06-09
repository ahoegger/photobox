package ch.ahoegger.photobox.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.db.util.StatementExecuter;

public final class DbSequence implements IDbSequence {
  protected static Logger LOG = LoggerFactory.getLogger(DbSequence.class);

  public synchronized static Long getNextKey() {

    return new StatementExecuter<Long>() {

      @Override
      protected Long executeQuery(Connection connection, Statement statement) throws SQLException {
        Long result = -1l;
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(COL_LAST_VAL).append(" FROM ").append(TABLE_NAME).append("");
        ResultSet rs = statement.executeQuery(sqlBuilder.toString());
        if (rs.next()) {
          result = rs.getLong(COL_LAST_VAL);
          // update
          sqlBuilder = new StringBuilder();
          sqlBuilder.append("UPDATE ").append(TABLE_NAME).append(" SET ").append(COL_LAST_VAL).append("=").append(result + 1);
          if (statement.executeUpdate(sqlBuilder.toString()) != 1) {
            LOG.error("Could not update sequence.");
          }
        }

        return result;
      }
    }.execute();

  }

}
