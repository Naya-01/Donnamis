import {getSessionObject} from "../utils/session";
import Notification from "../Components/Module/Notification";

const Toast = new Notification().getNotification("bottom");

class MemberLibrary {
  async getUserByHisToken() {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/member/getMemberByToken", options);
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

  /**
   * Register a quidam
   *
   * @param member member having al data of the member to register
   * @returns {Promise<void>} nothing
   */
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

  async login(username, password, remember) {
    let userData;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "username": username,
          "password": password,
          "rememberMe": remember,
        }),
        headers: {
          "Content-Type": "application/json",
        },
      };
      userData = await fetch("/api/auth/login/", options);
      if (!userData.ok) {
        userData.text().then((msg) => {
          Toast.fire({
            icon: 'error',
            title: msg
          })
        })
      }
    } catch (err) {
      console.log(err);
    }
    if (userData.status === 200) {
      Toast.fire({
        icon: 'success',
        title: "Bienvenue !"
      })
      return await userData.json();
    }
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
        method: "PUT",
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

  async updateMember(member) {
    let response;
    try {
      let options = {
        method: "PUT",
        body: JSON.stringify(member),
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

  async setImage(formData) {
    let response = null;
    try {
      let options = {
        method: 'POST',
        body: formData,
        headers: {
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch('api/member/setPicture/', options)
    } catch (err) {
      console.log(err);
    }
    let newMember;
    if (response.status === 200) {
      newMember = await response.json();
    }
    return newMember;
  }
}

export default MemberLibrary;