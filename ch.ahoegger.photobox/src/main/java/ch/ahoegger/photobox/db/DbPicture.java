package ch.ahoegger.photobox.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.util.PreparedStatementExecuter;
import ch.ahoegger.photobox.db.util.SQL;

public class DbPicture implements IDbPicture {

  public static void create(Picture p) {
    System.out.println("create Picture: " + p.getPathOrignal());
    new PreparedStatementExecuter<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("INSERT INTO ")
            .append(TABLE_NAME)
            .append(" ( ")
            .append(
                SQL.columns(COL_ID, COL_FOLDER_ID, COL_NAME, COL_CAPTURE_DATE, COL_ROTATION, COL_ACTIVE, COL_PATH_ORIGINAL, COL_PATH_SMALL, COL_PATH_MEDIUM,
                    COL_PATH_LARGE)).append(") ").append(" VALUES ").append("( ").append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?").append(")");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, p.getId());
        statement.setLong(parameterIndex++, p.getFolderId());
        statement.setString(parameterIndex++, p.getName());
        statement.setDate(parameterIndex++, SQL.toSqlDate(p.getCaptureDate()));
        statement.setInt(parameterIndex++, p.getRotation());
        statement.setBoolean(parameterIndex++, p.getActive());
        statement.setString(parameterIndex++, p.getPathOrignal());
        statement.setString(parameterIndex++, p.getPathSmall());
        statement.setString(parameterIndex++, p.getPathMedium());
        statement.setString(parameterIndex++, p.getPathLarge());
        statement.execute();
        return null;
      }
    }.execute();
  }

  public static List<Picture> findByParentId(Long parentId, Boolean active) {
    return new PreparedStatementExecuter<List<Picture>>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("SELECT ")
            .append(
                SQL.columns(COL_ID, COL_FOLDER_ID, COL_NAME, COL_ACTIVE, COL_CAPTURE_DATE, COL_PATH_LARGE, COL_PATH_MEDIUM, COL_PATH_ORIGINAL, COL_PATH_SMALL, COL_ROTATION))
            .append(" FROM ").append(TABLE_NAME)
            .append(" WHERE ").append(COL_FOLDER_ID).append(" = ? ");
        if (active != null) {
          sqlBuilder.append(" AND ").append(COL_ACTIVE).append(" = ?");
        }
        sqlBuilder.append(" ORDER BY ").append(COL_CAPTURE_DATE);
        return sqlBuilder.toString();
      }

      @Override
      protected List<Picture> bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, parentId);
        if (active != null) {
          statement.setBoolean(parameterIndex++, active);
        }
        ResultSet res = statement.executeQuery();
        List<Picture> result = new ArrayList<Picture>(res.getFetchSize());
        while (res.next()) {
          result.add(createPicture(res));
        }
        return result;
      }
    }.execute();
  }

  public static Picture findByOrignalPath(String orignalPath) {
    return new PreparedStatementExecuter<Picture>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("SELECT ")
            .append(
                SQL.columns(COL_ID, COL_FOLDER_ID, COL_NAME, COL_ACTIVE, COL_CAPTURE_DATE, COL_PATH_LARGE, COL_PATH_MEDIUM, COL_PATH_ORIGINAL, COL_PATH_SMALL,
                    COL_ROTATION)).append(" FROM ").append(TABLE_NAME).append(" WHERE ").append(COL_PATH_ORIGINAL).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Picture bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setString(1, orignalPath);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
          return createPicture(res);
        }
        return null;
      }
    }.execute();
  }

  public static Picture findById(Long id) {
    return new PreparedStatementExecuter<Picture>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("SELECT ")
            .append(
                SQL.columns(COL_ID, COL_FOLDER_ID, COL_NAME, COL_ACTIVE, COL_CAPTURE_DATE, COL_PATH_LARGE, COL_PATH_MEDIUM, COL_PATH_ORIGINAL, COL_PATH_SMALL,
                    COL_ROTATION)).append(" FROM ").append(TABLE_NAME).append(" WHERE ").append(COL_ID).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Picture bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setLong(1, id);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
          return createPicture(res);
        }
        return null;
      }
    }.execute();
  }

  private static Picture createPicture(ResultSet res) throws SQLException {
    return new Picture()
        .withId(res.getLong(COL_ID))
        .withFolderId(res.getLong(COL_FOLDER_ID))
        .withName(res.getString(COL_NAME))
        .withActive(res.getBoolean(COL_ACTIVE))
        .withCaptureDate(res.getDate(COL_CAPTURE_DATE))
        .withPathLarge(res.getString(COL_PATH_LARGE))
        .withPathMedium(res.getString(COL_PATH_MEDIUM))
        .withPathOrignal(res.getString(COL_PATH_ORIGINAL))
        .withPathSmall(res.getString(COL_PATH_SMALL))
        .withRotation(res.getInt(COL_ROTATION));
  }

  public static Picture update(Picture picture) {

    new PreparedStatementExecuter<Void>() {
      @Override
      protected String getStatement() {
        List<String> updateValues = new ArrayList<String>();
        if (picture.getRotation() != null) {
          updateValues.add(String.format("%s = ?", COL_ROTATION));
        }
        if (picture.getActive() != null) {
          updateValues.add(String.format("%s = ?", COL_ACTIVE));
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("UPDATE ").append(TABLE_NAME)
            .append(" SET ");
        sqlBuilder.append(updateValues.stream().collect(Collectors.joining(" ,")));
        sqlBuilder.append(" WHERE ")
            .append(COL_ID).append(" = ?");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        if (picture.getRotation() != null) {
          statement.setInt(parameterIndex++, picture.getRotation().intValue());
        }
        if (picture.getActive() != null) {
          statement.setBoolean(parameterIndex++, picture.getActive().booleanValue());
        }

        statement.setLong(parameterIndex++, picture.getId());
        statement.execute();
        return null;
      }
    }.execute();
    return findById(picture.getId());
  }
}
