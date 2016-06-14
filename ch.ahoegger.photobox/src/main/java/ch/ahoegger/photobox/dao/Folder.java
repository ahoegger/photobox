package ch.ahoegger.photobox.dao;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Folder implements Serializable {
  private static final long serialVersionUID = 1L;
  public static Folder ROOT = new Folder().withId(0L).withPathOrignal("/");

  private Long m_id;
  private String m_name;
  private Long m_parentId;
  private Boolean m_active;

  // bean only
  private Folder m_parent;
  private List<Folder> m_subFolders;
  private List<Picture> m_pictures;

  private String m_pathOrignal;
  private int m_totalPictureCount;
  private List<Picture> m_previewPictures;

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

  public void setName(String name) {
    m_name = name;
  }

  public Long getParentId() {
    return m_parentId;
  }

  public Folder withParentId(Long parentId) {
    setParentId(parentId);
    return this;
  }

  public void setParentId(Long parentId) {
    m_parentId = parentId;
  }

  public Boolean getActive() {
    return m_active;
  }

  public Folder withActive(Boolean active) {
    setActive(active);
    return this;
  }

  public void setActive(Boolean active) {
    m_active = active;
  }

  public String getPathOrignal() {
    return m_pathOrignal;
  }

  public Folder withPathOrignal(String pathOrignal) {
    setPathOrignal(pathOrignal);
    return this;
  }

  public void setPathOrignal(String pathOrignal) {
    m_pathOrignal = pathOrignal;
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

  public void setTotalPictureCount(int totalPictureCount) {
    m_totalPictureCount = totalPictureCount;
  }

  public int getTotalPictureCount() {
    return m_totalPictureCount;
  }

  public void setPreviewPictures(List<Picture> previewPictures) {
    m_previewPictures = previewPictures;
  }

  public List<Picture> getPreviewPictures() {
    return m_previewPictures;
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
