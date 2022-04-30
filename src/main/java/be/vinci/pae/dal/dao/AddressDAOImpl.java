package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class AddressDAOImpl implements AddressDAO {

  @Inject
  private AddressFactory addressFactory;
  @Inject
  private DALBackendService dalBackendService;

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  @Override
  public AddressDTO updateOne(AddressDTO addressDTO) {
    LinkedList<String> addressDTOList = new LinkedList<>();
    String query = "UPDATE donnamis.addresses SET version=version+1, ";

    query += "unit_number = ?,";
    addressDTOList.addLast(addressDTO.getUnitNumber());

    if (addressDTO.getBuildingNumber() != null && !addressDTO.getBuildingNumber().isBlank()) {
      query += "building_number = ?,";
      addressDTOList.addLast(addressDTO.getBuildingNumber());
    }
    if (addressDTO.getStreet() != null && !addressDTO.getStreet().isBlank()) {
      query += "street = ?,";
      addressDTOList.addLast(addressDTO.getStreet());
    }
    if (addressDTO.getPostcode() != null && !addressDTO.getPostcode().isBlank()) {
      query += "postcode = ?,";
      addressDTOList.addLast(addressDTO.getPostcode());
    }
    if (addressDTO.getCommune() != null && !addressDTO.getCommune().isBlank()) {
      query += "commune = ?,";
      addressDTOList.addLast(addressDTO.getCommune());
    }
    query = query.substring(0, query.length() - 1);
    if (query.endsWith("SET")) {
      return null;
    }
    query += " WHERE id_member = ? RETURNING id_member, unit_number, building_number, street, "
        + "postcode, commune, version ";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      int cnt = 1;
      for (String str : addressDTOList) {
        preparedStatement.setString(cnt++, str);
      }
      preparedStatement.setInt(cnt, addressDTO.getIdMember());

      return getAddressByPreparedStatement(preparedStatement);
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
    String query = "INSERT INTO donnamis.addresses (id_member, unit_number, building_number, "
        + "street, postcode, commune, version) values (?,?,?,?,?,?,?) RETURNING id_member, "
        + "unit_number, building_number, street, postcode, commune, version";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, addressDTO.getIdMember());
      preparedStatement.setString(2, addressDTO.getUnitNumber());
      preparedStatement.setString(3, addressDTO.getBuildingNumber());
      preparedStatement.setString(4, addressDTO.getStreet());
      preparedStatement.setString(5, addressDTO.getPostcode());
      preparedStatement.setString(6, addressDTO.getCommune());
      preparedStatement.setInt(7, 1);

      return getAddressByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * An address of a member by his member id.
   *
   * @param idMember the id of the member address
   * @return an AddressDTO
   */
  @Override
  public AddressDTO getAddressByMemberId(int idMember) {
    String query = "SELECT id_member, unit_number, building_number, street, postcode, commune, "
        + "version FROM donnamis.addresses WHERE id_member = ?";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idMember);
      return getAddressByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  /**
   * Get an addressDTO with a resultSet.
   *
   * @param preparedStatement a prepared statement that contain id_member, unit_number,
   *                          building_number, street, postcode, commune in this order
   * @return the matching addressDTO
   */
  private AddressDTO getAddressByPreparedStatement(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      return createAddressDTO(resultSet.getInt(1), resultSet.getString(2),
          resultSet.getString(3), resultSet.getString(4),
          resultSet.getString(5), resultSet.getString(6), resultSet.getInt(7));
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
   * @return the addressDTO created
   */
  private AddressDTO createAddressDTO(int idMember, String unitNumber, String buildingNumber,
      String street, String postcode, String commune, int version) {

    AddressDTO addressDTO = addressFactory.getAddressDTO();
    addressDTO.setIdMember(idMember);
    addressDTO.setStreet(street);
    addressDTO.setPostcode(postcode);
    addressDTO.setUnitNumber(unitNumber);
    addressDTO.setBuildingNumber(buildingNumber);
    addressDTO.setCommune(commune);
    addressDTO.setVersion(version);
    return addressDTO;
  }
}
