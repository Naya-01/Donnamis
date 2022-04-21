package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;

public interface AddressDAO {

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  AddressDTO updateOne(AddressDTO addressDTO);

  /**
   * Add an address.
   *
   * @param addressDTO : address to add in the DB.
   * @return addressDTO added.
   */
  AddressDTO createOne(AddressDTO addressDTO);

  /**
   * Create an AddressDTO instance.
   *
   * @param idMember       the member id
   * @param unitNumber     the unit number
   * @param buildingNumber the building number
   * @param street         the street
   * @param postcode       the postcode
   * @param commune        the commune
   * @return the addressDTO created
   */
  AddressDTO createAddressDTO(int idMember, String unitNumber, String buildingNumber,
      String street, String postcode, String commune);

  /**
   * An address of a member by his member id.
   *
   * @param idMember the id of the member address
   * @return an AddressDTO
   */
  AddressDTO getAddressByMemberId(int idMember);

}
