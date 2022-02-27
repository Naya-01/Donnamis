package be.vinci.pae.dal.services;

import be.vinci.pae.utils.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DALService {

  private Connection connection;

  public DALService() {
    try {
      connection = DriverManager.getConnection(Config.getProperty("dbUrl"),
          Config.getProperty("dbUser"),
          Config.getProperty("dbPassword"));

    } catch (SQLException e) {
      System.out.println("Impossible de joindre le server !");
      System.exit(1);
    }

  }

  public PreparedStatement getPreparedStatement(String query) {
    try {
      return connection.prepareStatement(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
