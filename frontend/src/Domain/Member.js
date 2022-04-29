"use strict";

class Member {

  constructor(username, lastname, firstname, password, phone, address, version, role, reasonRefusal, status,
      memberId = null) {
    this.memberId = memberId;
    this.username = username;
    this.lastname = lastname;
    this.firstname = firstname;
    this.status = status;
    this.role = role;
    this.phone = phone;
    this.password = password;
    this.reasonRefusal = reasonRefusal;
    this.address = address;
    this.image = null;
    this.version = version;
    this.role = role;
  }

}

export default Member;
