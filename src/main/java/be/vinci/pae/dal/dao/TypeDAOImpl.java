package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeDAOImpl implements TypeDAO {

  @Inject
  private TypeFactory typeFactory;
  @Inject
  private AbstractDAO abstractDAO;

  /**
   * Get a type we want to retrieve by his type name.
   *
   * @param typeName : the typeName of the type we want to retrieve
   * @return the type
   */
  @Override
  public TypeDTO getOne(String typeName) {
    String condition = "type_name = ?";
    List<Object> values = new ArrayList<>();
    values.add(typeName);
    ArrayList<Class> types = new ArrayList<>();
    types.add(TypeDTO.class);
    try (PreparedStatement preparedStatement = abstractDAO.getOne(condition, values, types)) {
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
    String condition = "id_type = ?";
    List<Object> values = new ArrayList<>();
    values.add(typeId);
    ArrayList<Class> types = new ArrayList<>();
    types.add(TypeDTO.class);
    try (PreparedStatement preparedStatement = abstractDAO.getOne(condition, values, types)) {
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
    List<Class> types = new ArrayList<>();
    types.add(TypeDTO.class);
    List<Object> values = new ArrayList<>();
    String condition = "is_default = true";

    try (PreparedStatement preparedStatement = abstractDAO.getAll(condition, values, types)) {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      List<TypeDTO> listTypeDTO = new ArrayList<>();
      while (resultSet.next()) {
        TypeDTO typeDTO = typeFactory.getTypeDTO();
        typeDTO.setId(resultSet.getInt(1));
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
    Map<String, Object> setters = new HashMap<>();
    setters.put("type_name", typeName);
    setters.put("is_default", false);
    List<Class> types = new ArrayList<>();
    types.add(TypeDTO.class);

    try (PreparedStatement preparedStatement = abstractDAO.insertOne(setters, types)) {
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
      TypeDTO typeDTO = typeFactory.getTypeDTO();
      typeDTO.setId(resultSet.getInt(1));
      typeDTO.setTypeName(resultSet.getString(2));
      typeDTO.setIsDefault(resultSet.getBoolean(3));

      resultSet.close();
      return typeDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
