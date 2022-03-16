package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import java.util.Arrays;

public class ObjectImpl implements ObjectDTO {

  private int idObject;
  private TypeDTO type;
  private String description;
  private String status;
  private byte[] image;
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
  public byte[] getImage() {
    return image;
  }

  @Override
  public void setImage(byte[] image) {
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

  @Override
  public String toString() {
    return "ObjectImpl{" +
        "idObject=" + idObject +
        ", type=" + type +
        ", description='" + description + '\'' +
        ", status='" + status + '\'' +
        ", image=" + Arrays.toString(image) +
        ", idOfferor=" + idOfferor +
        '}';
  }
}