package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TypeImpl implements TypeDTO {

  @JsonView(Views.Public.class)
  private Integer idType;
  @JsonView(Views.Public.class)
  private String typeName;
  @JsonView(Views.Public.class)
  private Boolean isDefault;

  @Override
  public Integer getIdType() {
    return idType;
  }

  @Override
  public void setIdType(int idType) {
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

}
