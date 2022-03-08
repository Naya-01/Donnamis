import {getSessionObject} from "../../utils/session";
import searchBar from "../Module/SearchBar";

/**
 * Render the Objects page
 */

const AllObjectsPage = async () => {
  const pageDiv = document.querySelector("#page");
  const offers = await getOffers();
  searchBar();
  for (const property in offers) {
    pageDiv.innerHTML += `
      <p>${offers[property].idOffer} ${offers[property].date} ${offers[property].timeSlot}</p>
    `;
  }

  const searchBarInput = document.getElementById("search-bar-input");
  searchBarInput.addEventListener('keyup', () => {
    return getOffers(searchBarInput.value);
  });

};

const getOffers = async (searchPattern) => {
  try {
    let options = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject("user").refreshToken
      },
    };
    let userData = await fetch(
        "/api/offers/all?search-pattern=" + searchPattern,
        options);
    if (!userData.ok) {
      throw new Error();
    }
    return userData.json();
  } catch (err) {
    console.log(err);
  }
}

export default AllObjectsPage;
