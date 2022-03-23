import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import searchBar from "../Module/SearchBar";
import profilImage from "../../img/profil.png";
import OfferLibrary from "../../Domain/OfferLibrary";

/**
 * Render the page to see an object
 */
const MyObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }

  await searchBar("Mes objets", false, true, "Recherche un objet", true);
  const searchBarDiv = document.getElementById("searchBar");

  // Create object cards
  const memberCards = document.getElementById("page-body");
  const objects = await OfferLibrary.prototype.getOffers(searchBarDiv.value);
  console.log(objects)
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
    profileImage.src = profilImage;
    profileImage.alt = "profile image"
    profileImageDiv.appendChild(profileImage);

    divCard.appendChild(profileImageDiv);

    const informationMemberDiv = document.createElement("div");
    informationMemberDiv.className = "col-7 mt-3";

    const memberBaseInformationSpan = document.createElement("span");
    memberBaseInformationSpan.className = "fs-4";
    memberBaseInformationSpan.innerText =  "" + object.object.type.typeName + ": " + object.object.description;



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
  }

}

export default MyObjectsPage;