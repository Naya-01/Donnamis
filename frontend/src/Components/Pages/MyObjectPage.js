import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import noImage from "../../img/noImage.png";
import OfferLibrary from "../../Domain/OfferLibrary";
import Notification from "../Module/Notification";
import MemberLibrary from "../../Domain/MemberLibrary";

const memberLibrary = new MemberLibrary();
const offerLibrary = new OfferLibrary();
const dictionnary = new Map([
  ['interested', 'Disponible'],
  ['available', 'Disponible'],
  ['assigned', 'En cours de donnation'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé']
]);
let idOffert;
let english_status;
let idType;
let description;
let time_slot;
let form = false;

/**
 * Render the page to see an object
 */
const MyObjectPage = async () => {
  // If he's not log in he's redirect to the homepage
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }

  let url_string = window.location;
  let url = new URL(url_string);
  let idOfferUrl = url.searchParams.get("idOffer");

  if (!idOfferUrl || idOfferUrl <= 0) {
    Redirect("/");
    return;
  }

  idOffert = idOfferUrl;
  // GET all informations of the object
  let offer = await offerLibrary.getOfferById(idOffert);
  if (offer === undefined) {
    Redirect("/");
    return;
  }

  idType = offer.object.type.idType;
  description = offer.object.description;
  time_slot = offer.timeSlot;

  english_status = offer.object.status;
  let french_status = dictionnary.get(english_status);

  // Get the id of the member
  let member = await memberLibrary.getUserByHisToken();
  let idMemberConnected = member.memberId;

  // GET all interests
  let nbMembersInterested = 3; //TODO : request to have all interests
  // Construct all the HTML

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML =
      `<div class="container p-3">
      <div class="mx-5 my-5">
      <h3 id="titleObject" class=pb-3></h3>
      <div class="card">
        <!-- Body of the card -->
        <div class="card-body">
          <p class="card-text">
              <div class="row justify-content-start p-2">
                <!-- The image -->
                <div class="col-4">
                  <img id="image" alt="no image" width="75%" src="${noImage}"/>
                </div>
                <!-- The description -->
                <div class="col-8">
                    <div class="mb-3">
                      <h5>
                        <label for="description_object" class="form-label">
                          Description
                        </label>
                       </h5>
                      <p id="description_object">${description}</p>
                    </div>
                </div>
              </div>
              <div class="row p-2">
                <!-- the time slot-->
                <div class="col">
                    <div class="mb-3">
                      <h5>
                        <label for="time_slot" class="form-label">
                          Plage horaire
                         </label>
                       </h5>
                      <p id="time_slot">${time_slot}</p>
                    </div>
                </div>
              <!-- The type -->
                <div class="col">
                  <div class="mb-3">
                    <h5><label for="type">Type</label></h5>
                    <p id="type">${offer.object.type.typeName}</p>
                  </div>
                </div>
                <!-- The status -->
                <div class="col">
                  <div class="mb-3">
                    <h5><label for="status">Etat de l'objet</label></h5>
                    <p id="status">${french_status}</p>
                  </div>
                </div>
              </div>
              <!-- The confirm button -->
              <div id="divB" class="text-center p-2">
                <input type="button"  class="btn btn-primary" 
                  id="modifyObjectButton" value="Modifier">
              </div>
              <div id="nbMembersInterested" class="text-center p-2">
                <p>${nbMembersInterested} personnes sont intéressées par 
                  cet objet</p>
              </div>
          </p>
        </div>
      </div>
    </div>
  </div>`;
  let button = document.getElementById("modifyObjectButton");
  if (idMemberConnected === offer.object.idOfferor) { //TODO : put ===
    document.getElementById("titleObject").textContent = "Votre objet";
    button.addEventListener("click", changeToForm);
  } else {
    //TODO : get user by id
    let memberGiver = await memberLibrary.getUserByHisId(
        offer.object.idOfferor);
    button.id = "interestedButton";
    button.value = "Je suis interessé";
    button.addEventListener("click", () => {
      //TODO : POST an interest
      button.disabled = true;
    });
    document.getElementById("titleObject").textContent = "L'objet de "
        + memberGiver.user.username;
  }

}

/**
 * Change elements of the html to have a text.
 * @param {Event} e : evenement
 */
async function changeToText(e) {
  // Make a simple image
  let old = document.getElementById("span_image");
  let image = document.createElement("img");
  image.id = "image";
  image.alt = "no image";
  image.style.width = "75%";
  image.setAttribute("src", noImage);
  old.parentNode.replaceChild(image, old);

  // Make a simple paragraph for description
  old = document.getElementById("description_object");
  let desc_text = document.createElement("p");
  desc_text.id = "description_object";
  desc_text.innerHTML = description;
  old.parentNode.replaceChild(desc_text, old);

  // Make a simple paragraph for time slot
  old = document.getElementById("time_slot");
  let time_slot_text = document.createElement("p");
  time_slot_text.id = "time_slot";
  time_slot_text.rows = "3";
  time_slot_text.innerHTML = time_slot;
  old.parentNode.replaceChild(time_slot_text, old);

  // Replace the button "Confirmer" by a "Modifier" one
  old = document.getElementById("divButtons");
  let new_button = document.createElement("input");
  new_button.type = "button";
  new_button.className = "btn btn-primary";
  new_button.value = "Modifier";
  new_button.id = "modifyObjectButton";
  new_button.addEventListener("click", changeToForm);
  old.parentNode.replaceChild(new_button, old);

  form = !form;
}

