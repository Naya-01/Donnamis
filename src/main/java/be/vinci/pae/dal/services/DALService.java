package be.vinci.pae.dal.services;

public interface DALService {

  /**
   * Start a transaction. Get a connection from the BasicDataSource & set it the ThreadLocal.
   */
  void startTransaction();

  /**
   * Commit the transaction, close the connection & remove the connection in the ThreadLocal.
   */
  void commitTransaction();

  /**
   * RollBack the transaction, close the connection & remove the connection in the ThreadLocal.
   */
  void rollBackTransaction();
}
