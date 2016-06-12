package ch.ahoegger.photobox.db.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * <h3>{@link SqlTransactionFilter}</h3>
 *
 * @author aho
 */
public class SqlTransactionFilter implements Filter {

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // Pass request back down the filter chain
    try {
      chain.doFilter(request, response);
      DbConnection.commit();
    }
    catch (Exception e) {
      DbConnection.rollback();
      throw e;
    }
    finally {
      DbConnection.release();
    }
  }

  @Override
  public void destroy() {
  }

}
