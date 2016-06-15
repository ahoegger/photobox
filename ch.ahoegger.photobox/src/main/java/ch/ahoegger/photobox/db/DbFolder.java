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

import ch.ahoegger.photobox.dao.Folder;
import ch.ahoegger.photobox.dao.NavigationLink;
import ch.ahoegger.photobox.db.util.DbStatement;
import ch.ahoegger.photobox.db.util.SQL;

public class DbFolder implements IDbFolder {
  protected static Logger LOG = LoggerFactory.getLogger(DbFolder.class);
  private static String CHILD_COUNT_TABLE_ALIAS = "CHILD_COUNT_TABLE";
  private static String COL_CHILD_COUNT = "CHILD_COUNT";

  public static Folder create(Folder folder) {
    if (folder.getId() == null) {
      folder.withId(DbSequence.getNextKey());
    }
    LOG.debug("Create folder '{}'.", folder);
    System.out.println("create folder: " + folder);
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(TABLE_NAME).append(" ( ").append(SQL.columns(COL_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(") ").append(" VALUES ").append("( ").append("?,  ?, ?, ?").append(")");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, folder.getId().longValue());
        statement.setString(parameterIndex++, folder.getName());
        statement.setBoolean(parameterIndex++, folder.getActive());
        statement.setString(parameterIndex++, folder.getPathOrignal());
        statement.execute();
        return null;
      }
    }.execute();

    // create links
    List<NavigationLink> parents = DbNavigationLink.getParents(folder.getParentId());
    parents.forEach(l -> {
      l.setDistance(l.getDistance() + 1);
      l.setChildId(folder.getId());
    });
    parents.add(new NavigationLink(folder.getParentId(), folder.getId(), 1));

