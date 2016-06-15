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
import ch.ahoegger.photobox.db.util.DbStatement;
import ch.ahoegger.photobox.db.util.SQL;

/**
 * <h3>{@link DbNavigationLink}</h3>
 *
 * @author aho
 */
public class DbNavigationLink implements IDbNavigationLink {
  protected static Logger LOG = LoggerFactory.getLogger(DbNavigationLink.class);

  public static List<NavigationLink> getParents(long id) {
    return new DbStatement<List<NavigationLink>>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlStatement = new StringBuilder();
        sqlStatement.append("SELECT ").append(SQL.columns(COL_PARENT_ID, COL_DISTANCE))
            .append(" FROM ").append(TABLE_NAME)
            .append(" WHERE ").append(COL_CHILD_ID).append(" = ?");
        return sqlStatement.toString();
      }

      @Override
      protected List<NavigationLink> bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        List<NavigationLink> result = new ArrayList<NavigationLink>();
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
          result.add(new NavigationLink(resultSet.getInt(COL_PARENT_ID), id, resultSet.getInt(COL_DISTANCE)));
        }
        return result;
      }
    }.execute();
  }

  public static void setParents(List<NavigationLink> links) {
    LOG.debug("Create parent links: {}", links.stream().map(link -> "[" + link.toString() + "]").collect(Collectors.joining(", ")));
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder statementBuilder = new StringBuilder();
        statementBuilder.append("INSERT INTO ").append(IDbNavigationLink.TABLE_NAME)
            .append(" (").append(SQL.columns(IDbNavigationLink.COL_PARENT_ID, IDbNavigationLink.COL_CHILD_ID, IDbNavigationLink.COL_DISTANCE)).append(") VALUES ");
        statementBuilder.append(links.stream().map(l -> "( ?, ?, ? )").collect(Collectors.joining(", ")));
        return statementBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int index = 1;
        for (NavigationLink l : links) {
          statement.setLong(index++, l.getParentId());
          statement.setLong(index++, l.getChildId());
          statement.setInt(index++, l.getDistance());
        }
        statement.execute();
        return null;
      }
    }.execute();
  }

  /**
   * @param id
   */
  public static void deleteByParentId(Long id) {
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("DELETE FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS)
            .append(" WHERE ").append(SQL.columnsAliased(TABLE_ALIAS, COL_PARENT_ID)).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, id);
        statement.execute();
        return null;
      }
    }.execute();
  }

  /**
   * @param id
   */
  public static void deleteByChildId(Long id) {
    new DbStatement<Void>() {
      @Override
      protected String getStatement() {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder
            .append("DELETE FROM ").append(TABLE_NAME).append(" AS ").append(TABLE_ALIAS)
            .append(" WHERE ").append(SQL.columnsAliased(TABLE_ALIAS, COL_CHILD_ID)).append(" = ? ");
        return sqlBuilder.toString();
      }

      @Override
      protected Void bindAndExecute(Connection connection, PreparedStatement statement) throws SQLException {
        int parameterIndex = 1;
        statement.setLong(parameterIndex++, id);
        statement.execute();
        return null;
      }
    }.execute();
  }
}
