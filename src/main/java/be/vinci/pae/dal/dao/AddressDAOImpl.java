package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddressDAOImpl implements AddressDAO {

  @Inject
  private DALService dalService;
  @Inject
  private AddressFactory addressFactory;

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
  @Override
  public AddressDTO updateOne(int idMember, String unitNumber, String buildingNumber, String street,
      String postcode, String commune, String country) {
    // Update in the db
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "UPDATE donnamis.addresses "
            + "SET unit_number = ?,"
            + " building_number = ?,"
            + " street = ?,"
            + " postcode = ?,"
            + " commune = ?,"
            + " country = ?"
            + "WHERE id_member = ?");
    try {
      if (unitNumber.length() == 0) {
        preparedStatement.setNull(1, java.sql.Types.NULL);
      } else {
        preparedStatement.setString(1, unitNumber);
      }
      preparedStatement.setString(2, buildingNumber);
      preparedStatement.setString(3, street);
      preparedStatement.setString(4, postcode);
      preparedStatement.setString(5, commune);
      preparedStatement.setString(6, country);
      preparedStatement.setInt(7, idMember);
      preparedStatement.execute();
      preparedStatement.close();

      // Creation of the new Address
      AddressDTO addressDTO = addressFactory.getAddressDTO();
      setAddress(addressDTO, idMember, unitNumber, buildingNumber, street, postcode, commune,
          country);
      return addressDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

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

  /**
   * Add an address
   *
   * @param addressDTO : address to add in the DB
   * @return addressDTO added
   */
  @Override
  public AddressDTO createOne(AddressDTO addressDTO) {
    // Insert in the db
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "INSERT INTO donnamis.addresses (id_member, unit_member, building_number, street,"
            + " postcode, commune, country)"
            + "VALUES(?,?,?,?,?,?,?)");
    try {
      preparedStatement.setInt(1, addressDTO.getIdMember());
      preparedStatement.setString(2, addressDTO.getUnitNumber());
      preparedStatement.setString(3, addressDTO.getBuildingNumber());
      preparedStatement.setString(4, addressDTO.getStreet());
      preparedStatement.setString(5, addressDTO.getPostCode());
      preparedStatement.setString(6, addressDTO.getCommune());
      preparedStatement.setString(7, addressDTO.getCountry());
      preparedStatement.executeQuery();
      preparedStatement.close();

      //vérifier s'il a été ajouté
      //ou mieux de faire getOne dans UCC?

      return addressDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }


  private void setAddress(AddressDTO addressDTO, int idMember, String unitNumber,
      String buildingNumber, String street,
      String postcode, String commune, String country) {
    addressDTO.setIdMember(idMember);
    addressDTO.setStreet(street);
    addressDTO.setPostcode(postcode);
    addressDTO.setUnitNumber(unitNumber);
    addressDTO.setBuildingNumber(buildingNumber);
    addressDTO.setCommune(commune);
    addressDTO.setCountry(country);
  }
}
