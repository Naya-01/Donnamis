package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.services.DALBackendService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeDAOImpl implements TypeDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private TypeFactory typeFactory;

  /**
   * Get a type we want to retrieve by his type name.
   *
   * @param typeName : the typeName of the type we want to retrieve
   * @return the type
   */
  @Override
  public TypeDTO getOne(String typeName) {
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(
        "SELECT id_type, type_name, is_default FROM donnamis.types WHERE type_name = ?");
    try {
      preparedStatement.setString(1, typeName);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getTypeDTO(preparedStatement);
  }

  /**
   * Get a type we want to retrieve by his id.
   *
   * @param typeId : the id of the type we want to retrieve
   * @return the type
   */
  @Override
  public TypeDTO getOne(int typeId) {
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(
        "SELECT id_type, type_name, is_default FROM donnamis.types WHERE id_type = ?");
    try {
      preparedStatement.setInt(1, typeId);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getTypeDTO(preparedStatement);
  }

  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  @Override
  public List<TypeDTO> getAllDefaultTypes() {
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(
        "SELECT id_type, type_name, is_default FROM donnamis.types WHERE is_default = true");
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      List<TypeDTO> listTypeDTO = new ArrayList<>();
      // Create a typeDTO for each tuple
      while (resultSet.next()) {
        TypeDTO typeDTO = typeFactory.getTypeDTO();
        typeDTO.setId(resultSet.getInt(1));
        typeDTO.setTypeName(resultSet.getString(2));
        typeDTO.setIsDefault(resultSet.getBoolean(3));
        listTypeDTO.add(typeDTO);
      }
      return listTypeDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Insert a new type in the db.
   *
   * @param typeName the name of the type
   * @return a typeDTO with all the informations of the new type added
   */
  @Override
  public TypeDTO addOne(String typeName) {
    String query = "INSERT INTO donnamis.types (type_name, is_default) VALUES (?, false) "
        + "RETURNING id_type, type_name, is_default";
    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      preparedStatement.setString(1, typeName);
      return getTypeDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private TypeDTO getTypeDTO(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      // Create the typeDTO if we have a result
      TypeDTO typeDTO = typeFactory.getTypeDTO();
      typeDTO.setId(resultSet.getInt(1));
      typeDTO.setTypeName(resultSet.getString(2));
      typeDTO.setIsDefault(resultSet.getBoolean(3));
      return typeDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
