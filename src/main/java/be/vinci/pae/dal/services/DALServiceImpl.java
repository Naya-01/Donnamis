package be.vinci.pae.dal.services;

import be.vinci.pae.utils.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DALServiceImpl implements DALBackendService, DALService {

  private Connection connection;

  /**
   * Establish the connection of the db.
   */
  public DALServiceImpl() {
    try {
      connection = DriverManager.getConnection(Config.getProperty("dbUrl"),
          Config.getProperty("dbUser"),
          Config.getProperty("dbPassword"));

    } catch (SQLException e) {
      System.out.println("Impossible de joindre le server !");
      System.exit(1);
    }

  }

  /**
   * Get a prepared statement for a query.
   *
   * @param query the query you need to be executed
   * @return a prepared statement of your query
   */
  @Override
  public PreparedStatement getPreparedStatement(String query) {
    try {
      return connection.prepareStatement(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void startTransaction() {

  }

  @Override
  public void commitTransaction() {

  }

  @Override
  public void rollBackTransaction() {

  }
}
