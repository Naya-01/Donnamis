import {getSessionObject} from "../utils/session";
import Swal from "sweetalert2";
import NotificationSA from "../Components/Module/NotificationSA";

class OfferLibrary {

  /**
   * Get last offer of an object.
   *
   * @param id the id of the offer
   * @returns {Promise<*>} the offer in json and the status
   */
  async getLastOfferById(id) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers/last/" + id, options);
    } catch (err) {
      console.log(err);
    }
    let current_offer;
    if (response.status === 200) {
      current_offer = await response.json();
    }

    return current_offer;
  }

  /**
   * Get an offer by its id.
   * @param id the id of the offer
   * @returns {Promise<*>} the offer in json and the status
   */
  async getOfferById(id) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers/getById/" + id, options);
    } catch (err) {
      console.log(err);
    }
    let current_offer;
    if (response.status === 200) {
      current_offer = await response.json();
    }

    return current_offer;
  }

  /**
   * Make an offer for an Object.
   *
   * @param timeSlot the time slot of the new offer
   * @param idObject the object that will receive a new offer
   * @param version
   * @returns {Promise<*>} the offer in json and the status
   */
  async addOffer(timeSlot, idObject, version = 1) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "timeSlot": timeSlot,
          "object": {
            "idObject": idObject,
            "version": version
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers/newOffer", options);
      let toast = NotificationSA.prototype.getNotification("bottom");
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
          title: "L'objet est de nouveau disponible"
        })
      }
    } catch (err) {
      console.log(err);
    }
    let current_offer;
    if (response.status === 200) {
      current_offer = await response.json();
    }

    return current_offer;
  }

  /**
   * Make an Object with his offer.
   *
   * @param timeSlot the time slot of the new offer
   * @param description the object description
   * @param typeName the object type
   * @returns {Promise<*>} the offer in json and the status
   */
  async addFirstOffer(timeSlot, description, typeName) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "timeSlot": timeSlot,
          "object": {
            "type": {
              "idType": 0,
              "typeName": typeName,
            },
            "description": description,
            "image": null,
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers", options);
      if (response.status === 200) {
        return await response.json();
      }
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
  }

  /**
   * Update an offer
   * @param id the id of the offer
   * @param timeSlot the time slot of the offer
   * @param description the description of the object
   * @param idType the id of the type
   * @param statusOffer
   * @param statusObject
   * @param versionObject
   * @param versionOffer
   * @returns {Promise<*>} the offer in json
   */
  async updateOffer(id, timeSlot, description, idType, statusOffer,
      statusObject, versionObject, versionOffer) {
    let response;
    try {
      let options = {
        method: "PUT",
        body: JSON.stringify({
          "idOffer": id,
          "timeSlot": timeSlot,
          "status": statusOffer,
          "object": {
            "description": description,
            "status": statusObject,
            "version": versionObject
          },
          "version": versionOffer
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers", options);
    } catch (err) {
      console.log(err);
    }
    let toast = NotificationSA.prototype.getNotification("bottom");
    if (response.status === 200) {
      return await response.json();
    }
    if (!response.ok) {
      response.text().then((msg) => {
        Swal.close();
        toast.fire({
          icon: 'error',
          title: msg
        });
      })
    }
  }

  /**
   * Get the last 6 offers with a different status of cancelled & not collected.
   *
   * @returns {Promise<*>} an list offer in the json format
   */
  async getAllLastOffers() {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      };
      response = await fetch("api/offers/lasts", options);
    } catch (err) {
      console.log(err);
    }
    let allLastOffers;
    if (response.status === 200) {
      allLastOffers = await response.json();
    }

    return allLastOffers;
  }

  /**
   * Count the offers of a member.
   *
   * @param idMember the id member
   * @returns {Promise<null|any>}
   */
  async getCountOffers(idMember) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers/countOffers/" + idMember, options);
    } catch (err) {
      console.log(err);
    }
    if (response.status === 200) {
      return await response.json();
    }
    return null;
  }

  /**
   * Search the offers with different option search.
   *
   * @param searchPattern it's the username, city, postCode
   * @param self true if he wants to search for himself
   * @param type the object type
   * @param objStatus the object status
   * @param date the limit date
   * @returns {Promise<boolean|any>}  an list offer in the json format or nothing
   */
  async getOffers(searchPattern, self, type, objStatus, date) {
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken
        },
      };
      let query = "/api/offers?search-pattern=" + searchPattern + "&type="
          + type + "&status=" + objStatus + "&date=" + date;
      if (self) {
        query += "&self=" + self;
      }
      let userData = await fetch(query, options);
      if (!userData.ok) {
        return false;
      }
      return await userData.json();
    } catch (err) {
      console.log(err);
    }
  }

  /**
   * Get Object received by à member.
   *
   * @param idReceiver id of the member
   * @returns {Promise<boolean|any>} an list offer in the json format or nothing
   */
  async getGivenOffers(idReceiver) {
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken
        },
      };
      let query = "/api/offers/givenOffers/" + idReceiver;
      let userData = await fetch(query, options);
      if (!userData.ok) {
        return false;
      }
      return await userData.json();
    } catch (err) {
      console.log(err);
    }
  }

  /**
   * Get Object received and assigned to a member.
   *
   * @returns {Promise<boolean|any>} an list offer in the json format or nothing
   */
  async getGivenAndAssignedOffers(search) {
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken
        },
      };
      let query = "/api/offers/givenAndAssignedOffers?search=" + search;
      let userData = await fetch(query, options);
      if (!userData.ok) {
        return false;
      }
      return await userData.json();
    } catch (err) {
      console.log(err);
    }
  }

  /**
   * Give an Object to  a member.
   *
   * @param idObject the id object
   * @param versionOffer the offer version that we need to send to the back to compare
   * @param versionObject the object version that we need to send to the back to compare
   * @returns {Promise<void>} the object that we gave in json format
   */
  async giveObject(idObject, versionOffer, versionObject) {
    try {
      let options = {
        method: 'POST',
        body: JSON.stringify({
          "version": versionOffer,
          "object": {
            "idObject": idObject,
            "version": versionObject
          },
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      const response = await fetch('api/offers/give', options);
      let toast = NotificationSA.prototype.getNotification("bottom");
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
          title: "L'objet a été donné"
        })
      }
      return response;
    } catch (err) {
      console.log(err);
    }
  }

  /**
   * Mark an Object to 'cancelled'.
   *
   * @param idOffer the if offer
   * @param versionOffer the offer version that we need to send to the back to compare
   * @param versionObject the object version that we need to send to the back to compare
   * @returns {Promise<void>} the offer cancelled in json format
   */
  async cancelObject(idOffer, versionOffer, versionObject) {
    try {
      let options = {
        method: 'POST',
        body: JSON.stringify({
          "idOffer": idOffer,
          "version": versionOffer,
          "object": {
            "version": versionObject
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      const response = await fetch('api/offers/cancelOffer/', options);
      let toast = NotificationSA.prototype.getNotification("bottom");
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
          title: "L'objet a été annulé"
        })
      }
      return response;
    } catch (err) {
      console.log(err);
    }

  }

  /**
   * Mark an Object to 'not_collected'.
   *
   * @param idOffer the id offer
   * @param versionOffer the offer version that we need to send to the back to compare
   * @param versionObject the object version that we need to send to the back to compare
   * @returns {Promise<void>} the offer not_collected in json format
   */
  async notCollectedObject(idOffer, versionOffer, versionObject) {
    try {
      let options = {
        method: 'POST',
        body: JSON.stringify({
          "idOffer": idOffer,
          "version": versionOffer,
          "object": {
            "version": versionObject
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      const response = await fetch('api/offers/notCollected/', options);
      let toast = NotificationSA.prototype.getNotification("bottom");
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
          title: "L'objet a été signalé comme non collecté"
        })
      }
      return response;
    } catch (err) {
      console.log(err);
    }
  }

}

export default OfferLibrary;