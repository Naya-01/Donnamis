import {getSessionObject} from "../utils/session";
import Notification from "../Components/Module/Notification";

const Toast = new Notification().getNotification("top-end");

class MemberLibrary {

  /**
   * Get user by his token.
   * @returns {Promise<*>} the member in json.
   */
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

  /**
   * Get user by his id.
   * @returns {Promise<*>} the member in json.
   */
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
   * @returns {Promise<boolean>} true if there is no problem in register
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
        return false;
      }
      return true;
    } catch (err) {
      console.log(err);
    }
  }

  /**
   * Login a quidam as a member or administrator.
   * @param username of the member.
   * @param password of the member.
   * @param remember if he want to be remembered longer.
   * @returns {Promise<*>} refresh and access token in json if he can connect.
   */
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

      const notification = new Notification().getNotification("bottom");
      notification.fire({
        icon: 'success',
        title: "Bienvenue !"
      })
      return await userData.json();
    }
  }

  /**
   * Filter members by status and the search.
   * @param search filter.
   * @param status filter.
   * @returns {Promise<{}>} a list of member.
   */
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

  /**
   * Update a user with his arguments.
   * @param status of the member.
   * @param memberId id of the member.
   * @param reasonRefusal reason of the refusal.
   * @param role of the member.
   * @param version of the update.
   * @returns {Promise<null|any>} the member updated in json.
   */
  async updateStatus(status, memberId, reasonRefusal, role, version) {
    let response;
    try {
      let options = {
        method: "PUT",
        body: JSON.stringify({
          "status": status,
          "reasonRefusal": reasonRefusal,
          "memberId": memberId,
          "role": role,
          "version": version,
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

  /**
   * Update the member by his data.
   * @param member datas to update.
   * @returns {Promise<null|any>} the member updated in json
   */
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
      if (response.status === 200) {
        return await response.json();
      }
      response.text().then((msg) => {
        Toast.fire({
          icon: 'error',
          title: msg
        });
      })
      return null;
    } catch (err) {
      console.log(err);
      return null;
    }
  }

  /**
   * Set a profil picture to the member who call the function.
   * @param formData is the picture data.
   * @param version of the update.
   * @returns {Promise<*>} the member updated in json.
   */
  async setImage(formData, version) {
    let response = null;
    try {
      let options = {
        method: 'POST',
        body: formData,
        headers: {
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch('api/member/setPicture?version=' + version,
          options)
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