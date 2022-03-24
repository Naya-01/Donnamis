import {getSessionObject} from "../utils/session";

class OfferLibrary {
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

  async addOffer(timeSlot, description, typeName, idOfferor) {
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
            "status": "available",
            "image": null, //TODO : change the image
            "idOfferor": idOfferor
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
  async updateOffer(id, timeSlot, description, idType, status) {
    let response;
    try {
      let options = {
        method: "POST", //TODO : change to PUT
        body: JSON.stringify({
          "idOffer": id,
          "timeSlot": timeSlot,
          "object": {
            "description": description,
            "image": null,
            "status": status,
            "type": {
              "idType": idType
            }
          }
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/offers/update", options);
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
}

export default OfferLibrary;