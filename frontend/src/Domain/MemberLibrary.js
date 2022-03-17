import {getSessionObject} from "../utils/session";

class MemberLibrary {
  async getUserByHisToken() {
    let response;
    try {
      let options = {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/auth/getuserbytoken", options);
    } catch (err) {
      console.log(err);
    }
    let user;
    if (response.status === 200) {
      user = await response.json();
    }
    return user;
  }
}

export default MemberLibrary;