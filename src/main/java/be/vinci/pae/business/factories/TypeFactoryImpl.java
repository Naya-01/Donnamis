package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.TypeImpl;
import be.vinci.pae.business.domain.dto.TypeDTO;

public class TypeFactoryImpl implements TypeFactory {

  /**
   * This function is used for the injection, it returns an implementation type.
   *
   * @return type implementation
   */
  @Override
  public TypeDTO getTypeDTO() {
    return new TypeImpl();
  }

}
