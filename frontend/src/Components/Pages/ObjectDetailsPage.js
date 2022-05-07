import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import noImage from "../../img/noImage.png";
import noImageProfile from "../../img/profil.png";
import OfferLibrary from "../../Domain/OfferLibrary";
import NotificationSA from "../Module/NotificationSA";
import MemberLibrary from "../../Domain/MemberLibrary";
import InterestLibrary from "../../Domain/InterestLibrary";
import ObjectLibrary from "../../Domain/ObjectLibrary";
import RatingLibrary from "../../Domain/RatingLibrary";
import Member from "../../Domain/Member";

const regNumberPhone =
    new RegExp('^[+]?[(]?[0-9]{3}[)]?[- .]?[0-9]{3}[- .]?[0-9]{4,6}$');
const Swal = require('sweetalert2');
const memberLibrary = new MemberLibrary();
const offerLibrary = new OfferLibrary();
const objectLibrary = new ObjectLibrary();
const interestLibrary = new InterestLibrary();
const ratingLibrary = new RatingLibrary();
const bottomNotification = new NotificationSA().getNotification();
const dictionnary = new Map([
  ['interested', 'Intéressé'],
  ['available', 'Publié'],
  ['assigned', 'Attribué'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé'],
  ['not_collected', 'Non récupéré']
]);

let idOffer;
let imageOfObject;
let english_status;
let idType;
let description;
let time_slot;
let form = false;
let localLinkImage;
let statusObject;
let note = 1;
let offer;
let idMemberConnected;
let telNumber;
let versionObject = 0;
let versionOffer = 0;
let versionMemberConnected;

/**
 * Render the page to see an object
 */
const ObjectDetailsPage = async () => {
  // If he's not log in he's redirect to the homepage
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }

  //GET the id of the offer by the url
  let url_string = window.location;
  let url = new URL(url_string);

  //Check the id of the offer in the url
  idOffer = url.searchParams.get("idOffer");
  if (!idOffer || idOffer <= 0) {
    Redirect("/");
    return;
  }

  // GET all informations of the object (and offer)
  offer = await offerLibrary.getOfferById(idOffer);
  if (offer === undefined) { // if we didn't found the offer
    Redirect("/");
    return;
  }
  //Set all fields
  if (offer.object.image) {
    imageOfObject = "/api/object/getPicture/" + offer.object.idObject;
  } else {
    imageOfObject = noImage;
  }
  description = offer.object.description;
  time_slot = offer.timeSlot;
  statusObject = offer.status;

  versionObject = offer.object.version;
  versionOffer = offer.version;

  let oldDate;
  if (offer.oldDate === undefined) {
    oldDate = "/"
  } else {
    oldDate = offer.oldDate[2] + "/" + offer.oldDate[1]
        + "/" + offer.oldDate[0];
  }
  // translate the status to french
  english_status = offer.status;
  let french_status = dictionnary.get(english_status);

  // Get the id of the member connected
  let member = await memberLibrary.getUserByHisToken();
  idMemberConnected = member.memberId;
  telNumber = member.phone;
  if (telNumber === undefined) {
    telNumber = "";
  }
  versionMemberConnected = member.version;

  // GET all interests
  let jsonInterests = await interestLibrary.getInterestedCount(
      offer.object.idObject);
  let nbMembersInterested = jsonInterests.count;
  let isInterested = jsonInterests.isUserInterested;

  // Construct all the HTML
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML =
      `<div class="container p-3">
      <div class="mx-5 my-5">
      <div class="row" id="problemMember"></div>
      <h2 id="titleObject" class=pb-3></h2>
      <div class="card">
        <!-- Body of the card -->
        <div class="card-body">
          <p class="card-text">
              <div class="row justify-content-start p-2">
                <!-- The image -->
                <div class="col-4">
                  <img id="image" alt="no image" width="75%" src="${imageOfObject}"/>
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
              <!-- the date -->
              <div class="row px-2">
                <p class="text-muted">Date de publication : ${offer.date[2]}/${offer.date[1]}/${offer.date[0]}</p>
              </div>
              <!-- the old date -->
              <div class="row px-2">
                <p class="text-muted">Date de la précédente offre : ${oldDate}</p>
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
                      <p id="time_slot">${offer.timeSlot}</p>
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
              <div class="row p-2 m-2">
                <!-- The modify button -->
                <div id="divB" class="col text-center">
                  <span id="divDate"></span>
                  <div id="divTel"></div>
                </div>
              </div>
              <div class="row p-2">
                <!-- number of interested people -->
                <div id="nbMembersInterested" class="text-center p-2">
                  <span id="InterestCount">${nbMembersInterested}</span><span> personne(s) intéressée(s) par 
                    cet objet</span>
                </div>
              </div>
              <div class="row p-2">
                <!-- The rating button -->
                <div id="ratingDiv" class="text-center p-2"></div>
              </div>
          </p>
        </div>
      </div>
      <!-- The comment with the rating-->
      <div id="displayRating"></div>
    </div>
  </div>`;

  // if this is the object of the member connected
  if (idMemberConnected === offer.object.idOfferor) {
    document.getElementById("titleObject").textContent = "Votre objet";
    //add the modifier bouton
    let divB = document.getElementById("divB");
    let new_button = document.createElement("input");
    new_button.type = "button";
    new_button.className = "btn btn-primary";
    new_button.value = "Modifier";
    new_button.id = "modifyObjectButton";
    new_button.addEventListener("click", changeToForm);
    divB.appendChild(new_button);
    if (english_status === "given") {
      new_button.remove();
      let current_rating = await ratingLibrary.getOne(offer.object.idObject);
      if (current_rating === undefined) {
        displayRating(null);
      } else {
        displayRating(current_rating);
      }
    }
  }
  // if this is not the object of the member connected
  else {
    // we get the member that gives the object
    let memberGiver = await memberLibrary.getUserByHisId(
        offer.object.idOfferor);
    //if the member is prevented, a msg is display
    if (memberGiver.status === "prevented") {
      document.getElementById("problemMember").innerHTML = `
        <p class="text-danger">
          <i class="bi bi-exclamation-triangle"></i>
          L'offreur est actuellement empêché de participer à la donnerie.
          <i class="bi bi-exclamation-triangle"></i>
        </p>
      `
    }
    // change buttons
    document.getElementById("titleObject").textContent = "L'objet de "
        + memberGiver.username;

    if (!isInterested && (english_status === "interested" || english_status
        === "available")) {
      displayAddInterest(offer.object.version, offer.version);
    } else if (english_status === "given") {
      let current_rating = await ratingLibrary.getOne(offer.object.idObject);
      if (current_rating === undefined) { // if there is no rating yet
        let current_interest = await interestLibrary.getOneInterest(
            offer.object.idObject);
        if (current_interest !== undefined && current_interest.status
            === "received") { // if the member connected has received the object
          let rating_button = document.createElement("input");
          rating_button.id = "buttonGivenRating";
          rating_button.value = "Donner une note";
          rating_button.type = "button";
          rating_button.className = "btn btn-primary";
          document.getElementById("ratingDiv").appendChild(rating_button);
          rating_button.addEventListener("click", ratingPopUp);
        } else { // if there is no rating and the member connected is not the receiver
          displayRating(null);
        }
      } else { // if there is a rating
        displayRating(current_rating);
      }
    }
  }
}

