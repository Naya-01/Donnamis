import {getSessionObject} from "../utils/session";
import Swal from "sweetalert2";
import NotificationSA from "../Components/Module/NotificationSA";

class ObjectLibrary {

  /**
   * Get the object by his id.
   * @param id of the object.
   * @returns {Promise<*>} the object in json.
   */
  async getObject(id) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/object/" + id, options);
    } catch (err) {
      console.log(err);
    }
    let actualObject;
    if (response.status === 200) {
      actualObject = await response.json();
    }
    return actualObject;

  }

  /**
   * Set a picture for the object.
   * @param formData is the picture data.
   * @param id of the object.
   * @param version of the update.
   * @returns {Promise<*>} the object in json.
   */
  async setImage(formData, id, version = 1) {
    let response;
    try {
      let options = {
        method: 'POST',
        body: formData,
        headers: {
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch(
          'api/object/setPicture/' + id + '?version=' + version, options)
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

}

export default ObjectLibrary;