import SearchBar from "../Module/SearchBar";
import CardList from "../Module/CardList";
import OfferLibrary from "../../Domain/OfferLibrary";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import {getSessionObject} from "../../utils/session";

/**
 * Render the Assigned objects page
 */
const AssignedObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  await SearchBar("Objets attribués", true, false, false, "Rechercher un objet",
      false, false);
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML += `<div id="card-list-div"></div>`

  const cardListDiv = document.getElementById("card-list-div");
  const offers = await OfferLibrary.prototype.getGivenAndAssignedOffers("");
  cardListDiv.innerHTML = await CardList(offers);

  for (const offer of pageDiv.querySelectorAll(".clickable")) {
    offer.addEventListener("click", async (e) => {
      e.preventDefault();
      let offerId = parseInt(e.currentTarget.dataset.elementId);
      RedirectWithParamsInUrl("/objectDetails", "?idOffer=" +
          offerId);
    });
  }

  const searchBar = document.getElementById("searchBar");
  searchBar.addEventListener('keyup', async (e) => {
    if (e.key === 'Enter') {
      const offers = await OfferLibrary.prototype.getGivenAndAssignedOffers(searchBar.value);
      cardListDiv.innerHTML = await CardList(offers);
    }
  });

  const searchButtonDiv = document.getElementById("searchButton");
  searchButtonDiv.addEventListener('click', async () => {
    const offers = await OfferLibrary.prototype.getGivenAndAssignedOffers(searchBar.value);
    cardListDiv.innerHTML = await CardList(offers);
  });
}

export default AssignedObjectsPage;