package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.ObjectImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = ObjectImpl.class)
public interface ObjectDTO {

  int getIdObject();

  void setIdObject(int idObject);

  TypeDTO getType();

  void setType(TypeDTO type);

  String getDescription();

  void setDescription(String description);

  String getStatus();

  void setStatus(String status);

  String getImage();

  void setImage(String image);

  int getIdOfferor();

  void setIdOfferor(int idOfferor);
}
