package be.vinci.pae.dal.dao;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public interface AbstractDAO {

  /**
   * Get the object from database.
   *
   * @param values    to complete the condition
   * @param condition of the object
   * @param types     of tables
   * @return the object from Database
   */
  <T> PreparedStatement getOne(String condition, List<Object> values,
      List<Class<T>> types);

  /**
   * Get all the objects from database.
   *
   * @param values    to complete the condition
   * @param condition of the object(s)
   * @param types     of tables
   * @return the list of object from Database
   */
  <T> PreparedStatement getAll(String condition, List<Object> values,
      List<Class<T>> types);

  /**
   * Update the object in database.
   *
   * @param toUpdate        values to update
   * @param conditionValues to complete the condition
   * @param condition       of the object
   * @param types           of tables
   * @return the list of object from Database
   */
  <T> PreparedStatement updateOne(Map<String, Object> toUpdate, String condition,
      List<Object> conditionValues,
      List<Class<T>> types);

  /**
   * Insert the object in database.
   *
   * @param objectDTO         with informations
   * @param type              of class
   * @param optionalCondition to complete the condition
   * @return the object from Database
   */
  <T> PreparedStatement insertOne(T objectDTO, Class<T> type, String optionalCondition);
}
