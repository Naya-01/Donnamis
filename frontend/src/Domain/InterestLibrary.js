import {getSessionObject} from "../utils/session";
import NotificationSA from "../Components/Module/NotificationSA";
import Swal from "sweetalert2";

class InterestLibrary {

  /**
   * Get count of notifications.
   *
   * @returns notification count
   */
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

  /**
   * Mark a notification as shown. /!\ There is no version update because of the non-sensibility of
   * the send_notification field /!\
   *
   * @param idObject of the interest.
   * @param idMember to update notification.
   * @returns interest updated.
   */
  async markNotificationShown(idObject, idMember) {
    let response;
    try {
      let options = {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch(
          "api/interest/notificationShown/" + idObject + "?idMember="
          + idMember,
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

  /**
   * Mark all notifications shown. /!\ There is no version update because of the non-sensibility of
   * the send_notification field /!\
   *
   * @returns list of interest updated.
   */
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

  /**
   * Get all the notification of a member by his token.
   *
   * @returns  interest List filtered with notifications
   */
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

  /**
   * Assign an object to a member interested.
   *
   * @param idObject the object given
   * @param idMember the id member that will receive the object
   * @param version the interest version that we need to send to the back to compare
   * @param versionOffer the offer version that we need to send to the back to compare
   * @param versionObject the object version that we need to send to the back to compare
   * @returns object updated.
   */
  async assignOffer(idObject, idMember, version, versionOffer, versionObject) {
    let response;
    let toast = NotificationSA.prototype.getNotification("bottom");
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
          title: "Le membre a été assigné"
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

  /**
   * Get the count of interested people of an object.
   *
   * @param idObject the object we want to retrieve the interest count
   * @returns  count of interests and a boolean if the user is one of the interested
   */
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

  /**
   * Get all the interests of an object.
   *
   * @param idObject of the object.
   * @returns interestDTO List
   */
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

  /**
   * Get an interest, by the id of the interested member and the id of the object.
   *
   * @param idObject  id object of the interest.
   * @returns a json of the interest.
   */
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

  /**
   * Add one interest.
   *
   * @param idObject the object will receive the interest.
   * @param date the date when the interest was added.
   * @param isCalled if the member want to be called
   * @param versionOffer the offer version that we need to send to the back to compare
   * @param versionObject the object version that we need to send to the back to compare
   * @returns a json of the interest with the Member who is interested in.
   */
  async addOne(idObject, date, isCalled, versionObject, versionOffer) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "idObject": idObject,
          "availabilityDate": date,
          "isCalled": isCalled,
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
    let toast = NotificationSA.prototype.getNotification("bottom");
    if (!response.ok) {
      response.text().then((msg) => {
        Swal.close();
        toast.fire({
          icon: 'error',
          title: msg
        });
      })
    }
    if (response.status === 200) {
      return await response.json();
    }
  }

}

export default InterestLibrary;