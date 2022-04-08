import {getSessionObject} from "../../utils/session";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import searchBar from "../Module/SearchBar";
import itemImage from "../../img/item.jpg";
import OfferLibrary from "../../Domain/OfferLibrary";
import ObjectLibrary from "../../Domain/ObjectLibrary";
import managementList from "../Module/ManagementList";
import button from "bootstrap/js/src/button";


const dictionary = new Map([
  ['interested', 'Disponible'],
  ['available', 'Disponible'],
  ['assigned', 'Attribué à xxxxx'],
  ['given', 'Donné à xxxx'],
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
        dictionary.get(object.object.status));

    const card = document.getElementById("member-card-" + object.idOffer);
    card.className += " clickable";

    const buttonCard = document.getElementById("button-card-" + object.idOffer);
    if (object.object.status !== "cancelled" && object.object.status !== "given") {
      const cancelButton = document.createElement("button");
      cancelButton.innerText = "Annuler";
      cancelButton.type = "button";
      cancelButton.className = "btn btn-danger mt-3 mx-1";
      cancelButton.addEventListener("click", async () => {
        await ObjectLibrary.prototype.cancelObject(
            object.object.idObject
        );
        Redirect("/myObjectsPage")
      });

      buttonCard.appendChild(cancelButton);
    }

    if (object.object.status !== "given" && object.object.status !== "assigned") {
      const viewAllInterestedMembers = document.createElement("button");
      viewAllInterestedMembers.innerText = "Voir les interessés";
      viewAllInterestedMembers.type = "button";
      viewAllInterestedMembers.className = "btn btn-primary mt-3 mx-1";

      const notificationInterested = document.createElement("span");
      notificationInterested.className = "badge badge-light";
      notificationInterested.innerText = "4";
      viewAllInterestedMembers.appendChild(notificationInterested);

      buttonCard.appendChild(viewAllInterestedMembers);
    }








    if (object.object.status === "cancelled") {
      const reofferButton = document.createElement("button");
      reofferButton.innerText = "Offrir à nouveau";
      reofferButton.type = "button";
      reofferButton.className = "btn btn-success mt-3 mx-1";

      buttonCard.appendChild(reofferButton);
    }

    if (object.object.status === "assigned") {
      const viewReceiverButton = document.createElement("button");
      viewReceiverButton.innerText = "Voir le receveur";
      viewReceiverButton.type = "button";
      viewReceiverButton.className = "btn btn-primary mt-3 mx-1";

      const nonRealisedOfferButton = document.createElement("button");
      nonRealisedOfferButton.innerText = "Non réalisée";
      nonRealisedOfferButton.type = "button";
      nonRealisedOfferButton.className = "btn btn-danger mt-3 mx-1";

      const offeredObjectButton = document.createElement("button");
      offeredObjectButton.innerText = "Objet donné";
      offeredObjectButton.type = "button";
      offeredObjectButton.className = "btn btn-success mt-3 mx-1";
      offeredObjectButton.addEventListener("click",async  ()=>{
        await OfferLibrary.prototype.updateOffer(
            object.idOffer,
            object.timeSlot,
            object.object.description,
            object.object.type.idType,
            "given");
        Redirect("/myObjectsPage");
      });

      //buttonCard.appendChild(viewReceiverButton);
      buttonCard.appendChild(nonRealisedOfferButton);
      buttonCard.appendChild(offeredObjectButton);
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