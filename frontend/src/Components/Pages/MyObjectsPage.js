import {getSessionObject} from "../../utils/session";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import searchBar from "../Module/SearchBar";
import itemImage from "../../img/item.jpg";
import OfferLibrary from "../../Domain/OfferLibrary";

/**
 * Render the page to see an object
 */
const MyObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  let status = "";
  await searchBar("Mes objets", true, false, true, "Recherche un objet", true,
      true);
  const searchBarDiv = document.getElementById("searchBar");
  await objectCards(searchBarDiv.value, "", status);
  const typeObject = document.getElementById("default-type-list");
  searchBarDiv.addEventListener('keyup', async (e) => {
    if (e.key === 'Enter') {
      let type = typeObject.options[typeObject.selectedIndex].value;
      if (type === "Tout") {
        type = "";
      }
      await objectCards(searchBarDiv.value, type, status);
    }
  });

  const searchButtonDiv = document.getElementById("searchButton");
  searchButtonDiv.addEventListener('click', async () => {
    let type = typeObject.options[typeObject.selectedIndex].value;
    if (type === "Tout") {
      type = "";
    }
    await objectCards(searchBarDiv.value, type, status);
  });

  let available = document.getElementById("btn-status-available");
  available.addEventListener('click', (e) =>{
    status="available";
  });

  let given = document.getElementById("btn-status-given");
  given.addEventListener('click', (e) =>{
    status="given";
  });

  let assigned = document.getElementById("btn-status-assigned");
  assigned.addEventListener('click', (e) =>{
    status="assigned";
  });

  let all = document.getElementById("btn-status-all");
  all.addEventListener('click', (e) =>{
    status="";
  });

  const addButton = document.getElementById("add-new-object-button");
  addButton.addEventListener('click', () => {
    Redirect("/addNewObjectPage")
  });
}


const objectCards = async (searchPattern, type, status) => {
  const memberCards = document.getElementById("page-body");
  const objects = await OfferLibrary.prototype.getOffers(searchPattern, true,
      type, status);
  memberCards.innerHTML = ``;
  for (const object of objects) {
    const buttonCardId = "button-card-" + object.memberId;

    const divCard = document.createElement("div");
    divCard.id = "member-card-" + object.idOffer;
    divCard.className = "row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded";

    const profileImageDiv = document.createElement("div");
    profileImageDiv.className = "col-1 m-auto";

    const profileImage = document.createElement("img");
    profileImage.className = "img-thumbnail";
    profileImage.src = itemImage;
    profileImage.alt = "item image"
    profileImageDiv.appendChild(profileImage);

    divCard.appendChild(profileImageDiv);

    const informationMemberDiv = document.createElement("div");
    informationMemberDiv.className = "col-7 mt-3 clickable";
    informationMemberDiv.id = "object-info";
    informationMemberDiv.addEventListener("click", async () => {
      RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" +
          object.idOffer);
    });

    const memberBaseInformationSpan = document.createElement("span");
    memberBaseInformationSpan.className = "fs-4";
    memberBaseInformationSpan.innerText = "" + object.object.type.typeName
        + ": " + object.object.description;

    const memberAddressInformationSpan = document.createElement("span")
    memberAddressInformationSpan.className = "text-secondary fs-5";
    memberAddressInformationSpan.innerText = object.object.status;

    informationMemberDiv.appendChild(memberBaseInformationSpan);
    informationMemberDiv.appendChild(document.createElement("br"));
    informationMemberDiv.appendChild(memberAddressInformationSpan);

    divCard.appendChild(informationMemberDiv);

    const buttonsCard = document.createElement("div");
    buttonsCard.className = "col-3 mb-4";

    const buttonInput = document.createElement("div");
    buttonInput.className = "d-grid gap-2 d-md-block";
    buttonInput.id = buttonCardId;

    if (object.object.status !== "cancelled") {
      const cancelButton = document.createElement("button");
      cancelButton.innerText = "Annuler";
      cancelButton.type = "button";
      cancelButton.className = "btn btn-danger";
      cancelButton.addEventListener("click", async (e) => {
        // e.preventDefault();
        await OfferLibrary.prototype.updateOffer(
            object.idOffer,
            object.timeSlot,
            object.object.description,
            object.object.type.idType,
            "cancelled");
        Redirect("/myObjectsPage")
        // console.log(object);

      });
      buttonInput.appendChild(cancelButton);
    }

    buttonsCard.appendChild(buttonInput);
    divCard.appendChild(buttonsCard);

    const cardForm = document.createElement("div");
    cardForm.id = "card-form-" + object.idOffer;
    divCard.appendChild(cardForm);

    memberCards.appendChild(divCard);

    const addButton = document.getElementById("add-new-object-button");
    addButton.addEventListener('click', () => {
      Redirect("/addNewObjectPage")
    });
  }
}

export default MyObjectsPage;