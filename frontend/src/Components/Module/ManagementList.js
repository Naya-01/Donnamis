
const managementList = (uniqueId, mainDiv, image, primaryText, secondaryText, overlayId = null) => {
  const buttonCardId = "button-card-" + uniqueId;

  let divCardId = "member-card-" + uniqueId;
  if (overlayId) {
    divCardId = divCardId + "-" + overlayId;
  }

  const divCard = document.createElement("div");
  divCard.id = divCardId;
  divCard.className = "row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded";

  const profileImageDiv = document.createElement("div");
  profileImageDiv.className = "col-1 m-auto";

  const profileImage = document.createElement("img");
  profileImage.className = "img-thumbnail";
  profileImage.src = image;
  profileImage.alt = "image"
  profileImageDiv.appendChild(profileImage);

  divCard.appendChild(profileImageDiv);

  const informationMemberDiv = document.createElement("div");
  informationMemberDiv.className = "col-7 mt-3";
  informationMemberDiv.id = "information-object-" + uniqueId;

  const memberBaseInformationSpan = document.createElement("span");
  memberBaseInformationSpan.className = "fs-4";
  memberBaseInformationSpan.innerText = primaryText;

  const memberAddressInformationSpan = document.createElement("span")
  memberAddressInformationSpan.className = "text-secondary fs-5";
  memberAddressInformationSpan.innerText = secondaryText;

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
  cardForm.id = "card-form-" + uniqueId;
  divCard.appendChild(cardForm);

  mainDiv.appendChild(divCard);
}

export default managementList;