/**
 * Display HTML elements to add an interest
 */
function displayAddInterest(versionObject, versionOffer) {
  // date of disponibility
  let labelDate = document.createElement("label");
  labelDate.for = "input_date";
  labelDate.innerHTML = "Date de disponibilité : ";
  let input_date = document.createElement("input");
  input_date.id = "input_date";
  input_date.type = "date";
  let date = new Date();
  // Put a zero in front of month if it is a number < 10
  let month = "";
  if (date.getMonth() % 10 !== 0) {
    month = "0"
  }
  month += (date.getMonth() + 1);
  // Put a zero in front of day if it is a number < 10
  let day = "";
  if (date.getDate() % 10 !== 0) {
    day = "0"
  }
  day += date.getDate();
  let dateActual = date.getFullYear() + "-" + month + "-" + day;
  input_date.value = dateActual;
  input_date.min = dateActual;
  document.getElementById("divDate").appendChild(labelDate);
  document.getElementById("divDate").appendChild(input_date);

  // tel number
  let checkboxTel = document.createElement("input");
  checkboxTel.type = "checkbox";
  checkboxTel.id = "callMe";
  checkboxTel.name = "callMe";

  let labelTel = document.createElement("label");
  labelTel.htmlFor = "callMe";
  labelTel.innerHTML = "Je souhaite être appelé via ce numéro : ";

  let numTelInput = document.createElement("input");
  numTelInput.type = "text";
  numTelInput.size = "20";
  numTelInput.id = "numTelInput";
  numTelInput.value = telNumber;

  let divTel = document.getElementById("divTel");
  divTel.appendChild(checkboxTel);
  divTel.appendChild(labelTel);
  divTel.appendChild(numTelInput);

  // button im interested
  let new_button = document.createElement("input");
  new_button.id = "interestedButton";
  new_button.value = "Je suis interessé";
  new_button.type = "button";
  new_button.className = "btn btn-primary";
  new_button.addEventListener("click", () => {
    addOneInterest(versionObject, versionOffer);
  });
  document.getElementById("divB").appendChild(new_button);
}

