/**
 *  A module to display a customized card that will appear in a div
 *
 * @param uniqueId the unique id of the offer
 * @param mainDiv the div to place this module
 * @param image the image at the left of the card
 * @param primaryText the primary text
 * @param secondaryText the secondary text
 * @param overlayId if you want to create child management list (adjust the name of the card to difference it from child)
 */
const managementList = (uniqueId, mainDiv, image, primaryText, secondaryText, overlayId = null) => {
  const buttonCardId = "button-card-" + uniqueId;

  let divCardId = "member-card-" + uniqueId;
  if (overlayId) {
    divCardId = divCardId + "-" + overlayId;
  }

  const divCard = document.createElement("div");
  divCard.id = divCardId;
  divCard.className = "row border border-1 mt-5 shadow p-3 mb-5 bg-body rounded";

  const pictureDiv = document.createElement("div");
  pictureDiv.className = "col-1 m-auto";

  const picture = document.createElement("img");
  picture.className = "img-thumbnail";
  picture.src = image;
  picture.alt = "image"
  pictureDiv.appendChild(picture);

  divCard.appendChild(pictureDiv);

  const informationDiv = document.createElement("div");
  informationDiv.className = "col-8 mt-3";
  informationDiv.id = "information-object-" + uniqueId;

  const baseInformationSpan = document.createElement("span");
  baseInformationSpan.className = "fs-5";
  baseInformationSpan.innerText = primaryText;

  const informationSpan = document.createElement("span")
  informationSpan.className = "text-secondary";
  informationSpan.innerText = secondaryText;

  informationDiv.appendChild(baseInformationSpan);
  informationDiv.appendChild(document.createElement("br"));
  informationDiv.appendChild(informationSpan);

  divCard.appendChild(informationDiv);

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