package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.TypeDTO;

public class TypeImpl implements TypeDTO {

  private int idType;
  private String typeName;
  private boolean isDefault;

  @Override
  public int getIdType() {
    return idType;
  }

  @Override
  public void setId(int idType) {
    this.idType = idType;
  }

  @Override
  public String getTypeName() {
    return this.typeName;
  }

  @Override
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  @Override
  public boolean isDefault() {
    return this.isDefault;
  }

  @Override
  public void setIsDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  @Override
  public String toString() {
    return "TypeImpl{" +
        "idType=" + idType +
        ", typeName='" + typeName + '\'' +
        ", isDefault=" + isDefault +
        '}';
  }
}
