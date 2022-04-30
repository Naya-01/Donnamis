import {getSessionObject} from "../utils/session";

class TypeLibrary {

  /**
   * Get all Default type.
   *
   * @returns {Promise<*>} list of the different object type
   */
  async getAllDefaultTypes() {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/type/allDefault", options);
    } catch (err) {
      console.log(err);
    }
    let allDefaultTypesJson;
    if (response.status === 200) {
      allDefaultTypesJson = await response.json();
    }
    return allDefaultTypesJson;
  }
}

export default TypeLibrary;