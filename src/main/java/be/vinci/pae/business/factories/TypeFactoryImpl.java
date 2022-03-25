package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.TypeImpl;
import be.vinci.pae.business.domain.dto.TypeDTO;

public class TypeFactoryImpl implements TypeFactory {

  @Override
  public TypeDTO getTypeDTO() {
    return new TypeImpl();
  }

}
