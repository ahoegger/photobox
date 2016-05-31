package ch.ahoegger.photobox.dao;

import java.io.Serializable;
import java.util.Date;

public class Picture implements Serializable {
  private static final long serialVersionUID = 1L;

  private long m_id;
  private String m_name;
  private Date m_captureDate;
  private long m_folderId;
  private int m_rotation;
  private boolean m_active;
//  @JsonIgnore
  private String m_pathOrignal;
//  @JsonIgnore
  private String m_pathSmall;
//  @JsonIgnore
  private String m_pathMedium;
//  @JsonIgnore
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

  public int getRotation() {
    return m_rotation;
  }

  public Picture withRotation(int rotation) {
    m_rotation = rotation;
    return this;
  }

  public boolean isActive() {
    return m_active;
  }

  public Picture withActive(boolean active) {
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
}
