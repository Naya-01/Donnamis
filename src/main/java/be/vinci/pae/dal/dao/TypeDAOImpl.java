package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypeDAOImpl implements TypeDAO {

  @Inject
  private TypeFactory typeFactory;
  @Inject
  private DALBackendService dalBackendService;

  /**
   * Get a type we want to retrieve by his type name.
   *
   * @param typeName : the typeName of the type we want to retrieve
   * @return the type
   */
  @Override
  public TypeDTO getOne(String typeName) {
    String query = "SELECT id_type, type_name, is_default FROM donnamis.types WHERE type_name = ?";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, typeName);
      return getTypeDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a type we want to retrieve by his id.
   *
   * @param typeId : the id of the type we want to retrieve
   * @return the type
   */
  @Override
  public TypeDTO getOne(int typeId) {
    String query = "SELECT id_type, type_name, is_default FROM donnamis.types WHERE id_type = ?";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, typeId);
      return getTypeDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  @Override
  public List<TypeDTO> getAllDefaultTypes() {
    String query = "SELECT id_type, type_name, is_default FROM donnamis.types "
        + "WHERE is_default = true";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      List<TypeDTO> listTypeDTO = new ArrayList<>();
      while (resultSet.next()) {
        TypeDTO typeDTO = typeFactory.getTypeDTO();
        typeDTO.setIdType(resultSet.getInt(1));
        typeDTO.setTypeName(resultSet.getString(2));
        typeDTO.setIsDefault(resultSet.getBoolean(3));
        listTypeDTO.add(typeDTO);
      }
      resultSet.close();
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
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, typeName);
      return getTypeDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Build a TypeDTO on base of a preparedStatement.
   * @param preparedStatement the ps that will be executed to have information of the type
   * @return the TypeDTO
   */
  private TypeDTO getTypeDTO(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      TypeDTO typeDTO = typeFactory.getTypeDTO();
      typeDTO.setIdType(resultSet.getInt(1));
      typeDTO.setTypeName(resultSet.getString(2));
      typeDTO.setIsDefault(resultSet.getBoolean(3));

      resultSet.close();
      return typeDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
