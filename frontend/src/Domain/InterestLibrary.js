import {getSessionObject} from "../utils/session";
import Notification from "../Components/Module/Notification";
import Swal from "sweetalert2";

class InterestLibrary {

  async getInterestCount() {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest//notificationCount",
          options);
    } catch (err) {
      console.log(err);
    }
    let notificationCount;
    if (response.status === 200) {
      notificationCount = await response.json();
    }
    return notificationCount;
  }

  async markNotificationShown(idObject) {
    let response;
    try {
      let options = {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/notificationShown/" + idObject,
          options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    }
    return allInterests;
  }

  async markAllNotificationShown() {
    let response;
    try {
      let options = {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/allNotificationShown",
          options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    }
    return allInterests;
  }

  async getAllNotifications() {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/getAllNotifications",
          options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    } else {
      allInterests = [];
    }
    return allInterests;
  }

  async assignOffer(idObject, idMember, version, versionOffer, versionObject) {
    let response;
    let toast = Notification.prototype.getNotification("bottom");
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "idMember": idMember,
          "version": version,
          "idObject": idObject,
          "object": {
            "version": versionObject
          },
          "offer": {
            "version": versionOffer
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/assignOffer", options);
      if (!response.ok) {
        response.text().then((msg) => {
          Swal.close();
          toast.fire({
            icon: 'error',
            title: msg
          });
        })
      } else {
        Swal.close();
        toast.fire({
          icon: 'success',
          title: "le membre a été assigné"
        })
      }
    } catch (err) {
      console.log(err);
    }
    let newInterest;
    if (response.status === 200) {
      newInterest = await response.json();
    }
    return newInterest;
  }

  async getInterestedCount(idObject) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/count/" + idObject, options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    }
    return allInterests;
  }

  async getAllInterests(idObject) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/getAllInterests/" + idObject,
          options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    }
    return allInterests;
  }

  async getOneInterest(idObject) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest?idObject=" + idObject, options);
    } catch (err) {
      console.log(err);
    }
    let current_interest;
    if (response.status === 200) {
      current_interest = await response.json();
    }
    return current_interest;
  }

  async addOne(idObject, date, be_called, versionObject, versionOffer) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "idObject": idObject,
          "availabilityDate": date,
          "object": {
            "version": versionObject
          },
          "offer": {
            "version": versionOffer
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest", options);
    } catch (err) {
      console.log(err);
    }
    let newInterest;
    if (response.status === 200) {
      newInterest = await response.json();
    }
    return newInterest;
  }
}

export default InterestLibrary;