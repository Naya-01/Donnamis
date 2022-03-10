package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;

public class ObjectImpl implements ObjectDTO {

  private Integer idObject;
  private TypeDTO type;
  private String description;
  private String status;
  private String image;
  private Integer idOfferor;

  @Override
  public Integer getIdObject() {
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
  public Integer getIdOfferor() {
    return idOfferor;
  }

  @Override
  public void setIdOfferor(int idOfferor) {
    this.idOfferor = idOfferor;
  }
}
