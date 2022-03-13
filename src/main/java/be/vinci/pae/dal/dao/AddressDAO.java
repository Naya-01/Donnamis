package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;

public interface AddressDAO {

  /**
   * Update an address.
   *
   * @param idMember       : the id of the member that have this address
   * @param unitNumber     : the unit number
   * @param buildingNumber : the building number
   * @param street         : the name of the street
   * @param postcode       : the postcode
   * @param commune        : the name of the commune
   * @param country        : the name of the country
   * @return the updated address of the member
   */
  AddressDTO updateOne(int idMember, String unitNumber, String buildingNumber, String street,
      String postcode, String commune, String country);

  /**
   * Create an address for the member.
   *
   * @param idMember       : the id of the member that will have this address
   * @param unitNumber     : the unit number
   * @param buildingNumber : the building number
   * @param street         : the name of the street
   * @param postcode       : the postcode
   * @param commune        : the name of the commune
   * @param country        : the name of the country
   * @return the new address for the member
   */
  AddressDTO createOne(int idMember, String unitNumber, String buildingNumber, String street,
      String postcode, String commune, String country);
}