    DbNavigationLink.setParents(parents);
    return folder;

  }

  public static Folder findById(Long id) {
    LOG.debug("find folder by id '{}'.", id);
    return new DbStatement<Folder>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(", ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, COL_CHILD_COUNT))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join navigation links for parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" AND ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_DISTANCE)).append(" = 1");
        // join with navigation for count
        sqlBuilder.append(" LEFT OUTER JOIN (")
            .append("SELECT ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_PARENT_ID)).append(" AS PPID")
            .append(", COUNT(").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_CHILD_ID)).append(")").append(" AS ").append(COL_CHILD_COUNT)
            .append(" FROM ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS + "_1")
            .append(" LEFT OUTER JOIN ").append(IDbPicture.TABLE_NAME).append(" AS ").append(IDbPicture.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_CHILD_ID)).append(" = ").append(SQL.columnsAliased(IDbPicture.TABLE_ALIAS, IDbPicture.COL_ID))
            .append(" WHERE 1 = 1");
        sqlBuilder.append(" AND ").append(SQL.columnsAliased(IDbPicture.TABLE_ALIAS, IDbPicture.COL_ACTIVE)).append(" = TRUE ");
        sqlBuilder.append(" GROUP BY ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_PARENT_ID))
            .append(") AS ").append(CHILD_COUNT_TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, "PPID"));
        // conditions
        sqlBuilder.append(" WHERE ").append(COL_ID).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Folder bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setLong(1, id);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
          return createFolder(res);
        }
        return null;
      }
    }.execute();
  }

  public static List<Folder> findByParentId(Long parentId, Boolean active, boolean sortAsc) {
    LOG.debug("find folder by parentId '{}'.", parentId);

    return new DbStatement<List<Folder>>() {
      @Override
      protected String getStatement() {

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(", ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, COL_CHILD_COUNT))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join with navigation for parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" AND ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_DISTANCE)).append(" = 1");
        // join with navigation for count
        sqlBuilder.append(" LEFT OUTER JOIN (")
            .append("SELECT ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_PARENT_ID)).append(" AS PPID")
            .append(", COUNT(").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_CHILD_ID)).append(")").append(" AS ").append(COL_CHILD_COUNT)
            .append(" FROM ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS + "_1")
            .append(" LEFT OUTER JOIN ").append(IDbPicture.TABLE_NAME).append(" AS ").append(IDbPicture.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_CHILD_ID)).append(" = ").append(SQL.columnsAliased(IDbPicture.TABLE_ALIAS, IDbPicture.COL_ID))
            .append(" WHERE 1 = 1 ");
        if (active != null) {
          sqlBuilder.append(" AND ").append(SQL.columnsAliased(IDbPicture.TABLE_ALIAS, IDbPicture.COL_ACTIVE)).append(" = ? ");
        }
        sqlBuilder.append(" GROUP BY ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_PARENT_ID))
            .append(") AS ").append(CHILD_COUNT_TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, "PPID"));

        // conditions
        sqlBuilder.append(" WHERE ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID)).append(" = ? ")
            .append(" AND ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, COL_CHILD_COUNT)).append(" > 0");

        if (active != null) {
          sqlBuilder.append(" AND ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ACTIVE)).append(" = ?");
        }
        sqlBuilder.append(" ORDER BY ").append(SQL.columnsAliased(TABLE_ALIAS, COL_NAME));
        if (sortAsc) {
          sqlBuilder.append(" ASC");
        }
        else {
          sqlBuilder.append(" DESC");
        }
        return sqlBuilder.toString();
      }

      @Override
      protected List<Folder> bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        if (active != null) {
          statement.setBoolean(parameterIndex++, active);
        }
        statement.setLong(parameterIndex++, parentId);
        if (active != null) {
          statement.setBoolean(parameterIndex++, active);
        }
        ResultSet res = statement.executeQuery();
        List<Folder> result = new ArrayList<Folder>(res.getFetchSize());
        while (res.next()) {
          result.add(createFolder(res));
        }
        return result;
      }
    }.execute();
  }

  public static Folder findByOrignalPath(String orignalPath) {
    LOG.debug("find folder by originalPath '{}'.", orignalPath);
    return new DbStatement<Folder>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID, COL_NAME, COL_ACTIVE, COL_PATH_ORIGINAL))
            .append(", ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID))
            .append(", ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, COL_CHILD_COUNT))
            .append(" FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS);
        // join with navigation for parent
        sqlBuilder.append(" LEFT OUTER JOIN ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" AND ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_DISTANCE)).append(" = 1");
        // join with navigation for count
        sqlBuilder.append(" LEFT OUTER JOIN (")
            .append("SELECT ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_PARENT_ID)).append(" AS PPID")
            .append(", COUNT(").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_CHILD_ID)).append(")").append(" AS ").append(COL_CHILD_COUNT)
            .append(" FROM ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS + "_1")
            .append(" LEFT OUTER JOIN ").append(IDbPicture.TABLE_NAME).append(" AS ").append(IDbPicture.TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_CHILD_ID)).append(" = ").append(SQL.columnsAliased(IDbPicture.TABLE_ALIAS, IDbPicture.COL_ID))
            .append(" WHERE 1 = 1 ");
        sqlBuilder.append(" AND ").append(SQL.columnsAliased(IDbPicture.TABLE_ALIAS, IDbPicture.COL_ACTIVE)).append(" = TRUE ");
        sqlBuilder.append(" GROUP BY ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS + "_1", IDbNavigationLink.COL_PARENT_ID))
            .append(") AS ").append(CHILD_COUNT_TABLE_ALIAS)
            .append(" ON ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ").append(SQL.columnsAliased(CHILD_COUNT_TABLE_ALIAS, "PPID"));

        // conditions
        sqlBuilder.append(" WHERE ").append(SQL.columnsAliased(TABLE_ALIAS, COL_PATH_ORIGINAL)).append(" LIKE ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Folder bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        statement.setString(1, orignalPath);
        ResultSet res = statement.executeQuery();
        if (res.next()) {
          return createFolder(res);
        }
        return null;
      }
    }.execute();
  }

  private static Folder createFolder(ResultSet res) throws SQLException {
    return new Folder().withId(res.getLong(1))
        .withParentId(res.getLong(5))
        .withName(res.getString(2))
        .withActive(res.getBoolean(3))
        .withPathOrignal(res.getString(4))
        .withTotalPictureCount(res.getInt(6));

  }

  public static Folder update(Folder folder) {

    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        List<String> updateValues = new ArrayList<String>();
        if (folder.getActive() != null) {
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
        if (folder.getActive() != null) {
          statement.setBoolean(parameterIndex++, folder.getActive().booleanValue());
        }

        statement.setLong(parameterIndex++, folder.getId());
        statement.execute();
        return null;
      }
    }.execute();
    return findById(folder.getId());
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
            .append(" WHERE ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" IN (")
            .append("SELECT ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_CHILD_ID))
            .append(" FROM ").append(IDbNavigationLink.TABLE_NAME).append(" AS ").append(IDbNavigationLink.TABLE_ALIAS)
            .append(" WHERE ").append(SQL.columnsAliased(IDbNavigationLink.TABLE_ALIAS, IDbNavigationLink.COL_PARENT_ID)).append(" = ? )")
            .append(" OR ").append(SQL.columnsAliased(TABLE_ALIAS, COL_ID)).append(" = ?");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, id);
        statement.setLong(parameterIndex++, id);
        int result = statement.executeUpdate();
        LOG.debug("Delete folder with id '{}' affected '{}' rows.", id, result);
        return null;
      }
    }.execute();

    DbPicture.deleteByParentId(id);
    DbNavigationLink.deleteByParentId(id);
    DbNavigationLink.deleteByChildId(id);

  }
}
