import {getSessionObject} from "../utils/session";

class InterestLibrary {
  async getInterestedCount(idObject) {
    let response;
    try {
      let options = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest/count/"+idObject, options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    }
    return allInterests;
  }
}

export default InterestLibrary;