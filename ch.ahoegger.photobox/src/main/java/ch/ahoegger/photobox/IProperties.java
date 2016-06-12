package ch.ahoegger.photobox;

import ch.ahoegger.photobox.dao.Folder;

public interface IProperties {

  String CONTEXT_PARAM_ORIGINAL_DIRECTORY = "ch.ahoegger.picturebox.original-directory";
  String CONTEXT_PARAM_WORKING_DIRECTORY = "ch.ahoegger.picturebox.working-directory";
  String CONTEXT_PARAM_DB_LOCATION = "ch.ahoegger.picturebox.dbLocation";

  Folder ROOT_FOLDER = new Folder().withId(0l);

  enum ImageType {
    Large, Medium, Small, Unknown
  }

  int RESOLUTION_LARGE = 2048;
  int RESOLUTION_MEDIUM = 800;
  int RESOLUTION_SMALL = 400;

}
