package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;

public interface ObjectUCC {

  /**
   * Find an object with his id.
   *
   * @param id : id of the object.
   * @return objectDTO having this id.
   */
  ObjectDTO getObject(int id);
}
