"use strict";

class Address {
  // constructor for register
  constructor(unitNumber, buildingNumber, street, postcode, commune) {
    this.idMember = null;
    this.unitNumber = unitNumber;
    this.buildingNumber = buildingNumber;
    this.street = street;
    this.postcode = postcode;
    this.commune = commune;
  }

}

// default export
export default Address;
