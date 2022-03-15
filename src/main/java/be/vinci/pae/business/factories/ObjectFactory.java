package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.ObjectDTO;

public interface ObjectFactory {

  /**
   * This function is used for the injection, it returns an implementation object.
   *
   * @return object implementation
   */
  ObjectDTO getObjectDTO();
}
