package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddressDAOImpl implements AddressDAO {

  @Inject
  private AddressFactory addressFactory;
  @Inject
  private AbstractDAO abstractDAO;

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  @Override
  public AddressDTO updateOne(AddressDTO addressDTO) {
    List<Class> types = new ArrayList<>();
    types.add(AddressDTO.class);

    List<Object> conditionValues = new ArrayList<>();
    conditionValues.add(addressDTO.getIdMember());

    Map<String, Object> toUpdate = new HashMap<>();
    toUpdate.put("unit_number", addressDTO.getUnitNumber());

    if (addressDTO.getBuildingNumber() != null && !addressDTO.getBuildingNumber().isBlank()) {
      toUpdate.put("building_number", addressDTO.getBuildingNumber());
    }
    if (addressDTO.getStreet() != null && !addressDTO.getStreet().isBlank()) {
      toUpdate.put("street", addressDTO.getStreet());
    }
    if (addressDTO.getPostcode() != null && !addressDTO.getPostcode().isBlank()) {
      toUpdate.put("postcode", addressDTO.getPostcode());
    }
    if (addressDTO.getCommune() != null && !addressDTO.getCommune().isBlank()) {
      toUpdate.put("commune", addressDTO.getCommune());
    }

    String condition =
        "id_member = ? RETURNING id_member, unit_number, building_number, street, "
            + "postcode, commune";

    try (PreparedStatement preparedStatement = abstractDAO.updateOne(toUpdate, condition,
        conditionValues, types)) {
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
    Map<String, Object> setters = new HashMap<>();
    setters.put("id_member", addressDTO.getIdMember());
    setters.put("unit_number", addressDTO.getUnitNumber());
    setters.put("building_number", addressDTO.getBuildingNumber());
    setters.put("street", addressDTO.getStreet());
    setters.put("postcode", addressDTO.getPostcode());
    setters.put("commune", addressDTO.getCommune());

    List<Class> types = new ArrayList<>();
    types.add(AddressDTO.class);

    try (PreparedStatement preparedStatement = abstractDAO.insertOne(setters, types)) {

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
   * @return the addressDTO created
   */
  @Override
  public AddressDTO createAdressDTO(int idMember, String unitNumber, String buildingNumber,
      String street, String postcode, String commune) {

    AddressDTO addressDTO = addressFactory.getAddressDTO();
    addressDTO.setIdMember(idMember);
    addressDTO.setStreet(street);
    addressDTO.setPostcode(postcode);
    addressDTO.setUnitNumber(unitNumber);
    addressDTO.setBuildingNumber(buildingNumber);
    addressDTO.setCommune(commune);
    return addressDTO;
  }

  /**
   * An address of a member by his member id.
   *
   * @param idMember the id of the member address
   * @return an AddressDTO
   */
  @Override
  public AddressDTO getAddressByMemberId(int idMember) {
    String condition = "id_member = ?";
    List<Object> values = new ArrayList<>();
    values.add(idMember);
    ArrayList<Class> types = new ArrayList<>();
    types.add(AddressDTO.class);
    try (PreparedStatement preparedStatement = abstractDAO.getOne(condition, values, types)) {
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
      return createAdressDTO(resultSet.getInt(1), resultSet.getString(2),
          resultSet.getString(3), resultSet.getString(4),
          resultSet.getString(5), resultSet.getString(6));
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
