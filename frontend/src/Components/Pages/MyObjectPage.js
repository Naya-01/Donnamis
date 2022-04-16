import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import noImage from "../../img/noImage.png";
import OfferLibrary from "../../Domain/OfferLibrary";
import Notification from "../Module/Notification";
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
const bottomNotification = new Notification().getNotification();
const dictionnary = new Map([
  ['interested', 'Disponible'],
  ['available', 'Disponible'],
  ['assigned', 'En cours de donnation'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé']
]);
let idOffer;
let idObject;
let imageOfObject;
let english_status;
let idType;
let description;
let time_slot;
let form = false;
let isInterested;
let localLinkImage;
let statusObject;
let note = 1;
let offer;
let idMemberConnected;


/**
 * Render the page to see an object
 */
const MyObjectPage = async () => {
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
  idObject = offer.object.idObject;
  if (offer.object.image) {
    imageOfObject = "/api/object/getPicture/" + idObject;
  }
  else{
    imageOfObject = noImage;
  }
  idType = offer.object.type.idType;
  description = offer.object.description;
  time_slot = offer.timeSlot;
  statusObject = offer.status;
  let oldDate;
  if(offer.oldDate === undefined){
    oldDate = "/"
  }
  else {
    oldDate = offer.oldDate[2] + "/" + offer.oldDate[1]
        + "/" + offer.oldDate[0];
  }
  // translate the status to french
  english_status = offer.status;
  let french_status = dictionnary.get(english_status);

  // Get the id of the member connected
  let member = await memberLibrary.getUserByHisToken();
  idMemberConnected = member.memberId;

  // GET all interests
  let jsonInterests = await interestLibrary.getInterestedCount(
      offer.object.idObject);
  isInterested = jsonInterests.isUserInterested;
  let nbMembersInterested = jsonInterests.count;

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
                  <p>${nbMembersInterested} personne(s) intéressée(s) par 
                    cet objet</p>
                </div>
              </div>
              <div class="row p-2">
                <!-- The rating button or rating shown -->
                <div id="ratingDiv" class="text-center p-2"></div>
              </div>
          </p>
        </div>
      </div>
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
    }
  }
  // if this is not the object of the member connected
  else {
    // we get the member that gives the object
    let memberGiver = await memberLibrary.getUserByHisId(
        offer.object.idOfferor);
    // change buttons
    document.getElementById("titleObject").textContent = "L'objet de "
        + memberGiver.username;
    if (!isInterested && (english_status === "interested" || english_status
        === "available")) {
      displayAddInterest();
    }
    else if(english_status === "given"){ //TODO : make minus request to the db here
      let current_rating = await ratingLibrary.getOne(idObject);
      if(current_rating === undefined){ // if there is no rating yet
        let current_interest = await interestLibrary.getOneInterest(idObject, idMemberConnected);
        if(current_interest !== undefined && current_interest.status === "received"){ // if the member connected has received the object
          let rating_button = document.createElement("input");
          rating_button.id = "buttonGivenRating";
          rating_button.value = "Donner une note";
          rating_button.type = "button";
          rating_button.className = "btn btn-primary";
          document.getElementById("ratingDiv").appendChild(rating_button);
          rating_button.addEventListener("click", ratingPopUp);
        }
        else{ // if there is no rating and the member connected is not the receiver
          displayRating(null, null);
        }
      }
      else{ // if there is a rating
        displayRating(current_rating.rating, current_rating.comment);
      }
    }
  }
}


function displayAddInterest(){
  // date of disponibility
  let labelDate = document.createElement("label");
  labelDate.for = "input_date";
  labelDate.innerHTML = "Date de disponibilité : ";
  let input_date = document.createElement("input");
  input_date.id = "input_date";
  input_date.type = "date";
  let date = new Date();
  let month = "";
  if (date.getMonth() % 10 !== 0) {
    month = "0"
  }
  month += (date.getMonth() + 1);
  let dateActual = date.getFullYear() + "-" + month + "-" + date.getDate();
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
  new_button.addEventListener("click", addOneInterest);
  document.getElementById("divB").appendChild(new_button);
}

async function addOneInterest(){
  let input_date = document.getElementById("input_date");
  let new_button = document.getElementById("interestedButton");
  //if there is no date specified
  if(input_date.value.length === 0){
    bottomNotification.fire({
      icon: 'error',
      title: 'Aucune date renseignée'
    })
    return;
  }
  let callMeCheckbox = document.getElementById("callMe");
  let numTelInput = document.getElementById("numTelInput");
  if(callMeCheckbox.checked){
    let numTel = numTelInput.value;
    if(numTel.trim().length === 0){
      numTelInput.classList.add("border-danger");
      bottomNotification.fire({
        icon: 'error',
        title: 'Si vous souhaitez être appelé, entrez un numéro de téléphone.'
      })
      return;
    }
    else if(!regNumberPhone.test(numTel.trim())){ //TODO : replace with check num with regex
      numTelInput.classList.add("border-danger");
      bottomNotification.fire({
        icon: 'error',
        title: 'Le numéro de téléphone entré est incorrect.'
      })
      return;
    }
    else{ // the num is good
      //update the tel number of the member
      let member = new Member(null, null, null,
          null, numTel, null, idMemberConnected);
      await memberLibrary.updateMember(member);
    }
  }
  numTelInput.classList.remove("border-danger");
  numTelInput.disabled = true;
  new_button.disabled = true;
  input_date.disabled = true;
  callMeCheckbox.disabled = true;
  await interestLibrary.addOne(offer.object.idObject, input_date.value)

  // the notification to show that the interest is send
  bottomNotification.fire({
    icon: 'success',
    title: 'Votre intérêt a bien été pris en compte.'
  })
}

