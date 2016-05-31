package ch.ahoegger.photobox.dao;


public class Link {

  private final String m_rel;
  private final String m_url;

  public Link(String rel, String url) {
    m_rel = rel;
    m_url = url;
  }

  public String getRel() {
    return m_rel;
  }

  public String getUrl() {
    return m_url;
  }
}
