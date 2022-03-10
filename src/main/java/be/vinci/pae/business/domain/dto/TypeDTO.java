package be.vinci.pae.business.domain.dto;

public interface TypeDTO {

  int getIdType();

  void setIdType(int idType);

  String getTypeName();

  void setTypeName(String typeName);

  boolean isDefault();

  void setDefault(boolean isDefault);
}
