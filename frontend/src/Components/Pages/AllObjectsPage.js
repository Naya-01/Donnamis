import searchBar from "../Module/SearchBar";
import OfferLibrary from "../../Domain/OfferLibrary";
import cardList from "../Module/CardList";
import { RedirectWithParamsInUrl } from "../Router/Router";

/**
 * Render the Objects page
 */

const AllObjectsPage = async () => {
  await searchBar("Toutes les offres", true, false, true, "Recherche une offre",
      true, true);

  let status = "";

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML += `<div id="offers-list"></div>`;

  const offersList = document.getElementById("offers-list");
  const searchBarInput = document.getElementById("searchBar");
  const searchButtonInput = document.getElementById("searchButton");
  const typeObject = document.getElementById("default-type-list");
  const available = document.getElementById("btn-status-available");
  const all = document.getElementById("btn-status-all");
  const given = document.getElementById("btn-status-given");
  const assigned = document.getElementById("btn-status-assigned");

  const actualizeCards = async () => {
    let type = typeObject.options[typeObject.selectedIndex].value;
    if (type === "Tout") {
      type = "";
    }
    console.log(status)
    const offers = await OfferLibrary.prototype.getOffers(searchBarInput.value, false, type,status);
    offersList.innerHTML = ``;
    if (!offers) {
      offersList.innerHTML = `<p>Aucun objet</p>`;
    }
    offersList.innerHTML = await cardList(offers);
    for (const offer of offersList.querySelectorAll(".clickable")) {
      offer.addEventListener("click", async (e) => {
        e.preventDefault();
        let offerId = parseInt(e.currentTarget.dataset.elementId);
        RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" + offerId);
      });
    }
  }

  await actualizeCards();

  searchButtonInput.addEventListener('click', async () => {
    await actualizeCards();
  });

  searchBarInput.addEventListener('keyup', async (e) => {
    if (e.key === 'Enter') {
      await actualizeCards();
    }
  });

  searchButtonInput.addEventListener('click', async () => {
    await actualizeCards();
  });

  available.addEventListener('click', async () =>{
    status = "available";
    await actualizeCards();
  });

  given.addEventListener('click', async () =>{
    status = "given";
    await actualizeCards();
  });

  assigned.addEventListener('click', async () =>{
    status = "assigned";
    await actualizeCards();
  });

  all.addEventListener('click', async () =>{
    status = "";
    await actualizeCards();
  });

};

export default AllObjectsPage;