/**
 * Display the rating with its comment
 * @param rating the rating to display
 * @param comment the comment to display
 */
function displayRating(rating, comment){
  let ratingDiv = document.getElementById("ratingDiv");
  if(rating == null || comment == null){
    let pNoRating = document.createElement("p");
    pNoRating.innerHTML = "L'objet n'a pas été noté pour le moment.";
    pNoRating.className = "text-secondary";
    ratingDiv.appendChild(pNoRating);
  }
  else{ //TODO : make a better display
    ratingDiv.innerHTML += create5StarsHTMLCode(rating);
    ratingDiv.innerHTML += `<p>${comment}</p>`;
  }
}

/**
 * Change elements of the html to have a text.
 * @param {Event} e : evenement
 */
function changeToText(e) {
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
 * @param {Event} e : evenement
 */
function changeToForm(e) {
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
  input_file.accept = "image/*";

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
 * @param {Event} e : evenement
 */
async function updateObject(e) {
  // Get all elements from the form
  let descriptionDOM = document.getElementById("description_object");
  let new_description = descriptionDOM.value.trim();
  let new_time_slotDOM = document.getElementById("time_slot")
  let new_time_slot = new_time_slotDOM.value.trim();

  // check the description
  let emptyParameters = 0;
  if (new_description.length === 0) {
    descriptionDOM.classList.add("border-danger");
    emptyParameters++;
  }
  else {
    if (descriptionDOM.classList.contains("border-danger")) {
      descriptionDOM.classList.remove("border-danger");
    }
  }

  // check the time slot
  if (new_time_slot.length === 0) {
    document.getElementById("time_slot").classList.add("border-danger");
    emptyParameters++;
  }
  else {
    if (new_time_slotDOM.classList.contains("border-danger")) {
      new_time_slotDOM.classList.remove("border-danger");
    }
  }

  // Check if there is an empty parameter
  if (emptyParameters > 0) {
    bottomNotification.fire({
      icon: 'error',
      title: 'Veuillez remplir les champs obligatoires !'
    })
    return;
  }

  // Update the image
  let fileInput = document.querySelector('input[name=file]');
  let objectWithImage;
  if (fileInput.files[0] !== undefined) { // if there is an image
    let formData = new FormData();
    formData.append('file', fileInput.files[0]);
    objectWithImage = await objectLibrary.setImage(formData, idObject);
    if(objectWithImage === undefined){
      bottomNotification.fire({
        icon: 'error',
        title: "L'image entrée n'est pas du bon format."
      })
      return;
    }
  }

  // Call the function to update the offer
  await offerLibrary.updateOffer(idOffer, new_time_slot, new_description,
      idType,
      english_status, statusObject);

  // Attribute new values
  description = new_description
  time_slot = new_time_slot;
  bottomNotification.fire({
    icon: 'success',
    title: 'Votre objet a bien été mis à jour.'
  })

  if (objectWithImage !== undefined) { // if there is an image
    if(localLinkImage !== undefined) {
      imageOfObject = localLinkImage;
    }
  }
  // Put text back
  changeToText(e);


}

/**
 * Display a popup to add a rating
 * @param e event
 * @returns {Promise<void>}
 */
async function ratingPopUp(e){
  Swal.fire({
    title: 'Donnez une note à cet objet :',
    html: createRatingHTMLCode(),
    width: 1000,
    padding: '2em',
    scrollbarPadding: false,
    backdrop: `rgba(80,80,80,0.7)`,
    allowOutsideClick: true,
    allowEscapeKey: true,
    confirmButtonText: 'Publier la note',
    preConfirm: async () => {
      let text_rating = document.getElementById("rating_text").value;
      if(text_rating.trim().length === 0){
        note = 1;
        bottomNotification.fire({
          icon: 'error',
          title: 'Vous devez commentez votre note.'
        })
        return;
      }
      let rating = await ratingLibrary.addRating(note, text_rating, idObject);
      if(rating === undefined){
        bottomNotification.fire({
          icon: 'error',
          title: 'Un problème est survenu lors de la création de la note.'
        })
      }
      else{
        bottomNotification.fire({
          icon: 'success',
          title: 'Votre note a bien été prise en compte.'
        })
        document.getElementById("buttonGivenRating").remove(); // remove the button
        displayRating(rating.rating, rating.comment); // display the new rating
      }
    }
  })
  let allStars = document.getElementsByClassName("bi bi-star-fill clickable");
  for(let i = 0; i < allStars.length; i++){
    allStars[i].addEventListener("click", changeColorStars);
  }
}

/**
 * Change the color of the stars in function of the note
 * @param e event
 */
function changeColorStars(e){
  let note_clicked = e.target.id.substring(4);
  let allStars = document.getElementsByClassName("bi bi-star-fill clickable");
  for(let i = 0; i < allStars.length; i++){
    allStars[i].style = "color:gray";
  }
  for(let i = 0; i < note_clicked; i++){
    allStars[i].style = "color:yellow";
  }
  note = note_clicked;
}

/**
 * Generate html code of 5 stars
 * @param nbYellow the number of yellow stars needed
 * @returns {string} the html code of the 5 stars
 */
function create5StarsHTMLCode(nbYellow){
  let htmlCode = ``;
  // Add 5 stars for the rating
  for(let i = 1; i <= 5; i++){
    let oneStar = document.createElement("i");
    oneStar.className = "bi bi-star-fill clickable";
    oneStar.id = "star" + i;
    if(i <= nbYellow){
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
function createRatingHTMLCode(){
  let htmlCode = create5StarsHTMLCode(1);
  htmlCode += `<div class=row">
                <textarea class="form-control" id="rating_text" 
                placeholder="Commentez votre note" rows="2"></textarea>
               </div>`
  return htmlCode;
}

export default MyObjectPage;