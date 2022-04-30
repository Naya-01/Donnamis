package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressImpl implements AddressDTO {

  @JsonView(Views.Public.class)
  private Integer idMember;
  @JsonView(Views.Public.class)
  private String unitNumber;
  @JsonView(Views.Public.class)
  private String buildingNumber;
  @JsonView(Views.Public.class)
  private String street;
  @JsonView(Views.Public.class)
  private String postcode;
  @JsonView(Views.Public.class)
  private String commune;
  @JsonView(Views.Public.class)
  private Integer version;


  @Override
  public Integer getIdMember() {
    return this.idMember;
  }

  @Override
  public void setIdMember(int idMember) {
    this.idMember = idMember;
  }

  @Override
  public String getUnitNumber() {
    return this.unitNumber;
  }

  @Override
  public void setUnitNumber(String unitNumber) {
    this.unitNumber = unitNumber;
  }

  @Override
  public String getBuildingNumber() {
    return this.buildingNumber;
  }

  @Override
  public void setBuildingNumber(String buildingNumber) {
    this.buildingNumber = buildingNumber;
  }

  @Override
  public String getStreet() {
    return this.street;
  }

  @Override
  public void setStreet(String street) {
    this.street = street;
  }

  @Override
  public String getPostcode() {
    return this.postcode;
  }

  @Override
  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  @Override
  public String getCommune() {
    return this.commune;
  }

  @Override
  public void setCommune(String commune) {
    this.commune = commune;
  }

  @Override
  public Integer getVersion() {
    return this.version;
  }

  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }
}
