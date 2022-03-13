package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.ObjectImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ObjectImpl.class)
public interface ObjectDTO {

  Integer getIdObject();

  void setIdObject(int idObject);

  Integer getIdType();

  void setIdType(int idType);

  String getDescription();

  void setDescription(String description);

  String getStatus();

  void setStatus(String status);

  byte[] getImage();

  void setImage(byte[] image);

  Integer getIdOfferor();

  void setIdOfferor(int idOfferor);
}
