package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeDAOImpl implements TypeDAO {

  @Inject
  private DALService dalService;
  @Inject
  private TypeFactory typeFactory;

  /**
   * Get all default types available.
   *
   * @return a list of default types
   */
  @Override
  public List<TypeDTO> getAllDefault() {
    String query =
        "SELECT t.id_type, t.is_default, t.type_name "
            + "FROM donnamis.types t "
            + "WHERE t.is_default = true "
            + "ORDER BY t.type_name";

    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      List<TypeDTO> listDefaultType = new ArrayList<>();

      while (resultSet.next()) {
        TypeDTO typeDTO = typeFactory.getTypeDTO();
        typeDTO.setIdType(resultSet.getInt(1));
        typeDTO.setDefault(resultSet.getBoolean(2));
        typeDTO.setTypeName(resultSet.getString(3));
        listDefaultType.add(typeDTO);
      }
      return listDefaultType;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
