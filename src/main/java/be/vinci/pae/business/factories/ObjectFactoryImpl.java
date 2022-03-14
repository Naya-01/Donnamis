package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.ObjectImpl;
import be.vinci.pae.business.domain.dto.ObjectDTO;

public class ObjectFactoryImpl implements ObjectFactory {

  /**
   * This function is used for the injection, it returns an implementation object.
   *
   * @return object implementation
   */
  @Override
  public ObjectDTO getObjectDTO() {
    return new ObjectImpl();
  }

}
