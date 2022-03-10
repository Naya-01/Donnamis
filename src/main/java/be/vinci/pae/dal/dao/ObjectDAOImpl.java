package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectDAOImpl implements ObjectDAO {

  @Inject
  private DALService dalService;
  @Inject
  private ObjectFactory objectFactory;

  /**
   * Get an object we want to retrieve by his id.
   *
   * @param id : the id of the object that we want to retrieve
   * @return the object
   */
  @Override
  public ObjectDTO getOne(int id) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "SELECT id_object, id_type, description, status, image, id_offeror "
            + "FROM donnamis.objects WHERE id_object = ?");

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    try {
      preparedStatement.setInt(1, id);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      objectDTO.setIdObject(resultSet.getInt(1));
      objectDTO.setType(null);
      objectDTO.setDescription(resultSet.getString(3));
      objectDTO.setStatus(resultSet.getString(4));
      objectDTO.setImage(resultSet.getString(5));
      objectDTO.setIdOfferor(resultSet.getInt(6));

      resultSet.close();
      preparedStatement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return objectDTO;
  }

}
