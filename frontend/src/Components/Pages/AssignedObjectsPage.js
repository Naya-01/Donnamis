import SearchBar from "../Module/SearchBar";
import CardList from "../Module/CardList";
import OfferLibrary from "../../Domain/OfferLibrary";
import MemberLibrary from "../../Domain/MemberLibrary";
import {RedirectWithParamsInUrl} from "../Router/Router";

/**
 * Render the Assigned objects page
 */
const AssignedObjectsPage = async () => {
  const user = await MemberLibrary.prototype.getUserByHisToken()
  await SearchBar("Objets attribués", true, false, true, "Rechercher un objet",
      false, false);
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML += `<div id="card-list-div"></div>`

  const cardListDiv = document.getElementById("card-list-div");
  const offers = await OfferLibrary.prototype.getGivenAndAssignedOffers();
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
      cardListDiv.innerHTML = await CardList(offers);
    }
  });

  const searchButtonDiv = document.getElementById("searchButton");
  searchButtonDiv.addEventListener('click', async () => {
    cardListDiv.innerHTML = await CardList(offers);
  });
}

export default AssignedObjectsPage;