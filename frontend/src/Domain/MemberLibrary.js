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

  async getUserByHisId(id) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/auth/id/" + id, options);
    } catch (err) {
      console.log(err);
    }
    let user;
    if (response.status === 200) {
      user = await response.json();
    }
    return user;
  }

  async getMemberBySearchAndStatus(search, status) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch(
          "api/member/search?search=" + search + "&status=" + status, options);
    } catch (err) {
      console.log(err);
      return {};
    }
    let user = {};
    if (response.status === 200) {
      user = await response.json();
    }
    return user;
  }

  async updateStatus(status, memberId, reasonRefusal, role) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "status": status,
          "reasonRefusal": reasonRefusal,
          "memberId": memberId,
          "role": role,
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/member/update", options);
    } catch (err) {
      console.log(err);
      return null;
    }
    if (response.status === 200) {
      return await response.json();
    }
    return null;
  }
}

export default MemberLibrary;