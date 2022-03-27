import searchBar from "../Module/SearchBar";
import OfferLibrary from "../../Domain/OfferLibrary";
import cardList from "../Module/CardList";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";

/**
 * Render the Objects page
 */

const AllObjectsPage = async () => {
  await searchBar("Tous les objets", true, false, true, "Recherche un objet",
      true);

  const pageDiv = document.querySelector("#page");

  pageDiv.innerHTML += `<div id="offers-list"></div>`;
  const offersList = document.getElementById("offers-list");

  await displayOffers("", offersList,"");
  const searchBarInput = document.getElementById("searchBar");
  const searchButtonInput = document.getElementById("searchButton");
  searchButtonInput.addEventListener('click', async () => {
    console.log(typeObject.options[typeObject.selectedIndex].value);
    let type = typeObject.options[typeObject.selectedIndex].value;
    if(type==="Tout") type="";
    await displayOffers(searchBarInput.value, offersList,type);
  });
  const typeObject = document.getElementById("default-type-list");

  searchBarInput.addEventListener('keyup', async (e) => {
    if (e.key === "Enter") {
      console.log(typeObject.options[typeObject.selectedIndex].value);
      let type = typeObject.options[typeObject.selectedIndex].value;
      if(type==="Tout") type="";
      await displayOffers(searchBarInput.value, offersList,type);
    }
  });
  const addButton = document.getElementById("add-new-object-button");
  addButton.addEventListener('click', () => {
    Redirect("/addNewObjectPage")
  });

};

// Display clients
const displayOffers = async (searchPattern, pageDiv, type) => {
  const offers = await OfferLibrary.prototype.getOffers(searchPattern, false, type);
  pageDiv.innerHTML = ``;
  if (!offers) {
    pageDiv.innerHTML = `<p>Aucun objet</p>`;
  }
  pageDiv.innerHTML = await cardList(offers);
  for (const offer of pageDiv.querySelectorAll(".clickable")) {
    offer.addEventListener("click", async (e) => {
      e.preventDefault();
      let offerId = parseInt(e.currentTarget.dataset.elementId);
      RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" +
          offerId);
    });
  }

}

export default AllObjectsPage;
