package ch.ahoegger.photobox.dao;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FolderResource extends ResourceOld {

  public FolderResource(String name, String path) {
    super(name, path);
  }

  @Override
  public boolean isFolder() {
    return true;
  }

}
