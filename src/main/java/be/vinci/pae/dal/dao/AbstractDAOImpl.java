package be.vinci.pae.dal.dao;

import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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

  private void setPreparedStatement(Map<String, Object> toUpdateOrToInsert, List<Object> values,
      PreparedStatement preparedStatement) {
    try {
      int i = 0;
      if (toUpdateOrToInsert != null) {
        for (Object object : toUpdateOrToInsert.values()) {
          preparedStatement.setObject(i + 1, object);
          i++;
        }
      }

      for (Object object : values) {
        int indice = object.toString().indexOf('.');
        if (indice != -1) {
          continue;
        }
        preparedStatement.setObject(i + 1, object);
        i++;
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private <T> String getTablesName(List<Class<T>> types) {
    String tables = "";
    int i = 0;
    for (Class<T> type : types) {
      tables += tableNames.get(type.getSimpleName());
      i++;
      if (i < types.size()) {
        tables += ",";
      }
    }
    return tables;
  }

  private <T> String getQuery(String method, String tables, String condition,
      Map<String, Object> toUpdateOrToInsert) {
    String query = method;
    if (tables.isEmpty() || tables.isBlank()) {
      throw new FatalException("Aucunes tables sélectionnées");
    }

    if (method.equals("SELECT")) {
      query += " * FROM " + tables + " WHERE " + condition;
    } else if (method.equals("UPDATE")) {
      if (toUpdateOrToInsert == null || toUpdateOrToInsert.isEmpty()) {
        throw new FatalException("Veuillez spécifier des champs à modifier");
      }

      String settersValues = "";
      int i = 0;
      for (String val : toUpdateOrToInsert.keySet()) {
        settersValues += val + " = ?";
        i++;
        if (i < toUpdateOrToInsert.size()) {
          settersValues += ",";
        }
      }
      query += " " + tables + " SET " + settersValues + " WHERE " + condition;
    }
    return query;
  }

  /**
   * Get the object from database.
   *
   * @param values    to complete the condition
   * @param condition of the object
   * @param types     of tables
   * @return the object from Database
   */
  @Override
  public <T> PreparedStatement getOne(String condition, List<Object> values,
      List<Class<T>> types) {
    if (condition.isEmpty() || condition.isBlank()) {
      throw new FatalException("données de filtre manquantes");
    }

    String query = getQuery("SELECT", getTablesName(types), condition, null);
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
    setPreparedStatement(null, values, preparedStatement);
    return preparedStatement;
  }

  /**
   * Get all the objects from database.
   *
   * @param values    to complete the condition
   * @param condition of the object(s)
   * @param types     of tables
   * @return the list of object from Database
   */
  @Override
  public <T> PreparedStatement getAll(String condition, List<Object> values,
      List<Class<T>> types) {

    String query = getQuery("SELECT", getTablesName(types), condition, null);
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
    setPreparedStatement(null, values, preparedStatement);
    return preparedStatement;

  }

  /**
   * Update the object in database.
   *
   * @param toUpdate        values to update
   * @param condition       of the object
   * @param conditionValues to complete the condition
   * @param types           of tables
   * @return the list of object from Database
   */
  @Override
  public <T> PreparedStatement updateOne(Map<String, Object> toUpdate, String condition,
      List<Object> conditionValues, List<Class<T>> types) {

    String query = getQuery("UPDATE", getTablesName(types), condition, toUpdate);
    System.out.println(query);
    PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
    setPreparedStatement(toUpdate, conditionValues, preparedStatement);
    System.out.println(preparedStatement);
    return preparedStatement;
  }


  /**
   * Insert the object in database.
   *
   * @param objectDTO         with informations
   * @param type              of class
   * @param optionalCondition to complete the condition
   * @return the object from Database
   */
  @Override
  public <T> PreparedStatement insertOne(T objectDTO, Class<T> type, String optionalCondition) {
    return null;
  }
}
