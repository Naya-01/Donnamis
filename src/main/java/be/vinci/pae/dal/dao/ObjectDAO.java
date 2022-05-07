package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import java.util.List;

public interface ObjectDAO {


  /**
   * Update the object picture.
   *
   * @param path location of the picture.
   * @param id   of the object.
   * @return Object modified.
   */
  ObjectDTO updateObjectPicture(String path, int id);

  /**
   * Get an object we want to retrieve by his id.
   *
   * @param id : the id of the object that we want to retrieve
   * @return the object
   */
  ObjectDTO getOne(int id);

  
  /**
   * Get all objects of a member that we want to retrieve by his id.
   *
   * @param idMember : take all object of this member.
   * @return list object of this member.
   */
  List<ObjectDTO> getAllObjectOfMember(int idMember);

  /**
   * Add object in the database with all information.
   *
   * @param objectDTO : object that we want to add in the database.
   * @return object with his id.
   */
  ObjectDTO addOne(ObjectDTO objectDTO);

  /**
   * Update an object.
   *
   * @param objectDTO : object that we want to update.
   * @return object updated
   */
  ObjectDTO updateOne(ObjectDTO objectDTO);
}
