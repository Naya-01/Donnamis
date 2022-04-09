"use strict";

class Member {

  constructor(username, lastname, firstname, password, phone, address,
      memberId = null) {
    this.memberId = memberId;
    this.username = username;
    this.lastname = lastname;
    this.firstname = firstname;
    this.status = null;
    this.role = null;
    this.phone = phone;
    this.password = password;
    this.reasonRefusal = null;
    this.address = address;
    this.image = null;
  }

}

// default export
export default Member;
