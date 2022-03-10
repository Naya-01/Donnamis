import {getSessionObject} from "../../utils/session";
import searchBar from "../Module/SearchBar";

/**
 * Render the Objects page
 */

const AllObjectsPage = async () => {
  searchBar();

  const pageDiv = document.querySelector("#page");

  pageDiv.innerHTML += `<div id="offers-list"></div>`;
  const offersList = document.getElementById("offers-list");

  await displayOffers("", offersList);
  const searchBarInput = document.getElementById("search-bar-input");
  searchBarInput.addEventListener('keyup', async () => {
    await displayOffers(searchBarInput.value, offersList);
  });

};

// Display clients
const displayOffers = async (searchPattern, pageDiv) => {
  const offers = await getOffers(searchPattern);
  if (!offers) {
    pageDiv.innerHTML = `<p>Aucun objet</p>`;
  }
  pageDiv.innerHTML = ``;
  for (const property in offers) {
    console.log(offers[property].object.type)

    pageDiv.innerHTML += `
      <p>
          ${offers[property].idOffer} 
          ${offers[property].date} 
          ${offers[property].timeSlot}
          ${offers[property].object.description}
          ${offers[property].object.status}
          ${offers[property].object.image}
          ${offers[property].object.idOfferor}
          ${offers[property].object.type.idType}
          ${offers[property].object.type.typeName}
          ${offers[property].object.type.default}
      </p>
    `;
  }
}

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
      return false;
    }
    return await userData.json();
  } catch (err) {
    console.log(err);
  }
}

export default AllObjectsPage;
