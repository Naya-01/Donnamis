import searchBar from "../Module/SearchBar";
import OfferLibrary from "../../Domain/OfferLibrary";
import cardList from "../Module/CardList";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import {getSessionObject} from "../../utils/session";

/**
 * Render the Objects page
 */
const AllObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
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
  let cancelled = document.getElementById("btn-status-cancelled");
  let not_collected = document.getElementById("btn-status-not_collected");
  let interested = document.getElementById("btn-status-interested");

  const actualizeCards = async () => {
    let type = typeObject.options[typeObject.selectedIndex].value;
    if (type === "Tout") {
      type = "";
    }
    const offers = await OfferLibrary.prototype.getOffers(searchBarInput.value,
        false, type, status);
    offersList.innerHTML = ``;
    if (!offers) {
      offersList.innerHTML = `<p>Aucun objet</p>`;
    }
    offersList.innerHTML = await cardList(offers);
    for (const offer of offersList.querySelectorAll(".clickable")) {
      offer.addEventListener("click", async (e) => {
        e.preventDefault();
        let offerId = parseInt(e.currentTarget.dataset.elementId);
        RedirectWithParamsInUrl("/objectDetails", "?idOffer=" + offerId);
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

  available.addEventListener('click', async () => {
    status = "available";
    await actualizeCards();
  });

  interested.addEventListener('click', async () => {
    status = "interested";
    await actualizeCards();
  });

  given.addEventListener('click', async () => {
    status = "given";
    await actualizeCards();
  });

  assigned.addEventListener('click', async () => {
    status = "assigned";
    await actualizeCards();
  });

  cancelled.addEventListener('click', async () => {
    status = "cancelled";
    await actualizeCards();
  });

  not_collected.addEventListener('click', async () => {
    status = "not_collected";
    await actualizeCards();
  });

  all.addEventListener('click', async () => {
    status = "";
    await actualizeCards();
  });

  const addButton = document.getElementById("add-new-object-button");
  addButton.addEventListener('click', () => {
    Redirect("/addNewObjectPage")
  });
};

export default AllObjectsPage;
