package be.vinci.pae.ihm.manager;

import be.vinci.pae.utils.Config;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

public class ImageImpl implements Image {


  /**
   * Write image on disk.
   *
   * @param file     data of the image
   * @param fileMime MIME of the image
   * @param path     where to save the image
   * @return true if wrote successfully
   */
  @Override
  public String writeImageOnDisk(InputStream file, FormDataBodyPart fileMime, String path) {

    String type = fileMime.getMediaType().getSubtype();
    String[] typesAllowed = {"png", "jpg", "jpeg"};
    boolean authorizedType = false;
    for (String t : typesAllowed) {
      if (type.equals(t)) {
        authorizedType = true;
      }
    }

    if (!authorizedType) {
      return null;
    }

    String internalPath = null;
    try {
      internalPath =
          "img\\" + path + UUID.randomUUID() + "." + fileMime.getMediaType().getSubtype();
      String finalPath = Config.getProperty("ImagePath") + internalPath;
      Files.copy(file, Paths.get(finalPath));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return internalPath;
  }
}
