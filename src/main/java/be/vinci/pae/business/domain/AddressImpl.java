package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.AddressDTO;

public class AddressImpl implements AddressDTO {

  private int idMember;
  private String unitNumber;
  private String buildingNumber;
  private String street;
  private String postcode;
  private String commune;
  private String country;


  @Override
  public int getIdMember() {
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
  public String getCountry() {
    return this.country;
  }

  @Override
  public void setCountry(String country) {
    this.country = country;
  }
  
}
