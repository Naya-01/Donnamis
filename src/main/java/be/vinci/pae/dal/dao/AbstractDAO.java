package be.vinci.pae.dal.dao;

import java.sql.PreparedStatement;
import java.util.Map;

public interface AbstractDAO {

  /**
   * Get the object from database.
   *
   * @param conditions to get the objects
   * @param type       of class
   * @return the object from Database
   */
  <T> PreparedStatement getOne(Map<String, Object> conditions, Class<T> type);

  /**
   * Get all the objects from database.
   *
   * @param type              of class
   * @param optionalCondition if needed , we can add where condition
   * @param <T>               type of return
   * @return the list of object from Database
   */
  <T> PreparedStatement getAll(Class<T> type, String optionalCondition);

  /**
   * Update the object in database.
   *
   * @param objectDTO         with informations
   * @param type              of class
   * @param optionalCondition if needed , we can add where condition
   * @param <T>               type of return
   * @return the object from Database
   */
  <T> PreparedStatement updateOne(T objectDTO, Class<T> type, String optionalCondition);

  /**
   * Insert the object in database.
   *
   * @param objectDTO         with informations
   * @param type              of class
   * @param optionalCondition
   * @return the object from Database
   */
  <T> PreparedStatement insertOne(T objectDTO, Class<T> type, String optionalCondition);
}
