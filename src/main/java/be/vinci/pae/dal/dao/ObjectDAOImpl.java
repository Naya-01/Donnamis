package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
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
    String query = "UPDATE donnamis.objects SET image=? , version = version + 1 WHERE id_object=?";
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
    String query = "SELECT id_object, description, status, image, id_offeror, version,id_type "
        + "FROM donnamis.objects WHERE id_object = ?";
    ObjectDTO objectDTO;
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeQuery();
      objectDTO = getObjectDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return objectDTO;
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
        "SELECT id_object, id_type, description, status, image, id_offeror, version "
            + "FROM donnamis.objects WHERE id_offeror = ?");

    List<ObjectDTO> objectDTOList;
    try {
      preparedStatement.setInt(1, idMember);
      objectDTOList = getObjectListDTO(preparedStatement);
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
    String query = "insert into donnamis.objects "
        + "(id_type, description, status, image, id_offeror, version) "
        + "values (?,?,'available',?,?, 1) "
        + "RETURNING id_object, description, status, image, id_offeror, version, id_type";

    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      preparedStatement.setInt(1, objectDTO.getType().getIdType());
      preparedStatement.setString(2, objectDTO.getDescription());
      preparedStatement.setString(3, objectDTO.getImage());
      preparedStatement.setInt(4, objectDTO.getIdOfferor());
      objectDTO = getObjectDTO(preparedStatement);
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
    if (objectDTO.getDescription() != null && !objectDTO.getDescription().isEmpty()) {
      query += "description = ?,";
      objectDTODeque.addLast(objectDTO.getDescription());
    }
    if (objectDTO.getStatus() != null && !objectDTO.getStatus().isEmpty()) {
      query += "status = ?,";
      objectDTODeque.addLast(objectDTO.getStatus());
    }

    query = query.substring(0, query.length() - 1);
    if (query.endsWith("SET")) {
      return null;
    }
    query += " , version = version + 1 "
        + "WHERE id_object = ? RETURNING id_object, "
        + "description, status, image, id_offeror, id_type, version";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      int cnt = 1;
      for (String str : objectDTODeque) {
        preparedStatement.setString(cnt++, str);
      }
      preparedStatement.setInt(cnt, objectDTO.getIdObject());
      return getObjectDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private ObjectDTO getObjectDTO(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      ObjectDTO objectDTO = objectDTOFromResultSet(resultSet);
      resultSet.close();
      preparedStatement.close();
      return objectDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private ObjectDTO objectDTOFromResultSet(ResultSet resultSet) {
    try {
      ObjectDTO objectDTO = objectFactory.getObjectDTO();
      objectDTO.setIdObject(resultSet.getInt("id_object"));
      objectDTO.setIdType(resultSet.getInt("id_type"));
      objectDTO.setDescription(resultSet.getString("description"));
      objectDTO.setStatus(resultSet.getString("status"));
      String img = resultSet.getString("image");
      if (img != null) {
        objectDTO.setImage(Config.getProperty("ImagePath") + img);
      }
      objectDTO.setIdOfferor(resultSet.getInt("id_offeror"));
      objectDTO.setVersion(resultSet.getInt("version"));
      objectDTO.setType(typeDAO.getOne(objectDTO.getIdType()));
      return objectDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private List<ObjectDTO> getObjectListDTO(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      List<ObjectDTO> objectDTOList = new ArrayList<>();
      while (resultSet.next()) {
        objectDTOList.add(objectDTOFromResultSet(resultSet));
      }
      resultSet.close();
      preparedStatement.close();
      if (objectDTOList.isEmpty()) {
        return null;
      } else {
        return objectDTOList;
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

}
