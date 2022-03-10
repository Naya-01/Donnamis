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
  public void setIdType(int idType) {
    this.idType = idType;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  @Override
  public boolean isDefault() {
    return isDefault;
  }

  @Override
  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }
}
