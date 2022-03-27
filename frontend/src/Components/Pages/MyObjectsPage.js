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

  await searchBar("Mes objets", true, false, true, "Recherche un objet", true);
  const searchBarDiv = document.getElementById("searchBar");
  await objectCards(searchBarDiv.value);
  searchBarDiv.addEventListener('keyup', async (e) => {
    if (e.key === 'Enter') {
      await objectCards(searchBarDiv.value);
    }
  });

  const searchButtonDiv = document.getElementById("searchButton");
  searchButtonDiv.addEventListener('click', async () => {
    await objectCards(searchBarDiv.value);
  });

}

const objectCards = async (searchPattern) => {
  const memberCards = document.getElementById("page-body");
  const objects = await OfferLibrary.prototype.getOffers(searchPattern, true);
  memberCards.innerHTML = ``;
  for (const object of objects) {
    const buttonCardId = "button-card-" + object.memberId;

    const divCard = document.createElement("div");
    divCard.id = "member-card-" + object.idOffer;
    divCard.className = "row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded clickable";

    const profileImageDiv = document.createElement("div");
    profileImageDiv.className = "col-1 m-auto";

    const profileImage = document.createElement("img");
    profileImage.className = "img-thumbnail";
    profileImage.src = itemImage;
    profileImage.alt = "item image"
    profileImageDiv.appendChild(profileImage);

    divCard.appendChild(profileImageDiv);

    const informationMemberDiv = document.createElement("div");
    informationMemberDiv.className = "col-7 mt-3";

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

    buttonsCard.appendChild(buttonInput);
    divCard.appendChild(buttonsCard);

    const cardForm = document.createElement("div");
    cardForm.id = "card-form-" + object.idOffer;
    divCard.appendChild(cardForm);

    memberCards.appendChild(divCard);
    const card = document.getElementById("member-card-" + object.idOffer)
    card.addEventListener("click", async () => {
      RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" +
          object.idOffer);
    });
    const addButton = document.getElementById("add-new-object-button");
    addButton.addEventListener('click', () => {
      Redirect("/addNewObjectPage")
    });
  }
}

export default MyObjectsPage;