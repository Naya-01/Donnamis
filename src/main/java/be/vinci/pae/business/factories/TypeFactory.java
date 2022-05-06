package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.TypeDTO;

public interface TypeFactory {

  /**
   * This function is used for the injection, it returns an implementation type.
   *
   * @return type implementation
   */
  TypeDTO getTypeDTO();

}
