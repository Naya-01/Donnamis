package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import java.awt.image.BufferedImage;
import java.util.List;

public interface ObjectUCC {

  /**
   * Assign the object to a member.
   *
   * @param objectDTO to be assigned.
   * @param idMember  to be assigned.
   * @return objectDTO updated.
   */
  ObjectDTO assignObject(ObjectDTO objectDTO, int idMember);

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
   * @param id of the oject
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

  ObjectDTO updateOne(ObjectDTO objectDTO);

  /**
   * Update the object picture.
   *
   * @param internalPath location of the picture.
   * @param id           of the object.
   * @return Object modified.
   */
  ObjectDTO updateObjectPicture(String internalPath, int id);

  /**
   * Cancel an Object.
   *
   * @param objectDTO object with his id & new status to 'cancelled'
   * @return an object
   */
  ObjectDTO cancelObject(ObjectDTO objectDTO);

  /**
   * Give an Object.
   *
   * @param objectDTO object with his id & new status to 'given'
   * @return an object
   */
  ObjectDTO giveObject(ObjectDTO objectDTO);
}
