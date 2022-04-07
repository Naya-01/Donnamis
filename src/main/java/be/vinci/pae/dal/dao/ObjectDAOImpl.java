package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class ObjectDAOImpl implements ObjectDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private ObjectFactory objectFactory;
  @Inject
  private TypeDAO typeDAO;

  /**
   * Update the object picture.
   *
   * @param path location of the picture.
   * @param id   of the object.
   * @return Object modified.
   */
  @Override
  public ObjectDTO updateObjectPicture(String path, int id) {
    String query = "UPDATE donnamis.objects SET image=? WHERE id_object=?";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, path);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOne(id);
  }

  /**
   * Get an object we want to retrieve by his id.
   *
   * @param id : the id of the object that we want to retrieve
   * @return the object
   */
  @Override
  public ObjectDTO getOne(int id) {
    String query = "SELECT id_object, description, status, image, id_offeror "
        + "FROM donnamis.objects WHERE id_object = ?";

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      setObject(objectDTO, resultSet);

      resultSet.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return objectDTO;
  }

  /**
   * Get all objects that we want to retrieve by his status.
   *
   * @param status : the status of the objects that we want to retrieve
   * @return the object
   */
  @Override
  public List<ObjectDTO> getAllByStatus(String status) {
    String query = "SELECT id_object, id_type, description, status, image, id_offeror "
        + "FROM donnamis.objects WHERE status = ?";

    List<ObjectDTO> objectDTOList = new ArrayList<>();
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, status);
      setListObject(preparedStatement, objectDTOList);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return objectDTOList;
  }

  /**
   * Get all objects of a member that we want to retrieve by his id.
   *
   * @param idMember : take all object of this member.
   * @return list object of this member.
   */
  @Override
  public List<ObjectDTO> getAllObjectOfMember(int idMember) {
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(
        "SELECT id_object, id_type, description, status, image, id_offeror "
            + "FROM donnamis.objects WHERE id_offeror = ?");

    List<ObjectDTO> objectDTOList = new ArrayList<>();
    try {
      preparedStatement.setInt(1, idMember);
      setListObject(preparedStatement, objectDTOList);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return objectDTOList;
  }

  /**
   * Add object in the database with all information.
   *
   * @param objectDTO : object that we want to add in the database.
   * @return object with his id.
   */
  @Override
  public ObjectDTO addOne(ObjectDTO objectDTO) {
    String query = "insert into donnamis.objects (id_type, description, status, image, id_offeror) "
        + "values (?,?,?,?,?) RETURNING id_object, description, status, image, id_offeror";

    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      preparedStatement.setInt(1, objectDTO.getType().getIdType());
      preparedStatement.setString(2, objectDTO.getDescription());
      preparedStatement.setString(3, objectDTO.getStatus());
      preparedStatement.setString(4, objectDTO.getImage());
      preparedStatement.setInt(5, objectDTO.getIdOfferor());

      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      setObject(objectDTO, resultSet);
      preparedStatement.close();
      resultSet.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return objectDTO;
  }

  /**
   * Update an object.
   *
   * @param objectDTO : object that we want to update.
   * @return object updated
   */
  @Override
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    Deque<String> objectDTODeque = new ArrayDeque<>();
    String query = "UPDATE donnamis.objects SET ";
    TypeDTO typeDTO = null;
    if (objectDTO.getDescription() != null && !objectDTO.getDescription().isEmpty()) {
      query += "description = ?,";
      objectDTODeque.addLast(objectDTO.getDescription());
    }
    if (objectDTO.getStatus() != null && !objectDTO.getStatus().isEmpty()) {
      query += "status = ?,";
      objectDTODeque.addLast(objectDTO.getStatus());
    }
    if (objectDTO.getType() != null && objectDTO.getType().getTypeName() != null
        && !objectDTO.getType().getTypeName().isEmpty()) {
      typeDTO = typeDAO.getOne(objectDTO.getType().getTypeName());
      if (typeDTO == null) {
        typeDTO = typeDAO.addOne(objectDTO.getType().getTypeName());
      }
    }

    query = query.substring(0, query.length() - 1);
    if (query.endsWith("SET")) {
      return null;
    }
    query += " WHERE id_object = ? RETURNING id_object, description, status, image, id_offeror";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      int cnt = 1;
      for (String str : objectDTODeque) {
        preparedStatement.setString(cnt++, str);
      }
      preparedStatement.setInt(cnt, objectDTO.getIdObject());
      ObjectDTO objectDTOUpdated = getObject(preparedStatement);
      preparedStatement.close();
      if (objectDTOUpdated != null) {
        objectDTOUpdated.setType(typeDTO);
      }
      return objectDTOUpdated;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private void setListObject(PreparedStatement preparedStatement, List<ObjectDTO> objectDTOList) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      while (resultSet.next()) {
        ObjectDTO objectDTO = objectFactory.getObjectDTO();
        setObject(objectDTO, resultSet);
        objectDTOList.add(objectDTO);
      }
      resultSet.close();
      preparedStatement.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  private void setObject(ObjectDTO objectDTO, ResultSet resultSet) {
    try {
      objectDTO.setIdObject(resultSet.getInt(1));
      objectDTO.setDescription(resultSet.getString(2));
      objectDTO.setStatus(resultSet.getString(3));
      if (resultSet.getString(4) == null) {
        objectDTO.setImage(resultSet.getString(4));
      } else {
        objectDTO.setImage(Config.getProperty("ImagePath") + resultSet.getString(4));
      }
      objectDTO.setIdOfferor(resultSet.getInt(5));
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private ObjectDTO getObject(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      ObjectDTO objectDTO = objectFactory.getObjectDTO();
      objectDTO.setIdObject(resultSet.getInt(1));
      objectDTO.setDescription(resultSet.getString(2));
      objectDTO.setStatus(resultSet.getString(3));
      if (resultSet.getString(4) == null) {
        objectDTO.setImage(resultSet.getString(4));
      } else {
        objectDTO.setImage(Config.getProperty("ImagePath") + resultSet.getString(4));
      }
      objectDTO.setIdOfferor(resultSet.getInt(5));
      resultSet.close();
      return objectDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

}