/**
 * Change elements of the html to have a form.
 * @param {Event} e : evenement
 */
async function changeToForm(e) {
  e.preventDefault();
  // Make the image clickable to import a file
  let old = document.getElementById("image");
  let span_image = document.createElement("span");
  span_image.id = "span_image";
  span_image.className = "img_file_input";
  let label_image = document.createElement("label");
  label_image.setAttribute("for", "file_input");
  let image = document.createElement("img");
  image.alt = "no image";
  image.style.width = "75%";
  image.setAttribute("src", noImage);
  label_image.appendChild(image);
  let input_file = document.createElement("input");
  input_file.id = "file_input";
  input_file.type = "file";
  span_image.appendChild(label_image);
  span_image.appendChild(input_file);
  old.parentNode.replaceChild(span_image, old);

  // Make a textarea for description
  old = document.getElementById("description_object");
  let desc_textarea = document.createElement("textarea");
  desc_textarea.className = "form-control";
  desc_textarea.id = "description_object";
  desc_textarea.rows = "6";
  desc_textarea.value = description;
  old.parentNode.replaceChild(desc_textarea, old);

  // Make a textarea for description for the time slot
  old = document.getElementById("time_slot");
  let time_slot_textarea = document.createElement("textarea");
  time_slot_textarea.className = "form-control";
  time_slot_textarea.id = "time_slot";
  time_slot_textarea.rows = "3";
  time_slot_textarea.value = time_slot;
  old.parentNode.replaceChild(time_slot_textarea, old);

  // Replace the button "Modifier" by a "Confirmer" one
  old = document.getElementById("modifyObjectButton");
  let divButtons = document.createElement("div");
  divButtons.className = "row justify-content-md-center";
  divButtons.id = "divButtons";
  let divCol1 = document.createElement("div");
  divCol1.className = "col-2";
  let new_button = document.createElement("input");
  new_button.type = "button";
  new_button.className = "btn btn-primary";
  new_button.value = "Confirmer";
  new_button.id = "confirmObjectButton";
  new_button.addEventListener("click", updateObject);
  divCol1.appendChild(new_button);
  divButtons.appendChild(divCol1);

  let divCol2 = document.createElement("div");
  divCol2.className = "col-2";
  let cancelButton = document.createElement("input");
  cancelButton.type = "button";
  cancelButton.className = "btn btn-primary";
  cancelButton.value = "Annuler";
  cancelButton.id = "cancelObjectButton";
  cancelButton.addEventListener("click", changeToText);
  divCol2.appendChild(cancelButton);
  divButtons.appendChild(divCol2);
  old.parentNode.replaceChild(divButtons, old);

  form = !form;
}

/**
 * Send to the backend all informations to update an object
 * @param {Event} e : evenement
 */
function updateObject(e) {
  e.preventDefault();
  // Get all elements from the form
  // TODO : how to get the image ?
  let new_image = document.getElementById("file_input");
  let descriptionDOM = document.getElementById("description_object");
  let new_description = descriptionDOM.value.trim();
  let new_time_slotDOM = document.getElementById("time_slot")
  let new_time_slot = new_time_slotDOM.value.trim();

  // Test description, time slot and show to the user the errors if they exist
  let emptyParameters = 0;
  if (new_description.length === 0) {
    descriptionDOM.classList.add("border-danger");
    emptyParameters++;
  } else {
    if (descriptionDOM.classList.contains("border-danger")) {
      descriptionDOM.classList.remove("border-danger");
    }
  }
  if (new_time_slot.length === 0) {
    document.getElementById("time_slot").classList.add("border-danger");
    emptyParameters++;
  } else {
    if (new_time_slotDOM.classList.contains("border-danger")) {
      new_time_slotDOM.classList.remove("border-danger");
    }

  }
  // Check if there is an empty parameter
  if (emptyParameters > 0) {
    let notif = new Notification().getNotification();
    notif.fire({
      icon: 'error',
      title: 'Veuillez remplir les champs obligatoires !'
    })
    return;
  }
  // Call the function to update the offer
  offerLibrary.updateOffer(idOffert, new_time_slot, new_description, idType,
      english_status);

  // Attribute new values
  description = new_description
  time_slot = new_time_slot;

  // Put text back
  changeToText(e);
}

export default MyObjectPage;