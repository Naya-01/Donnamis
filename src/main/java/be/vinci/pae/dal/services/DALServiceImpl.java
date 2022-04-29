package be.vinci.pae.dal.services;

import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.utils.Config;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;

public class DALServiceImpl implements DALBackendService, DALService {

  private ThreadLocal<Connection> connection;
  private BasicDataSource dataSource;

  /**
   * Establish the connection of the db.
   */
  public DALServiceImpl() {
    connection = new ThreadLocal<>();

    dataSource = new BasicDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl(Config.getProperty("dbUrl"));
    dataSource.setUsername(Config.getProperty("dbUser"));
    dataSource.setPassword(Config.getProperty("dbPassword"));

  }

  /**
   * Get a prepared statement for a query.
   *
   * @param query the query you need to be executed
   * @return a prepared statement of your query
   */
  @Override
  public PreparedStatement getPreparedStatement(String query) {
    PreparedStatement ps;
    try {
      Connection conn = connection.get();
      ps = conn.prepareStatement(query);
      return ps;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Start a transaction. Get a connection from the BasicDataSource & set it the ThreadLocal.
   */
  @Override
  public void startTransaction() {
    try {
      if (connection.get() != null) {
        throw new FatalException("Connection deja ouverte");
      }
      Connection conn = dataSource.getConnection();
      conn.setAutoCommit(false);
      connection.set(conn);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Commit the transaction, close the connection & remove the connection in the ThreadLocal.
   */
  @Override
  public void commitTransaction() {
    Connection conn = connection.get();
    try {
      conn.commit();
      conn.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    } finally {
      connection.remove();
    }
  }

  /**
   * RollBack the transaction, close the connection & remove the connection in the ThreadLocal.
   */
  @Override
  public void rollBackTransaction() {
    Connection conn = connection.get();
    try {
      conn.rollback();
      conn.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    } finally {
      connection.remove();
    }
  }
}
