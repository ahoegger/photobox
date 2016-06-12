package ch.ahoegger.photobox.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Events with a level below the specified min level or above the specified max level will be denied. Events with a
 * within the specified interval will trigger a FilterReply.NEUTRAL result, to allow the rest of the filter chain
 * process the event
 *
 * @since 5.1
 */
public class LevelRangeFilter extends Filter<ILoggingEvent> {

  private Level m_levelMin;
  private Level m_levelMax;

  @Override
  public FilterReply decide(ILoggingEvent event) {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }

    if (event.getLevel().isGreaterOrEqual(m_levelMin) && m_levelMax.isGreaterOrEqual(event.getLevel())) {
      return FilterReply.NEUTRAL;
    }
    else {
      return FilterReply.DENY;
    }
  }

  public void setLevelMin(String levelMin) {
    m_levelMin = Level.toLevel(levelMin);
  }

  public void setLevelMax(String levelMax) {
    m_levelMax = Level.toLevel(levelMax);
  }

  @Override
  public void start() {
    if (m_levelMin != null || m_levelMax != null) {
      if (m_levelMin == null) {
        m_levelMin = Level.TRACE;
      }
      if (m_levelMax == null) {
        m_levelMax = Level.ERROR;
      }
      super.start();
    }
  }
}
