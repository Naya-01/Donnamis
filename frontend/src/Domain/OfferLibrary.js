import {getSessionObject} from "../utils/session";

class OfferLibrary {
  /**
   * Get an offer by its id.
   * @param id the id of the offer
   * @returns {Promise<*>} the offer in json
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
   * Update an offer
   * @param id the id of the offer
   * @param timeSlot the time slot of the offer
   * @param description the description of the object
   * @param idType the id of the type
   * @param typeName the type name of the type
   * @returns {Promise<*>} the offer in json
   */
  async updateOffer(id, timeSlot, description, idType, typeName) {
    let response;
    try {
      let options = {
        method: "POST", //TODO : change to PUT
        body: JSON.stringify({
          "idOffer": id,
          "timeSlot": timeSlot,
          "object": {
            "description": description,
            "type": {
              "idType": idType,
              "typeName": typeName
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
}

export default OfferLibrary;