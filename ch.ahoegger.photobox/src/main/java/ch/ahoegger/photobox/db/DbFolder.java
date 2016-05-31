package ch.ahoegger.photobox.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.db.util.PreparedStatementExecuter;
import ch.ahoegger.photobox.db.util.SQL;

public class DbFolder implements IDbFolder {

  public static void create(Folder folder) {
    System.out.println("create folder: " + folder);
    new PreparedStatementExecuter<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(SQL.columns(COL_ID, COL_PARENT_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(") ").append(" VALUES ").append("( ").append("?, ?, ?, ?, ?").append(")");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, folder.getId().longValue());
        statement.setLong(parameterIndex++, folder.getParentId().longValue());
        statement.setString(parameterIndex++, folder.getName());
        statement.setBoolean(parameterIndex++, folder.isActive());
        statement.setString(parameterIndex++, folder.getPathOrignal());
        statement.execute();
        return null;
      }
    }.execute();
  }

  public static Folder findById(Long id) {
    return new PreparedStatementExecuter<Folder>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(SQL.columns(COL_ID, COL_PARENT_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(" FROM ").append(TABLE_NAME)
            .append(" WHERE ").append(COL_ID).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Folder bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setLong(1, id);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
          return new Folder().withId(res.getLong(COL_ID))
              .withParentId(res.getLong(COL_PARENT_ID))
              .withName(res.getString(COL_NAME))
              .withActive(res.getBoolean(COL_ACTIVE))
              .withPathOrignal(res.getString(COL_PATH_ORIGINAL));
        }
        return null;
      }
    }.execute();
  }

  public static List<Folder> findByParentId(Long parentId, Boolean active) {
    return new PreparedStatementExecuter<List<Folder>>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(SQL.columns(COL_ID, COL_PARENT_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(" FROM ").append(TABLE_NAME)
            .append(" WHERE ").append(COL_PARENT_ID).append(" = ? ");
        if (active != null) {
          sqlBuilder.append(" AND ").append(COL_ACTIVE).append(" = ?");
        }
        return sqlBuilder.toString();
      }

      @Override
      protected List<Folder> bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, parentId);
        if (active != null) {
          statement.setBoolean(parameterIndex++, active);
        }
        ResultSet res = statement.executeQuery();
        List<Folder> result = new ArrayList<Folder>(res.getFetchSize());
        while (res.next()) {
          result.add(new Folder().withId(res.getLong(COL_ID))
              .withParentId(res.getLong(COL_PARENT_ID))
              .withName(res.getString(COL_NAME))
              .withActive(res.getBoolean(COL_ACTIVE))
              .withPathOrignal(res.getString(COL_PATH_ORIGINAL)));
        }
        return result;
      }
    }.execute();
  }

  public static Folder findByOrignalPath(String orignalPath) {
    return new PreparedStatementExecuter<Folder>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(SQL.columns(COL_ID, COL_PARENT_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(" FROM ").append(TABLE_NAME)
            .append(" WHERE ").append(COL_PATH_ORIGINAL).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Folder bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setString(1, orignalPath);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
          return new Folder().withId(res.getLong(COL_ID))
              .withParentId(res.getLong(COL_PARENT_ID))
              .withName(res.getString(COL_NAME))
              .withActive(res.getBoolean(COL_ACTIVE))
              .withPathOrignal(res.getString(COL_PATH_ORIGINAL));
        }
        return null;
      }
    }.execute();
  }
}
