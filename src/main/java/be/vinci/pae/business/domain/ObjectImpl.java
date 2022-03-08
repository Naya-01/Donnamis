package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.ObjectDTO;

public class ObjectImpl implements ObjectDTO {

  private int idObject;
  private int idType;
  private String description;
  private String status;
  private String image;
  private int idOfferor;

  @Override
  public int getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

  @Override
  public int getIdType() {
    return idType;
  }

  @Override
  public void setIdType(int idType) {
    this.idType = idType;
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
