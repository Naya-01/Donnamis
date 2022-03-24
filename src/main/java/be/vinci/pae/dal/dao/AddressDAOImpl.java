package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

public class AddressDAOImpl implements AddressDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private AddressFactory addressFactory;

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  @Override
  public AddressDTO updateOne(AddressDTO addressDTO) {
    Deque<String> addressDTODeque = new ArrayDeque<>();
    String query = "UPDATE donnamis.addresses SET ";

    if (addressDTO.getUnitNumber() != null && !addressDTO.getUnitNumber().isEmpty()) {
      query += "unit_number = ?,";
      addressDTODeque.addLast(addressDTO.getUnitNumber());
    }
    if (addressDTO.getBuildingNumber() != null && !addressDTO.getBuildingNumber().isEmpty()) {
      query += "building_number = ?,";
      addressDTODeque.addLast(addressDTO.getBuildingNumber());
    }
    if (addressDTO.getStreet() != null && !addressDTO.getStreet().isEmpty()) {
      query += "street = ?,";
      addressDTODeque.addLast(addressDTO.getStreet());
    }
    if (addressDTO.getPostcode() != null && !addressDTO.getPostcode().isEmpty()) {
      query += "postcode = ?,";
      addressDTODeque.addLast(addressDTO.getPostcode());
    }
    if (addressDTO.getCommune() != null && !addressDTO.getCommune().isEmpty()) {
      query += "commune = ?,";
      addressDTODeque.addLast(addressDTO.getCommune());
    }
    if (addressDTO.getCountry() != null && !addressDTO.getCountry().isEmpty()) {
      query += "country = ?,";
      addressDTODeque.addLast(addressDTO.getCountry());
    }
    query = query.substring(0, query.length() - 1);
    if (query.endsWith("SET")) {
      return null;
    }
    query += " WHERE id_member = ? RETURNING id_member, unit_number, building_number, street, "
        + "postcode, commune, country";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      int cnt = 1;
      for (String str : addressDTODeque) {
        preparedStatement.setString(cnt++, str);
      }
      preparedStatement.setInt(cnt, addressDTO.getIdMember());

      return getAddressByPreparedStatement(preparedStatement);
    } catch (SQLException throwables) {
      return null;
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
    String query = "INSERT INTO donnamis.addresses (id_member, unit_number, building_number, "
        + "street, postcode, commune, country) values (?,?,?,?,?,?,?) RETURNING id_member, "
        + "unit_number, building_number, street, postcode, commune, country";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, addressDTO.getIdMember());
      preparedStatement.setString(2, addressDTO.getUnitNumber());
      preparedStatement.setString(3, addressDTO.getBuildingNumber());
      preparedStatement.setString(4, addressDTO.getStreet());
      preparedStatement.setString(5, addressDTO.getPostcode());
      preparedStatement.setString(6, addressDTO.getCommune());
      preparedStatement.setString(7, addressDTO.getCountry());

      return getAddressByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Create an AddressDTO instance.
   *
   * @param idMember       the member id
   * @param unitNumber     the unit number
   * @param buildingNumber the building number
   * @param street         the street
   * @param postcode       the postcode
   * @param commune        the commune
   * @param country        the country
   * @return the addressDTO created
   */
  @Override
  public AddressDTO getAddress(int idMember, String unitNumber, String buildingNumber,
      String street, String postcode, String commune, String country) {

    AddressDTO addressDTO = addressFactory.getAddressDTO();
    addressDTO.setIdMember(idMember);
    addressDTO.setStreet(street);
    addressDTO.setPostcode(postcode);
    addressDTO.setUnitNumber(unitNumber);
    addressDTO.setBuildingNumber(buildingNumber);
    addressDTO.setCommune(commune);
    addressDTO.setCountry(country);
    return addressDTO;
  }

  /**
   * Get an addressDTO with a resultSet.
   *
   * @param preparedStatement a prepared statement that contain id_member, unit_number,
   *                          building_number, street, postcode, commune, country in this order
   * @return the matching addressDTO
   */
  private AddressDTO getAddressByPreparedStatement(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      return getAddress(resultSet.getInt(1), resultSet.getString(2),
          resultSet.getString(3), resultSet.getString(4),
          resultSet.getString(5), resultSet.getString(6),
          resultSet.getString(7));
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
