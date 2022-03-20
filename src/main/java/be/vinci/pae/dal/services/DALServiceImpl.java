package be.vinci.pae.dal.services;

import be.vinci.pae.utils.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DALServiceImpl implements DALBackendService, DALService {

  private ThreadLocal<Connection> connection;

  /**
   * Establish the connection of the db.
   */
  public DALServiceImpl() {
    connection = new ThreadLocal<>();

  }

  /**
   * Get a prepared statement for a query.
   *
   * @param query the query you need to be executed
   * @return a prepared statement of your query
   */
  @Override
  public PreparedStatement getPreparedStatement(String query) {
    Connection conn;
    PreparedStatement ps = null;
    try {
      conn = connection.get();
      ps = conn.prepareStatement(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return ps;
  }

  @Override
  public void startTransaction() {
    try {
      Connection conn;
      conn = DriverManager.getConnection(Config.getProperty("dbUrl"),
          Config.getProperty("dbUser"),
          Config.getProperty("dbPassword"));
      conn.setAutoCommit(false);
      connection.set(conn);
    } catch (SQLException e) {
      System.out.println("Impossible de joindre le server !");
      System.exit(1);
    }
  }

  @Override
  public void commitTransaction() {

  }

  @Override
  public void rollBackTransaction() {

  }
}