/**
 * Add an interest
 * @param versionObject
 * @param versionOffer
 */
async function addOneInterest(versionObject, versionOffer) {
  let input_date = document.getElementById("input_date");
  let new_button = document.getElementById("interestedButton");
  //if there is no date specified
  if (input_date.value.length === 0) {
    bottomNotification.fire({
      icon: 'error',
      title: 'Aucune date renseignée'
    })
    return;
  }
  let callMeCheckbox = document.getElementById("callMe");
  let numTelInput = document.getElementById("numTelInput");
  let notificationCall = false;
  if (callMeCheckbox.checked) {
    notificationCall = true;
    let numTel = numTelInput.value;
    if (numTel.trim().length === 0) {
      numTelInput.classList.add("border-danger");
      bottomNotification.fire({
        icon: 'error',
        title: 'Si vous souhaitez être appelé, entrez un numéro de téléphone.'
      })
      return;
    } else if (!regNumberPhone.test(numTel.trim())) {
      numTelInput.classList.add("border-danger");
      bottomNotification.fire({
        icon: 'error',
        title: 'Le numéro de téléphone entré est incorrect.'
      })
      return;
    } else if (numTel !== telNumber) { // the num is good and has changed
      //update the tel number of the member
      let memberToUpdate = new Member(null, null, null,
          null, numTel, null, versionMemberConnected, null,
          null, null, idMemberConnected);
      await memberLibrary.updateMember(memberToUpdate);
      versionMemberConnected += 1;
    }
  }
  numTelInput.classList.remove("border-danger");
  numTelInput.disabled = true;
  new_button.disabled = true;
  input_date.disabled = true;
  callMeCheckbox.disabled = true;
  let newInterest = await interestLibrary.addOne(offer.object.idObject,
      input_date.value, notificationCall, versionObject, versionOffer);
  // the notification to show that the interest is send
  if (newInterest !== undefined) {
    bottomNotification.fire({
      icon: 'success',
      title: 'Votre intérêt a bien été pris en compte.'
    })
    // increment the number of people interested
    let countInterest = document.getElementById("InterestCount");
    countInterest.innerHTML = parseInt(++countInterest.innerHTML);
  }
}

/**
 * Display the rating with its comment
 * @param current_rating the rating to display
 */
