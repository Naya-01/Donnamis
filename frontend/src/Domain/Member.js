"use strict";

class Member {
  // constructor for register
  constructor(username, lastname, firstname, password, phone, address) {
    this.memberId = null;
    this.username = username;
    this.lastname = lastname;
    this.firstname = firstname;
    this.status = null;
    this.role = null;
    this.phone = phone;
    this.password = password;
    this.reasonRefusal = null;
    this.address = address;
  }
}

// default export
export default Member;
