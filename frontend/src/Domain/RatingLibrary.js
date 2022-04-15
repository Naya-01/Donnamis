import {getSessionObject} from "../utils/session";

class RatingLibrary {
  async addRating(rating, comment, idObject) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "rating": rating,
          "comment": comment,
          "idObject": idObject
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/ratings", options);
    } catch (err) {
      console.log(err);
    }
    let current_rating;
    if (response.status === 200) {
      current_rating = await response.json();
    }

    return current_rating;
  }
}

export default RatingLibrary;