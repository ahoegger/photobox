package ch.ahoegger.photobox.dao;

import java.io.Serializable;
import java.util.List;

public class Folder implements Serializable {
  private static final long serialVersionUID = 1L;
  public static Folder ROOT = new Folder().withId(0L).withPathOrignal("/");

  private Long m_id;
  private String m_name;
  private Long m_parentId;
  private boolean m_active;

  // bean only
  private Folder m_parent;
  private List<Folder> m_subFolders;
  private List<Picture> m_pictures;

  private String m_pathOrignal;

  public Long getId() {
    return m_id;
  }

  public Folder withId(Long id) {
    m_id = id;
    return this;
  }

  public String getName() {
    return m_name;
  }

  public Folder withName(String name) {
    m_name = name;
    return this;
  }

  public Long getParentId() {
    return m_parentId;
  }

  public Folder withParentId(Long parentId) {
    m_parentId = parentId;
    return this;
  }

  public boolean isActive() {
    return m_active;
  }

  public Folder withActive(boolean active) {
    m_active = active;
    return this;
  }

  public String getPathOrignal() {
    return m_pathOrignal;
  }

  public Folder withPathOrignal(String pathOrignal) {
    m_pathOrignal = pathOrignal;
    return this;
  }

  public void setParent(Folder parent) {
    m_parent = parent;
  }

  public Folder getParent() {
    return m_parent;
  }

  public void setSubFolders(List<Folder> subFolders) {
    m_subFolders = subFolders;
  }

  public List<Folder> getSubFolders() {
    return m_subFolders;
  }

  public void setPictures(List<Picture> pictures) {
    m_pictures = pictures;
  }

  public List<Picture> getPictures() {
    return m_pictures;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("{")
        .append("id:").append(getId())
        .append(", name:").append(getName())
        .append(", parentId:").append(getParentId())
        .append(", origPath:").append(getPathOrignal())
        .append("}");
    return builder.toString();
  }

}
