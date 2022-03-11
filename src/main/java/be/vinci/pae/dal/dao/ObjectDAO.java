package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import java.util.List;

public interface ObjectDAO {

  /**
   * Get an object we want to retrieve by his id.
   *
   * @param id : the id of the object that we want to retrieve
   * @return the object
   */
  ObjectDTO getOne(int id);

  /**
   * Get all objects that we want to retrieve by his status.
   *
   * @param status : the status of the objects that we want to retrieve
   * @return the object
   */
  List<ObjectDTO> getAllByStatus(String status);


  /**
   * Get all objects of a member that we want to retrieve by his id.
   *
   * @param idMember : take all object of this member.
   * @return list object of this member.
   */
  List<ObjectDTO> getAllObjectOfMember(int idMember);
}
