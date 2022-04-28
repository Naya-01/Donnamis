import {getSessionObject} from "../utils/session";

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
          "Content-Type": "application/json",
        },
      };
      response = await fetch(
          'api/object/setPicture/' + id + '?version=' + version, options)
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

export default ObjectLibrary;