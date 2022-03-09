package be.vinci.pae.dal.dao;

public interface AddressDAO {

  /**
   * Add an address in the DB.
   *
   * @param unit_number     : unit number of the address.
   * @param building_number : building number of the address.
   * @param street          : street of the address.
   * @param postcode        : postcode of the address
   * @param commune         : commune of the address
   * @param country         : country of the address
   * @return the id of the new address in the DB.
   */
  int addOneAddress(String unit_number, String building_number, String street,
      String postcode, String commune, String country);
}
