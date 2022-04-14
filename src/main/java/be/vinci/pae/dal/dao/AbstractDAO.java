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
  PreparedStatement getOne(String condition, List<Object> values,
      List<Class> types);

  /**
   * Get all the objects from database.
   *
   * @param values    to complete the condition
   * @param condition of the object(s)
   * @param types     of tables
   * @return the list of object from Database
   */
  PreparedStatement getAll(String condition, List<Object> values,
      List<Class> types);

  /**
   * Update the object in database.
   *
   * @param toUpdate        values to update
   * @param conditionValues to complete the condition
   * @param condition       of the object
   * @param types           of tables
   * @return the list of object from Database
   */
  PreparedStatement updateOne(Map<String, Object> toUpdate, String condition,
      List<Object> conditionValues,
      List<Class> types);

  /**
   * Insert the object in database.
   *
   * @param toInsert values to insert
   * @param types    of tables
   * @return the object from Database
   */
  PreparedStatement insertOne(Map<String, Object> toInsert, List<Class> types);
}
