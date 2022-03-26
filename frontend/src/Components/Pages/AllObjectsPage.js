import {getSessionObject} from "../../utils/session";
import searchBar from "../Module/SearchBar";

/**
 * Render the Objects page
 */

const AllObjectsPage = async () => {
  await searchBar("Tous les objets", false, true, "Recherche un objet", true);

  const pageDiv = document.querySelector("#page");

  pageDiv.innerHTML += `<div id="offers-list"></div>`;
  const offersList = document.getElementById("offers-list");

  await displayOffers("", offersList);
  const searchBarInput = document.getElementById("searchBar");
  const searchButtonInput = document.getElementById("searchButton");
  searchButtonInput.addEventListener('click', async () => {
    await displayOffers(searchBarInput.value, offersList);
  });

};

// Display clients
const displayOffers = async (searchPattern, pageDiv) => {
  const offers = await getOffers(searchPattern);
  pageDiv.innerHTML = ``;
  if (!offers) {
    pageDiv.innerHTML = `<p>Aucun objet</p>`;
  }
  for (const property in offers) {
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



export default AllObjectsPage;
