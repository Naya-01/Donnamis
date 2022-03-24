package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.AddressDTO;

public interface AddressUCC {

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  AddressDTO updateOne(AddressDTO addressDTO);
}
