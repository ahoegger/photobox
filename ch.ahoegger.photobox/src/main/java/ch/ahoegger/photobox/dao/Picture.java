package ch.ahoegger.photobox.dao;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Picture implements Serializable {
  private static final long serialVersionUID = 1L;

  private long m_id;

  private String m_name;
  private Date m_captureDate;
  private long m_folderId;
  private Integer m_rotation;
  private Boolean m_active;
  private String m_pathOrignal;
  private String m_pathSmall;
  private String m_pathMedium;
  private String m_pathLarge;

  public long getId() {
    return m_id;
  }

  public Picture withId(long id) {
    m_id = id;
    return this;
  }

  public String getName() {
    return m_name;
  }

  public Picture withName(String name) {
    m_name = name;
    return this;
  }

  public Date getCaptureDate() {
    return m_captureDate;
  }

  public Picture withCaptureDate(Date captureDate) {
    m_captureDate = captureDate;
    return this;
  }

  public long getFolderId() {
    return m_folderId;
  }

  public Picture withFolderId(long folderId) {
    m_folderId = folderId;
    return this;
  }

  public Integer getRotation() {
    return m_rotation;
  }

  public Picture withRotation(Integer rotation) {
    m_rotation = rotation;
    return this;
  }

  public Boolean getActive() {
    return m_active;
  }

  public Picture withActive(Boolean active) {
    m_active = active;
    return this;
  }

//  @JsonIgnore
  public String getPathOrignal() {
    return m_pathOrignal;
  }

  public Picture withPathOrignal(String pathOrignal) {
    m_pathOrignal = pathOrignal;
    return this;
  }

  public String getPathSmall() {
    return m_pathSmall;
  }

  public Picture withPathSmall(String pathSmall) {
    m_pathSmall = pathSmall;
    return this;
  }

  public String getPathMedium() {
    return m_pathMedium;
  }

  public Picture withPathMedium(String pathMedium) {
    m_pathMedium = pathMedium;
    return this;
  }

  public String getPathLarge() {
    return m_pathLarge;
  }

  public Picture withPathLarge(String pathLarge) {
    m_pathLarge = pathLarge;
    return this;
  }

  public void setId(long id) {
    m_id = id;
  }

  public void setName(String name) {
    m_name = name;
  }

  public void setCaptureDate(Date captureDate) {
    m_captureDate = captureDate;
  }

  public void setFolderId(long folderId) {
    m_folderId = folderId;
  }

  public void setRotation(Integer rotation) {
    m_rotation = rotation;
  }

  public void setActive(Boolean active) {
    m_active = active;
  }

  public void setPathOrignal(String pathOrignal) {
    m_pathOrignal = pathOrignal;
  }

  public void setPathSmall(String pathSmall) {
    m_pathSmall = pathSmall;
  }

  public void setPathMedium(String pathMedium) {
    m_pathMedium = pathMedium;
  }

  public void setPathLarge(String pathLarge) {
    m_pathLarge = pathLarge;
  }

}
