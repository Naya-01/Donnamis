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
   * @return true if wrote successfully
   */
  String writeImageOnDisk(InputStream file, FormDataBodyPart fileMime, String path);
}
