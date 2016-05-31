package ch.ahoegger.photobox.scale;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExecuterContext implements ServletContextListener {

  protected static Logger LOG = LogManager.getLogger(ExecuterContext.class);
  private ExecutorService executor;

  public void contextInitialized(ServletContextEvent sce) {
    ServletContext context = sce.getServletContext();
    int nr_executors = 1;
    ThreadFactory daemonFactory = new DaemonThreadFactory();
    try {
      nr_executors = Integer.parseInt(context.getInitParameter("nr-executors"));
    } catch (NumberFormatException ignore) {
    }

    if (nr_executors <= 1) {
      executor = Executors.newSingleThreadExecutor(daemonFactory);
    } else {
      executor = Executors.newFixedThreadPool(nr_executors, daemonFactory);
    }
    context.setAttribute("MY_EXECUTOR", executor);
  }

  public void contextDestroyed(ServletContextEvent sce) {
    LOG.debug("Shotdown Executer");
    executor.shutdown();
    try {
      if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
        LOG.debug("Executer will be killed.");
        executor.shutdownNow(); // or process/wait until all pending jobs are
                                // done
      } else {
        LOG.debug("Executer shutdown successfully.");
      }
    } catch (InterruptedException e) {

    }
  }
}
