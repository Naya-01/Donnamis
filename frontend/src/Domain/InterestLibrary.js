import {getSessionObject} from "../utils/session";
import Notification from "../Components/Module/Notification";

class InterestLibrary {

  async markNotificationShown(idObject) {
    let response;
    try {
      let options = {
        method: "GET",
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
        method: "GET",
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
    }
    return allInterests;
  }

  async assignOffer(idObject, idMember) {
    let response;
    let toast = Notification.prototype.getNotification("bottom");
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "object": {
            "idObject": idObject
          },
          "idMember": idMember
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/assignOffer", options);
      if (!response.ok) {
        response.text().then((msg) => {
          toast.fire({
            icon: 'error',
            title: msg
          });
        })
      } else {
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

  async addOne(idObject, date) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "object": {
            "idObject": idObject
          },
          "availabilityDate": date
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