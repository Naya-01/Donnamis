package be.vinci.pae.business.domain.dto;

public interface ObjectDTO {

  int getIdObject();

  void setIdObject(int idObject);

  int getIdType();

  void setIdType(int idType);

  String getDescription();

  void setDescription(String description);

  String getStatus();

  void setStatus(String status);

  String getImage();

  void setImage(String image);

  int getIdOfferor();

  void setIdOfferor(int idOfferor);
}
