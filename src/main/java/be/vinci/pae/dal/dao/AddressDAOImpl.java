package be.vinci.pae.dal.dao;

import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddressDAOImpl implements AddressDAO {

  @Inject
  private DALService dalService;


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
  @Override
  public int addOneAddress(String unit_number, String building_number, String street,
      String postcode, String commune, String country) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "insert into donnamis.addresses (unit_number, building_number, street, postcode, "
            + "commune, country) values (?,?,?,?,?) RETURNING id_address;");
    try {

      preparedStatement.setString(1, unit_number);
      preparedStatement.setString(2, building_number);
      preparedStatement.setString(3, street);
      preparedStatement.setString(4, postcode);
      preparedStatement.setString(5, commune);
      preparedStatement.setString(6, country);

      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();

      if (!resultSet.next()) {
        return -1;
      }

      return resultSet.getInt(1); //returns the id of the new address
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return -1;
  }
}
