package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import java.awt.image.BufferedImage;
import java.util.List;

public interface ObjectUCC {


  /**
   * Find an object with his id.
   *
   * @param id : id of the object.
   * @return objectDTO having this id.
   */
  ObjectDTO getObject(int id);

  /**
   * Get the picture of an object.
   *
   * @param id of the object
   * @return picture as file
   */
  BufferedImage getPicture(int id);

  /**
   * Find all object of a member.
   *
   * @param idMember : id member that we want to get all his object.
   * @return object list of this member.
   */
  List<ObjectDTO> getAllObjectMember(int idMember);

  /**
   * Update an object.
   *
   * @param objectDTO : object that we want to update.
   * @return object updated
   */
  ObjectDTO updateOne(ObjectDTO objectDTO);

  /**
   * Update the object picture.
   *
   * @param internalPath location of the picture.
   * @param id           of the object.
   * @param memberId     owner of the object.
   * @param version      of the object
   * @return Object updated.
   */
  ObjectDTO updateObjectPicture(String internalPath, int id, Integer memberId, int version);

}
