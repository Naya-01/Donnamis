import TypeLibrary from "../../Domain/TypeLibrary";
import profilImage from "../../img/profil.png";

const cardList = async (div) => {
  const buttonCardId = "button-card-" + member.memberId;

  const divCard = document.createElement("div");
  divCard.id = "member-card-" + member.memberId;
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
  memberBaseInformationSpan.innerText = " " + member.firstname + " "
      + member.lastname + " (" + member.username + ")";

  const memberAddressInformationSpan = document.createElement("span")
  memberAddressInformationSpan.className = "text-secondary fs-5";
  memberAddressInformationSpan.innerText = " " +
      member.address.buildingNumber + " " + member.address.street + " " +
      member.address.postcode + " " + member.address.commune + " " +
      member.address.country;

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
  cardForm.id = "card-form-" + member.memberId;
  divCard.appendChild(cardForm);

  div.appendChild(divCard);
}

export default cardList;