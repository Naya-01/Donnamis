"use strict";

class Address {

  constructor(unitNumber, buildingNumber, street, postcode, commune, version) {
    this.idMember = null;
    this.unitNumber = unitNumber;
    this.buildingNumber = buildingNumber;
    this.street = street;
    this.postcode = postcode;
    this.commune = commune;
    this.version = version;
  }

}

export default Address;
