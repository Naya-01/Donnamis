package be.vinci.pae.dal.services;

public interface DALService {

  void startTransaction();

  void commitTransaction();

  void rollBackTransaction();
}
