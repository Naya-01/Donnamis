package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.AddressImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = AddressImpl.class)
public interface AddressDTO {

  Integer getIdMember();

  void setIdMember(int idMember);

  String getUnitNumber();

  void setUnitNumber(String unitNumber);

  String getBuildingNumber();

  void setBuildingNumber(String buildingNumber);

  String getStreet();

  void setStreet(String street);

  String getPostcode();

  void setPostcode(String postcode);

  String getCommune();

  void setCommune(String commune);

  Integer getVersion();

  void setVersion(Integer version);

}