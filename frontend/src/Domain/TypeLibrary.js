import {getSessionObject} from "../utils/session";

class TypeLibrary {
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
    console.log(allDefaultTypesJson);
    return allDefaultTypesJson;
  }
}

export default TypeLibrary;