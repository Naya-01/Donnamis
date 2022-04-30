package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.AddressImpl;
import be.vinci.pae.business.domain.dto.AddressDTO;

public class AddressFactoryImpl implements AddressFactory {

  /**
   * This function is used for the injection, it returns an implementation address.
   *
   * @return address implementation
   */
  @Override
  public AddressDTO getAddressDTO() {
    return new AddressImpl();
  }
}
