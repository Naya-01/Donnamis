package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.AddressDTO;

public interface AddressFactory {

  /**
   * This function is used for the injection, it returns an implementation address.
   *
   * @return address implementation
   */
  AddressDTO getAddressDTO();
}
