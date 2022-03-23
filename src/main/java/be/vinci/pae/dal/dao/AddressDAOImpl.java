package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressDAOImpl implements AddressDAO {

  @Inject
  private DALBackendService dalBackendService;
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
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(
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
      throw new FatalException(e);
    }
  }

  /**
   * Add an address.
   *
   * @param addressDTO : address to add in the DB.
   * @return addressDTO added.
   */
  @Override
  public AddressDTO createOne(AddressDTO addressDTO) {
    // Insert in the db
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(
        "insert into donnamis.addresses (id_member, unit_number, building_number, street, "
            + "postcode, commune, country) values (?,?,?,?,?,?,?) RETURNING id_member;");
    try {
      preparedStatement.setInt(1, addressDTO.getIdMember());
      preparedStatement.setString(2, addressDTO.getUnitNumber());
      preparedStatement.setString(3, addressDTO.getBuildingNumber());
      preparedStatement.setString(4, addressDTO.getStreet());
      preparedStatement.setString(5, addressDTO.getPostcode());
      preparedStatement.setString(6, addressDTO.getCommune());
      preparedStatement.setString(7, addressDTO.getCountry());
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      //get id of new member
      int idNewAddressMember = resultSet.getInt(1);
      if (idNewAddressMember != addressDTO.getIdMember()) {
        return null;
      }

      preparedStatement.close();
      return addressDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Add values to an AddressDTO instance.
   *
   * @param addressDTO     the instance
   * @param idMember       the member id
   * @param unitNumber     the unit number
   * @param buildingNumber the building number
   * @param street         the street
   * @param postcode       the postcode
   * @param commune        the commune
   * @param country        the country
   */
  @Override
  public void setAddress(AddressDTO addressDTO, int idMember, String unitNumber,
      String buildingNumber, String street, String postcode, String commune, String country) {
    addressDTO.setIdMember(idMember);
    addressDTO.setStreet(street);
    addressDTO.setPostcode(postcode);
    addressDTO.setUnitNumber(unitNumber);
    addressDTO.setBuildingNumber(buildingNumber);
    addressDTO.setCommune(commune);
    addressDTO.setCountry(country);
  }
}
