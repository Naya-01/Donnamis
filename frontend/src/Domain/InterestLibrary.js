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
      response = await fetch("api/interest/count/" + idObject, options);
    } catch (err) {
      console.log(err);
    }
    let allInterests;
    if (response.status === 200) {
      allInterests = await response.json();
    }
    return allInterests;
  }

  async addOne(idObject, date) {
    let response;
    try {
      let options = {
        method: "POST",
        body: JSON.stringify({
          "object": {
            "idObject": idObject
          },
          "availabilityDate": date
        }),
        headers: {
          "Content-Type": "application/json",
          "Authorization": getSessionObject("user").accessToken,
        },
      };
      response = await fetch("api/interest", options);
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

export default InterestLibrary;