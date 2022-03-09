package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;

public interface ObjectDAO {

  /**
   * Get an object we want to retrieve by his id.
   *
   * @param id : the id of the object that we want to retrieve
   * @return the object
   */
  ObjectDTO getOne(int id);
}