function displayRating(current_rating) {
  let ratingDiv = document.getElementById("ratingDiv");
  // if the object doesn't have a rating yet
  if (current_rating == null || current_rating.rating == null
      || current_rating.comment == null) {
    let pNoRating = document.createElement("p");
    pNoRating.innerHTML = "L'objet n'a pas été noté pour le moment.";
    pNoRating.id = "PnoRating";
    pNoRating.className = "text-secondary";
    ratingDiv.appendChild(pNoRating);
  }
  // if the object already has a rating
  else {
    let displayRatingDiv = document.getElementById("displayRating");
    let profilPicture;
    if (current_rating.memberRater.image === undefined) {
      profilPicture = noImageProfile;
    } else {
      profilPicture = "/api/member/getPicture/" + current_rating.idMember;
    }
    displayRatingDiv.innerHTML += `
    <div class="card bg-light my-3">
      <div class="card-body">
        
        <p class="card-text">
          <div class="row">
            <div class="col-4 mx-auto">
              <!-- Profil picture of the rater -->
              <img id="image" alt="no image" width="75%" src="${profilPicture}"/>
              <!-- Show stars that represents the rating -->
              <p>${create5StarsHTMLCode(current_rating.rating)}</p>
            </div>
            <div class="col-8">
            <h3 class="card-title mb-3">La note de ${current_rating.memberRater.username} pour cet objet</h3>
              <!-- the comment of the rating -->
              <h5>Commentaire :</h5>
              <p>${current_rating.comment}</p>
            </div>
          </div>
        </p>
      </div>
    </div>`;
  }
}

/**
 * Change elements of the html to have a text.
 * @param {Event} e : event
 */
