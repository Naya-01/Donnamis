package be.vinci.pae.dal.dao;

import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class AbstractDAOImpl implements AbstractDAO {

  private static Map<String, String> tableNames;

  static {
    tableNames = new HashMap<>();
    tableNames.put("AddressDTO", "donnamis.addresses");
    tableNames.put("InterestDTO", "donnamis.interests");
    tableNames.put("MemberDTO", "donnamis.members");
    tableNames.put("ObjectDTO", "donnamis.objects");
    tableNames.put("OfferDTO", "donnamis.offers");
    tableNames.put("RatingDTO", "donnamis.ratings");
    tableNames.put("TypeDTO", "donnamis.types");
  }

  @Inject
  private DALBackendService dalBackendService;

  /**
   * Get the object from database.
   *
   * @param conditions of the object(s)
   * @param type       of class
   * @return the object from Database
   */
  @Override
  public <T> PreparedStatement getOne(Map<String, Object> conditions, Class<T> type) {
    if (conditions.isEmpty()) {
      throw new FatalException("Ids manquants");
    }
    String tableName = tableNames.get(type.getSimpleName());
    String condition = "";
    int i = 0;
    for (String s : conditions.keySet()) {
      condition += s + " = ?";
      i++;
      if (i < conditions.size()) {
        condition += " AND ";
      }
    }
    String query = "SELECT * FROM " + tableName + " WHERE " + condition;
    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      i = 0;
      for (Object values : conditions.values()) {
        preparedStatement.setObject((i + 1), values);
        i++;
      }
      return preparedStatement;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get all the objects from database.
   *
   * @param type              of class
   * @param optionalCondition if needed , we can add where condition
   * @return the list of object from Database
   */
  @Override
  public <T> PreparedStatement getAll(Class<T> type, String optionalCondition) {
    return null;
  }

  /**
   * Update the object in database.
   *
   * @param objectDTO         with informations
   * @param type              of class
   * @param optionalCondition if needed , we can add where condition
   * @return the object from Database
   */
  @Override
  public <T> PreparedStatement updateOne(T objectDTO, Class<T> type, String optionalCondition) {
    return null;
  }

  /**
   * Insert the object in database.
   *
   * @param objectDTO         with informations
   * @param type              of class
   * @param optionalCondition
   * @return the object from Database
   */
  @Override
  public <T> PreparedStatement insertOne(T objectDTO, Class<T> type, String optionalCondition) {
    return null;
  }
}
