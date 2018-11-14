package ch.ahoegger.photobox.quarz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.listeners.SchedulerListenerSupport;

/**
 * <h3>{@link AbstractCanceableJob}</h3>
 *
 * @author aho
 */
public abstract class AbstractCanceableJob implements Job {

  private P_SchedulerListener m_schedulerListener = new P_SchedulerListener();
  private boolean m_canceled = false;

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {

      context.getScheduler().getListenerManager().addSchedulerListener(m_schedulerListener);
      execute(new IMonitor() {

        @Override
        public boolean isCanceled() {
          return m_canceled;
        }
      });

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      try {
        context.getScheduler().getListenerManager().removeSchedulerListener(m_schedulerListener);
      }
      catch (SchedulerException e) {
        //void
      }
    }
  }

  protected abstract void execute(IMonitor monitor);

  private class P_SchedulerListener extends SchedulerListenerSupport {
    @Override
    public void schedulerShuttingdown() {
      m_canceled = true;
    }
  }

  public interface IMonitor {
    boolean isCanceled();
  }

}
