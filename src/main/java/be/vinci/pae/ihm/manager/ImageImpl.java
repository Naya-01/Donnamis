package be.vinci.pae.ihm.manager;

import be.vinci.pae.utils.Config;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

public class ImageImpl implements Image {

  private static String[] typesAllowed = {"png", "jpg", "jpeg"};

  /**
   * Write image on disk.
   *
   * @param file     data of the image
   * @param fileMime MIME of the image
   * @param path     where to save the image
   */
  @Override
  public void writeImageOnDisk(InputStream file, FormDataBodyPart fileMime, String path,
      int filename) {

    if (!isAuthorized(fileMime)) {
      return;
    }

    for (String extension : typesAllowed) {
      String testPath =
          Config.getProperty("ImagePath") + "img\\" + path + filename + "."
              + extension;
      File f = new File(testPath);
      if (f.exists()) {
        f.delete();
      }
    }

    try {

      String finalPath =
          Config.getProperty("ImagePath") + getInternalPath(path, filename, fileMime);
      File f = new File(finalPath);
      if (f.exists()) {
        f.delete();
      }
      Files.copy(file, Paths.get(finalPath));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Get the finalPath for the attributes.
   *
   * @param path     of the folder.
   * @param id       of the image.
   * @param fileMime MIME of the image.
   * @return the string of path.
   */
  @Override
  public String getInternalPath(String path, Integer id, FormDataBodyPart fileMime) {
    return "img\\" + path + id + "." + fileMime.getMediaType().getSubtype().toLowerCase();
  }

  /**
   * Check if the image is authorized.
   *
   * @param fileMime MIME of the image.
   * @return true if authorized.
   */
  @Override
  public boolean isAuthorized(FormDataBodyPart fileMime) {
    String type = fileMime.getMediaType().getSubtype().toLowerCase();
    Boolean authorized = false;
    for (String t : typesAllowed) {
      if (type.equals(t)) {
        authorized = true;
      }
    }
    return authorized;
  }
}
