import {getSessionObject} from "../utils/session";
import NotificationSA from "../Components/Module/NotificationSA";
import Swal from "sweetalert2";

class RatingLibrary {

  /**
   * Add a rating on the API.
   * @param rating from 0 to 5.
   * @param comment of the rating.
   * @param idObject of the critic.
   * @returns {Promise<*>} the new rating in json.
   */
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
    if (response.status === 200) {
      return await response.json();
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
   * Get a rating by an id object.
   * @param idObject of the object.
   * @returns {Promise<*>} the rating in json.
   */
  async getOne(idObject) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/ratings/" + idObject, options);
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