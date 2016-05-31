package ch.ahoegger.photobox.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Resource {

  private final String name;
  private final boolean folder;

  private String m_path;
  private String m_parentPath;

  private final List<Link> links = new ArrayList<Link>();
  private final List<Resource> children = new ArrayList<Resource>();
  private boolean childrenLoaded = false;
  private Date m_capturedDate;

  public Resource(String name, boolean folder) {
    this.name = name;
    this.folder = folder;
  }

  public String getName() {
    return name;
  }

  public boolean isFolder() {
    return folder;
  }

  public void setPath(String path) {
    m_path = path;
  }

  public String getPath() {
    return m_path;
  }

  public String getParentPath() {
    return m_parentPath;
  }

  public void setParentPath(String parentPath) {
    m_parentPath = parentPath;
  }

  public void setCapturedDate(Date capturedDate) {
    m_capturedDate = capturedDate;
  }

  public Date getCapturedDate() {
    return m_capturedDate;
  }

  public void addLink(Link link) {
    links.add(link);
  }

  public List<Link> getLinks() {
    return links;
  }

  public void addChild(Resource resource) {
    children.add(resource);
  }

  public List<Resource> getChildren() {
    return children;
  }

  public void setChildrenLoaded(boolean childrenLoaded) {
    this.childrenLoaded = childrenLoaded;
  }

  public boolean isChildrenLoaded() {
    return childrenLoaded;
  }

}