function changeToText(e) {
  e.preventDefault();
  // Make a simple image
  let old = document.getElementById("span_image");
  let image = document.createElement("img");
  image.id = "image";
  image.alt = "no image";
  image.style.width = "75%";
  image.setAttribute("src", imageOfObject);
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
 * @param {Event} e : event
 */
function changeToForm(e) {
  e.preventDefault();
  // Make the image clickable to import a file
  let old = document.getElementById("image");
  let span_image = document.createElement("span");
  span_image.id = "span_image";
  span_image.className = "img_file_input";
  let label_image = document.createElement("label");
  label_image.setAttribute("for", "file_input");

  // the image
  let image = document.createElement("img");
  image.alt = "no image";
  image.className = "clickable";
  image.style.width = "75%";
  image.setAttribute("src", imageOfObject);
  label_image.appendChild(image);

  // the input to set an image
  let input_file = document.createElement("input");
  input_file.id = "file_input";
  input_file.type = "file";
  input_file.name = "file";
  input_file.accept = ".jpg, .jpeg, .png";

  // if the image is changed by the user
  input_file.onchange = () => {
    const [file] = input_file.files
    if (file) {
      let link = URL.createObjectURL(file);
      image.src = link;
      localLinkImage = link;
    }
  }
  span_image.appendChild(label_image);
  span_image.appendChild(input_file);
  old.parentNode.replaceChild(span_image, old);

  // textarea for description
  old = document.getElementById("description_object");
  let desc_textarea = document.createElement("textarea");
  desc_textarea.className = "form-control";
  desc_textarea.id = "description_object";
  desc_textarea.rows = "6";
  desc_textarea.value = description;
  old.parentNode.replaceChild(desc_textarea, old);

  // textarea for description for the time slot
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

  // Add "Annuler" button
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
 * @param {Event} e : event
 */
async function updateObject(e) {
  e.preventDefault();
  // Get all elements from the form
  let descriptionDOM = document.getElementById("description_object");
  let new_description = descriptionDOM.value.trim();
  let new_time_slotDOM = document.getElementById("time_slot")
  let new_time_slot = new_time_slotDOM.value.trim();

  // check the description
  let emptyFields = 0;
  if (new_description.length === 0) {
    descriptionDOM.classList.add("border-danger");
    emptyFields++;
  } else {
    descriptionDOM.classList.remove("border-danger");
  }

  // check the time slot
  if (new_time_slot.length === 0) {
    document.getElementById("time_slot").classList.add("border-danger");
    emptyFields++;
  } else {
    new_time_slotDOM.classList.remove("border-danger");
  }

  // Check if there is an empty field
  if (emptyFields > 0) {
    bottomNotification.fire({
      icon: 'error',
      title: 'Veuillez remplir les champs obligatoires !'
    })
    return;
  }

  // Update the image
  let fileInput = document.querySelector('input[name=file]');
  let objectWithImage;

  //if the image has been modified
  if (fileInput.files[0] !== undefined) {
    let types = ["image/jpeg", "image/jpg", "image/png"];
    let canBeUpload = false;
    for (const type in types) {
      if (fileInput.files[0].type === types[type]) {
        canBeUpload = true;
      }
    }

    if (!canBeUpload) {
      bottomNotification.fire({
        icon: 'error',
        title: "Nous n'acceptons que des images png, jpeg et jpg."
      })
      return;
    } else {
      let formData = new FormData();
      formData.append('file', fileInput.files[0]);
      objectWithImage = await objectLibrary.setImage(formData,
          offer.object.idObject, versionObject);
      versionObject = objectWithImage.version;
    }
  }

  // Call the function to update the offer
  let newOffer = await offerLibrary.updateOffer(idOffer, new_time_slot,
      new_description, idType, english_status, statusObject, versionObject,
      versionOffer);
  if (newOffer !== undefined) {
    versionOffer = versionOffer + 1;
    versionObject = versionObject + 1;
    bottomNotification.fire({
      icon: 'success',
      title: 'Votre objet a bien été mis à jour.'
    })
  }
  // Attribute new values
  description = new_description
  time_slot = new_time_slot;

  if (objectWithImage !== undefined) { // if there is an image
    if (localLinkImage !== undefined) {
      imageOfObject = localLinkImage;
    }
  }
  // Put text back
  changeToText(e);

}

/**
 * Display a popup to add a rating
 * @param {Event} e : event
 */
async function ratingPopUp(e) {
  e.preventDefault();
  Swal.fire({
    title: 'Donnez une note à cet objet :',
    html: createRatingHTMLCode(),
    width: 1000,
    padding: '2em',
    scrollbarPadding: false,
    backdrop: `rgba(80, 80, 80, 0.7)`,
    allowOutsideClick: true,
    allowEscapeKey: true,
    confirmButtonText: 'Publier la note',
    preConfirm: async () => {
      let text_rating = document.getElementById("rating_text").value;
      if (text_rating.trim().length === 0) {
        note = 1;
        bottomNotification.fire({
          icon: 'error',
          title: 'Vous devez commentez votre note.'
        })
        return;
      }
      let rating = await ratingLibrary.addRating(note, text_rating,
          offer.object.idObject);
      if (rating !== undefined) {
        bottomNotification.fire({
          icon: 'success',
          title: 'Votre note a bien été prise en compte.'
        })
        document.getElementById("buttonGivenRating").remove(); // remove the button
        displayRating(rating); // display the new rating
        let pNoRating = document.getElementById("PnoRating");
        if (pNoRating !== null) {
          pNoRating.remove();
        }
      }
    }
  })
  let allStars = document.getElementsByClassName("bi bi-star-fill clickable");
  for (let i = 0; i < allStars.length; i++) {
    allStars[i].addEventListener("click", changeColorStars);
  }
}

/**
 * Change the color of the stars in function of the note
 * @param {Event} e : event
 */
function changeColorStars(e) {
  e.preventDefault();
  let note_clicked = e.target.id.substring(4);
  let allStars = document.getElementsByClassName("bi bi-star-fill clickable");
  for (let i = 0; i < allStars.length; i++) {
    allStars[i].style = "color:gray";
  }
  for (let i = 0; i < note_clicked; i++) {
    allStars[i].style = "color:yellow";
  }
  note = note_clicked;
}

/**
 * Generate html code of 5 stars
 * @param nbYellow the number of yellow stars needed
 * @returns {string} the html code of the 5 stars
 */
function create5StarsHTMLCode(nbYellow) {
  let htmlCode = ``;
  // Add 5 stars for the rating
  for (let i = 1; i <= 5; i++) {
    let oneStar = document.createElement("i");
    oneStar.className = "bi bi-star-fill clickable";
    oneStar.id = "star" + i;
    if (i <= nbYellow) {
      oneStar.style = "color:yellow";
    }
    htmlCode += oneStar.outerHTML;
  }
  return htmlCode;
}

/**
 * Generate html code to add a rating
 * @returns {string} the html code to add a rating
 */
function createRatingHTMLCode() {
  let htmlCode = create5StarsHTMLCode(1);
  htmlCode += `<div class=row">
                <textarea class="form-control" id="rating_text" 
                placeholder="Commentez votre note" rows="2"></textarea>
               </div>`
  return htmlCode;
}

export default ObjectDetailsPage;