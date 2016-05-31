package ch.ahoegger.photobox.db.util;

import java.sql.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SQL {

  public static final String WHERE_DEFAULT = "WHERE 1 = 1";

  public static String columns(String... columns) {
    Stream<String> columnStream = Stream.of(columns);
    return columnStream.collect(Collectors.joining(", "));
  }

  public static String columnsAliased(String alias, String... columns) {
    Stream<String> columnStream = Stream.of(columns);
    return columnStream.map(c -> alias + "." + c).collect(Collectors.joining(", "));
  }

  public static String whereStringContains(String tableAlias, String column, String text) {
    return whereStringContains(tableAlias + "." + column, text);
  }

  public static String whereStringContains(String column, String text) {
    StringBuilder statementBuilder = new StringBuilder();
    statementBuilder.append("UPPER(").append(column).append(") LIKE UPPER('");
    statementBuilder.append("%").append(text).append("%')");
    return statementBuilder.toString();
  }

  public static Date toSqlDate(java.util.Date uDate) {
    if (uDate == null) {
      return null;
    }
    return new Date(uDate.getTime());
  }
}
