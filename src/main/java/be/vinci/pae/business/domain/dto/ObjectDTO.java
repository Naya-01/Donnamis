package be.vinci.pae.business.domain.dto;

public interface ObjectDTO {

  Integer getIdObject();

  void setIdObject(int idObject);

  TypeDTO getType();

  void setType(TypeDTO idType);

  String getDescription();

  void setDescription(String description);

  String getStatus();

  void setStatus(String status);

  String getImage();

  void setImage(String image);

  Integer getIdOfferor();

  void setIdOfferor(int idOfferor);
}
