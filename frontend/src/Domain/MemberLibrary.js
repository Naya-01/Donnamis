import {getSessionObject} from "../utils/session";
import Notification from "../Components/Module/Notification";

const Toast = new Notification().getNotification();

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
      response = await fetch("/api/member/getMemberByToken/", options);
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
      response = await fetch("api/member/id/" + id, options);
    } catch (err) {
      console.log(err);
    }
    let user;
    if (response.status === 200) {
      user = await response.json();
    }
    return user;
  }

  async registerMember(member) {

    let userData;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify(member),
        headers: {
          "Content-Type": "application/json",
        },
      };
      userData = await fetch("/api/auth/register/", options);
      if (!userData.ok) {
        userData.text().then((msg) => {
          Toast.fire({
            icon: 'error',
            title: msg
          });
        })
      }
    } catch (err) {
      console.log(err);
    }
  }
}

export default MemberLibrary;