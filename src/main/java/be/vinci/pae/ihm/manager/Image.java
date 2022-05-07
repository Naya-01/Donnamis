package be.vinci.pae.ihm.manager;

import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

public interface Image {


  /**
   * Write image on disk.
   *
   * @param file     data of the image
   * @param fileMime MIME of the image
   * @param path     where to save the image
   */
  void writeImageOnDisk(InputStream file, FormDataBodyPart fileMime, String path,
      int filename);


  /**
   * Get the finalPath for the attributes.
   *
   * @param path     of the folder.
   * @param id       of the image.
   * @param fileMime MIME of the image.
   * @return the string of path.
   */
  String getInternalPath(String path, Integer id, FormDataBodyPart fileMime);

  /**
   * Check if the image is authorized.
   *
   * @param fileMime MIME of the image.
   * @return true if authorized.
   */
  boolean isAuthorized(FormDataBodyPart fileMime);
}
