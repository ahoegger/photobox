package ch.ahoegger.photobox.dao;

public abstract class ResourceOld {

  private String name;
  private String path;
  private String uri;

  public ResourceOld(String name, String path) {
    this.name = name;
    this.path = path;
    uri = "resources/" + path + "/" + name;
  }

  public String getName() {
    return name;
  }

  public String getPath() {
    return path;
  }

  public String getUri() {
    return uri;
  }

  public abstract boolean isFolder();

  @Override
  public String toString() {
    return getName();
  }
}
