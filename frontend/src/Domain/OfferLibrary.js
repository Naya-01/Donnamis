import {getSessionObject} from "../utils/session";

class OfferLibrary {
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
}

export default OfferLibrary;