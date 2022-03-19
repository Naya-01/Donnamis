"use strict";

class Address {
  // constructor for register
  constructor(unitNumber, buildingNumber, street, postcode, commune, country) {
    this.idMember = null;
    this.unitNumber = unitNumber;
    this.buildingNumber = buildingNumber;
    this.street = street;
    this.postcode = postcode;
    this.commune = commune;
    this.country = country;

  }

}

// default export
export default Address;
