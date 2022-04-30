package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.TypeImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = TypeImpl.class)
public interface TypeDTO {

  Integer getIdType();

  void setIdType(int idType);

  String getTypeName();

  void setTypeName(String typeName);

  boolean isDefault();

  void setIsDefault(boolean isDefault);
}
