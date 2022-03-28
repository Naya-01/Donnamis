import {getSessionObject} from "../../utils/session";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import searchBar from "../Module/SearchBar";
import itemImage from "../../img/item.jpg";
import OfferLibrary from "../../Domain/OfferLibrary";
import managementList from "../Module/ManagementList";
import button from "bootstrap/js/src/button";


const dictionary = new Map([
  ['interested', 'Disponible'],
  ['available', 'Disponible'],
  ['assigned', 'En cours de donnation'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé']
]);

/**
 * Render the page to see an object
 */
const MyObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }

  await searchBar("Mes objets", true, false, true, "Recherche un objet", true, true);

  let status = "";
  const searchBarDiv = document.getElementById("searchBar");
  const typeObject = document.getElementById("default-type-list");

  const actualizeCards = async () => {
    let type = typeObject.options[typeObject.selectedIndex].value;
    if (type === "Tout") {
      type = "";
    }
    await objectCards(searchBarDiv.value, type, status);
  }

  await actualizeCards();
  searchBarDiv.addEventListener('keyup', async (e) => {
    if (e.key === 'Enter') {
      await actualizeCards();
    }
  });

  const searchButtonDiv = document.getElementById("searchButton");
  searchButtonDiv.addEventListener('click', async () => {
    await actualizeCards();
  });

  let available = document.getElementById("btn-status-available");
  available.addEventListener('click', async () =>{
    status = "available";
    await actualizeCards();
  });

  let given = document.getElementById("btn-status-given");
  given.addEventListener('click', async () =>{
    status = "given";
    await actualizeCards();
  });

  let assigned = document.getElementById("btn-status-assigned");
  assigned.addEventListener('click', async () =>{
    status = "assigned";
    await actualizeCards();
  });

  let all = document.getElementById("btn-status-all");
  all.addEventListener('click', async () =>{
    status = "";
    await actualizeCards();
  });

  const addButton = document.getElementById("add-new-object-button");
  addButton.addEventListener('click', () => {
    Redirect("/addNewObjectPage")
  });
}


const objectCards = async (searchPattern, type, status) => {
  const memberCards = document.getElementById("page-body");
  const objects = await OfferLibrary.prototype.getOffers(searchPattern, true, type, status);
  memberCards.innerHTML = ``;
  for (const object of objects) {
    managementList(object.idOffer, memberCards, itemImage,
        object.object.type.typeName + ": " + object.object.description,
        dictionary.get(object.object.status))

    if (object.object.status !== "cancelled") {
      const cancelButton = document.createElement("button");
      cancelButton.innerText = "Annuler";
      cancelButton.type = "button";
      cancelButton.className = "btn btn-danger";
      cancelButton.addEventListener("click", async () => {
        await OfferLibrary.prototype.updateOffer(
            object.idOffer,
            object.timeSlot,
            object.object.description,
            object.object.type.idType,
            "cancelled");
        Redirect("/myObjectsPage")
      });
      const buttonCard = document.getElementById("button-card-" + object.idOffer);
      buttonCard.appendChild(cancelButton);
    }

    const informationDiv = document.getElementById("information-object-" + object.idOffer);
    informationDiv.addEventListener('click', () => {
      RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" + object.idOffer);
    });

    const addButton = document.getElementById("add-new-object-button");
    addButton.addEventListener('click', () => {
      Redirect("/addNewObjectPage")
    });
  }
}

export default MyObjectsPage;