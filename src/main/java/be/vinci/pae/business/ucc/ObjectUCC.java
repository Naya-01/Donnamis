package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
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
}
