package be.vinci.pae.dal.services;

import java.sql.PreparedStatement;

public interface DALBackendService {

  /**
   * Get a prepared statement for a query.
   *
   * @param query the query you need to be executed
   * @return a prepared statement of your query
   */
  PreparedStatement getPreparedStatement(String query);
}
