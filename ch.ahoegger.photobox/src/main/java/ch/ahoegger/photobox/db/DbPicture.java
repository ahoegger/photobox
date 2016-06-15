package ch.ahoegger.photobox.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ahoegger.photobox.dao.NavigationLink;
import ch.ahoegger.photobox.dao.Picture;
import ch.ahoegger.photobox.db.util.DbStatement;
import ch.ahoegger.photobox.db.util.SQL;

public class DbPicture implements IDbPicture {
  protected static Logger LOG = LoggerFactory.getLogger(DbPicture.class);

  public static void create(Picture p) {
    if (p.getId() == null) {
      p.withId(DbSequence.getNextKey());
    }
    LOG.debug("Create picture: {}.", p);
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("INSERT INTO ")
            .append(TABLE_NAME)
            .append(" ( ")
            .append(
                SQL.columns(COL_ID, COL_NAME, COL_CAPTURE_DATE, COL_ROTATION, COL_ACTIVE, COL_PATH_ORIGINAL, COL_PATH_SMALL, COL_PATH_MEDIUM,
                    COL_PATH_LARGE)).append(") ").append(" VALUES ").append("( ").append("?, ?, ?, ?, ?, ?, ?, ?, ?").append(")");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, p.getId());
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

    // create links
    List<NavigationLink> parents = DbNavigationLink.getParents(p.getFolderId());
    parents.forEach(l -> {
      l.setDistance(l.getDistance() + 1);
      l.setChildId(p.getId());
    });
    parents.add(new NavigationLink(p.getFolderId(), p.getId(), 1));

    DbNavigationLink.setParents(parents);

  }

  public static List<Picture> findByParentId(Long parentId, Boolean active) {
    return new DbStatement<List<Picture>>() {

      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ")
            .append(
                SQL.columnsAliased(TABLE_ALIAS, COL_ID, COL_NAME, COL_ACTIVE, COL_CAPTURE_DATE, COL_PATH_LARGE, COL_PATH_MEDIUM, COL_PATH_ORIGINAL, COL_PATH_SMALL, COL_ROTATION))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" AND ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_DISTANCE)).append(" = 1 ");
        // conditions
        sqlBuilder.append(" WHERE ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID)).append(" = ? ");
        if (active != null) {
          sqlBuilder.append(" AND ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ACTIVE)).append(" = ?");
        }
        sqlBuilder.append(" ORDER BY ").append(SQL.columnsAliased(TABLE_ALIAS, COL_NAME, COL_CAPTURE_DATE)).append(" ASC");
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
        List<Picture> result = new ArrayList<Picture>();
        while (res.next()) {

          result.add(createPicture(res));
        }
        return result;
      }
    }.execute();
  }

  public static Picture findByOrignalPath(String orignalPath) {
    return new DbStatement<Picture>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ")
            .append(
                SQL.columnsAliased(TABLE_ALIAS, COL_ID, COL_NAME, COL_ACTIVE, COL_CAPTURE_DATE, COL_PATH_LARGE, COL_PATH_MEDIUM, COL_PATH_ORIGINAL, COL_PATH_SMALL, COL_ROTATION))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" AND ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_DISTANCE)).append(" = 1 ");
        // conditions

        sqlBuilder.append(" WHERE ")
            .append(SQL.columnsAliased(TABLE_ALIAS, COL_PATH_ORIGINAL)).append(" = ? ");
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
    if (id == null) {
      return null;
    }
    ArrayList<Long> ids = new ArrayList<Long>();
    ids.add(id);
    return findByIds(ids).stream().findFirst().orElse(null);
  }

  public static List<Picture> findByIds(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return null;
    }
    return new DbStatement<List<Picture>>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ")
            .append(
                SQL.columnsAliased(TABLE_ALIAS, COL_ID, COL_NAME, COL_ACTIVE, COL_CAPTURE_DATE, COL_PATH_LARGE, COL_PATH_MEDIUM, COL_PATH_ORIGINAL, COL_PATH_SMALL, COL_ROTATION))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" AND ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_DISTANCE)).append(" = 1 ");
        // conditions

        sqlBuilder.append(" WHERE 1 = 1");
        if (ids.size() == 1) {
          sqlBuilder.append(" AND ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ? ");
        }
        else {
          sqlBuilder.append(" AND ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" IN (")
              .append(ids.stream().map(id -> "?").collect(Collectors.joining(", "))).append(")");
        }
        sqlBuilder.append(" ORDER BY ").append(SQL.columnsAliased(TABLE_ALIAS, COL_NAME, COL_CAPTURE_DATE)).append(" ASC");
        return sqlBuilder.toString();
      }

      @Override
      protected List<Picture> bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int index = 1;
        for (Long id : ids) {
          statement.setLong(index++, id);
        }
        List<Picture> result = new ArrayList<Picture>();
        ResultSet res = statement.executeQuery();
        while (res.next()) {
          result.add(createPicture(res));
        }

        return result;
      }
    }.execute();
  }

  private static Picture createPicture(ResultSet res) throws SQLException {
    return new Picture()
        .withId(res.getLong(1))
        .withFolderId(res.getLong(10))
        .withName(res.getString(2))
        .withActive(res.getBoolean(3))
        .withCaptureDate(res.getDate(4))
        .withPathLarge(res.getString(5))
        .withPathMedium(res.getString(6))
        .withPathOrignal(res.getString(7))
        .withPathSmall(res.getString(8))
        .withRotation(res.getInt(9));
  }

  public static List<Long> getPictureIds(Long parentId, Boolean active) {
    return new DbStatement<List<Long>>() {

      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ")
            .append(
                SQL.columnsAliased(TABLE_ALIAS, COL_ID))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID));
        // conditions
        sqlBuilder.append(" WHERE ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID)).append(" = ? ");
        if (active != null) {
          sqlBuilder.append(" AND ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ACTIVE)).append(" = ?");
        }
        sqlBuilder.append(" ORDER BY ").append(SQL.columnsAliased(TABLE_ALIAS, COL_NAME, COL_CAPTURE_DATE)).append(" ASC");
        return sqlBuilder.toString();
      }

      @Override
      protected List<Long> bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, parentId);
        if (active != null) {
          statement.setBoolean(parameterIndex++, active);
        }
        ResultSet res = statement.executeQuery();
        List<Long> result = new ArrayList<Long>();
        while (res.next()) {
          result.add(res.getLong(1));
        }
        return result;
      }
    }.execute();
  }

  public static Picture update(Picture picture) {

    new DbStatement<Void>() {
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

  /**
   * @param id
   */
  public static void delete(Long id) {
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("DELETE FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS)
            .append(" WHERE ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, id);
        int result = statement.executeUpdate();
        LOG.debug("Delete picture with id '{}' affected '{}' rows.", id, result);
        return null;
      }
    }.execute();

    DbNavigationLink.deleteByChildId(id);

  }

  /**
   * @param parentId
   */
  public static void deleteByParentId(Long parentId) {

    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("DELETE FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS)
            .append(" WHERE ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" IN (")
            .append("SELECT ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" FROM ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" WHERE ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID)).append(" = ? )");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, parentId);
        int result = statement.executeUpdate();
        LOG.debug("Delete pictures width parent id '{}' affected '{}' rows.", parentId, result);
        return null;
      }
    }.execute();
  }
}
