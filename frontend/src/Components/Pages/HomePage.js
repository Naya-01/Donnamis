import OfferLibrary from "../../Domain/OfferLibrary";
import {RedirectWithParamsInUrl} from "../Router/Router";
import cardList from "../Module/CardList";
import searchBar from "../Module/SearchBar";

const offerLibrary = new OfferLibrary();

/**
 * Render the HomePage
 */
const HomePage = async () => {
  const pageDiv = document.querySelector("#page");
  //create the search bar
  await searchBar("Accueil", false, false, false, false, "");
  //make the html page
  pageDiv.innerHTML += await cardList(await offerLibrary.getAllLastOffers());

  //add event listener to each card offer
  for (const offer of pageDiv.querySelectorAll(".clickable")) {
    offer.addEventListener("click", async (e) => {
      e.preventDefault();
      let offerId = parseInt(e.currentTarget.dataset.elementId);
      RedirectWithParamsInUrl("/objectDetails", "?idOffer=" +
          offerId);
    });
  }
};

export default HomePage;
