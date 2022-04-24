import {getSessionObject} from "../utils/session";

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

  async addOffer(timeSlot, idObject) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "timeSlot": timeSlot,
          "object": {
            "idObject": idObject
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers/newOffer", options);
    } catch (err) {
      console.log(err);
    }
    let current_offer;
    if (response.status === 200) {
      current_offer = await response.json();
    }

    return current_offer;
  }

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
            "image": null, //TODO : change the image
          }
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
    let current_offer;
    if (response.status === 200) {
      current_offer = await response.json();
    }

    return current_offer;
  }

  /**
   * Update an offer
   * @param id the id of the offer
   * @param timeSlot the time slot of the offer
   * @param description the description of the object
   * @param idType the id of the type
   * @param status the status of the object
   * @returns {Promise<*>} the offer in json
   */
  async updateOffer(id, timeSlot, description, idType, statusOffer,
      statusObject) {
    let response;
    try {
      let options = {
        method: "PUT", //TODO : change to PUT
        body: JSON.stringify({
          "idOffer": id,
          "timeSlot": timeSlot,
          "status": statusOffer,
          "object": {
            "description": description,
            "status": statusObject,
          }
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
    let current_offer;
    if (response.status === 200) {
      current_offer = await response.json();
    }

    return current_offer;
  }

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

  async getOffers(searchPattern, self, type, objStatus) {
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").refreshToken
        },
      };
      let query = "/api/offers?search-pattern=" + searchPattern + "&type="
          + type + "&status=" + objStatus;
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

  async getGivenOffers(idReceiver) {
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").refreshToken
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

  async giveObject(idObject) {
    try {
      let options = {
        method: 'POST',
        body: JSON.stringify({
          "object": {
            "idObject": idObject
          },
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      await fetch('api/offers/give', options)
    } catch (err) {
      console.log(err);
    }
  }

  async cancelObject(idOffer) {
    try {
      let options = {
        method: 'POST',
        body: JSON.stringify({
          "idOffer": idOffer,
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      await fetch('api/offers/cancelOffer/', options)
    } catch (err) {
      console.log(err);
    }

  }

  async notCollectedObject(idOffer) {
    try {
      let options = {
        method: 'POST',
        body: JSON.stringify({
          "idOffer": idOffer,
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      await fetch('api/offers/notCollected/', options)
    } catch (err) {
      console.log(err);
    }
  }

}

export default OfferLibrary;