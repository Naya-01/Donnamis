package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectImpl implements ObjectDTO {
  @JsonView(Views.Public.class)
  private int idObject;
  @JsonView(Views.Public.class)
  private TypeDTO type;
  @JsonView(Views.Public.class)
  private String description;
  @JsonView(Views.Public.class)
  private String status;
  @JsonView(Views.Public.class)
  private String image;
  @JsonView(Views.Public.class)
  private int idOfferor;
  @JsonView(Views.Public.class)

  @Override
  public int getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

  @Override
  public TypeDTO getType() {
    return type;
  }

  @Override
  public void setType(TypeDTO type) {
    this.type = type;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String getImage() {
    return image;
  }

  @Override
  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public int getIdOfferor() {
    return idOfferor;
  }

  @Override
  public void setIdOfferor(int idOfferor) {
    this.idOfferor = idOfferor;
  }

}