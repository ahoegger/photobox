package ch.ahoegger.photobox.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * Filter used on {@link HttpServletRequest#getPathInfo()}
 */
public class PathInfoFilter {
  private final Pattern m_pattern;

  /**
   * @param simplePattern
   *          pattern list (with wildcard '*') comma, newline or whitespace separated
   */
  public PathInfoFilter(String simplePattern) {
    this(simplePatternListToRegex(simplePattern));
  }

  public PathInfoFilter(Pattern pattern) {
    m_pattern = pattern;
  }

  /**
   * comma separated list of simple patterns with *
   */
  public static Pattern simplePatternListToRegex(String patList) {
    Set<String> patSet = new HashSet<>();
    if (patList != null) {
      for (String s : patList.split("[,\\s]")) {
        s = s.trim();
        if (!s.isEmpty()) {
          if (!s.startsWith("/")) {
            s = "/" + s;
          }
          s = simplePatternToRegex(s);
          if (s != null) {
            patSet.add(s);
          }
        }
      }
    }
    if (patSet.isEmpty()) {
      return null;
    }
    return Pattern.compile("(" + format(patSet, "|", false) + ")", Pattern.CASE_INSENSITIVE);
  }

  /**
   * pattern with *
   */
  public static String simplePatternToRegex(String s) {
    String r = Pattern.quote(s);//wrapped into \Q...\E
    // * -> \E.*\Q
    r = r.replace("*", "\\E.*\\Q");
    return r;
  }

  public boolean accepts(String pathInfo) {
    return pathInfo != null && m_pattern != null && m_pattern.matcher(pathInfo).matches();
  }

  public static <T> String format(Collection<T> list, String delimiter, boolean quoteStrings) {
    if (list == null || list.isEmpty()) {
      return "";
    }
    StringBuilder result = new StringBuilder();
    Iterator<T> it = list.iterator();
    // first
    result.append(objectToString(it.next(), quoteStrings));
    // rest
    while (it.hasNext()) {
      result.append(delimiter);
      result.append(objectToString(it.next(), quoteStrings));
    }
    return result.toString();
  }

  private static String objectToString(Object o, boolean quoteStrings) {
    if (o == null) {
      return "null";
    }
    if (o instanceof Number) {
      return o.toString();
    }
    if (quoteStrings) {
      return "'" + o.toString().replaceAll(",", "%2C") + "'";
    }
    else {
      return o.toString();
    }

  }

}
