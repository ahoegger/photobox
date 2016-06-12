package ch.ahoegger.photobox.dao;

/**
 * <h3>{@link NavigationLink}</h3>
 *
 * @author aho
 */
public class NavigationLink {

  private long m_parentId;
  private long m_childId;
  private int m_distance;

  public NavigationLink(long parentId, long childId, int distance) {
    m_parentId = parentId;
    m_childId = childId;
    m_distance = distance;
  }

  public long getParentId() {
    return m_parentId;
  }

  public void setParentId(long parentId) {
    m_parentId = parentId;
  }

  public long getChildId() {
    return m_childId;
  }

  public void setChildId(long childId) {
    m_childId = childId;
  }

  public int getDistance() {
    return m_distance;
  }

  public void setDistance(int distance) {
    m_distance = distance;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("parentId:").append(getParentId())
        .append(", childId:").append(getChildId())
        .append(", distance:").append(getDistance());
    return builder.toString();
  }

}
