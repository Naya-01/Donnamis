import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import noImage from "../../img/noImage.png";
import TypeLibrary from "../../Domain/TypeLibrary";

const typeLibrary = new TypeLibrary();
let form = false;

/**
 * Render the page to see his object
 */
const MyObjectPage = async () => {
  // If he's not log in he's redirect to the homepage
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  // GET all informations of the object
  //TODO fetch to get all information of the object

  // Construct all the HTML
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML =
      `<div class="container p-3">
      <div class="mx-5 my-5">
      <h3 class=pb-3>Votre objet</h3>
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
                      <h5><label for="description_object" class="form-label">Description</label></h5>
                      <p id="description_object">La description de l'objet</p>
                    </div>
                </div>
              </div>
              <div class="row p-2">
                <!-- the time slot-->
                <div class="col">
                    <div class="mb-3">
                      <h5><label for="time_slot" class="form-label">Plage horaire</label></h5>
                      <p id="time_slot">Ceci est la plage horaire</p> <!-- TODO : GET from the DB -->
                    </div>
                 </div>
              <!-- The type -->
                <div class="col">
                  <div class="mb-3">
                    <h5><label for="type">Type</label></h5>
                    <p id="type">Voiture</p> <!-- TODO : GET from the DB -->
                  </div>
                </div>
                <!-- The status -->
                <div class="col">
                  <div class="mb-3">
                    <h5><label for="status">Etat de l'objet</label></h5>
                    <p id="status">Disponible</p> <!-- TODO : GET from the DB -->
                  </div>
                </div>
              </div>
              <!-- The confirm button -->
              <div class="text-center">
              <input type="button"  class="btn btn-primary" id="modifyObjectButton" value="Modifier"> <!-- TODO : add link to the other page -->
              </div>
            
          </p>
        </div>
      </div>
    </div>
  </div>`;
  let button = document.getElementById("modifyObjectButton");
  button.addEventListener("click", changeToFormOrText);

}

/**
 * Send to the backend all informations to create an object
 * @param {Event} e : evenement
 */
async function changeToFormOrText(e) {
  e.preventDefault();
  // Convert to Form
  if (!form) {
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
    desc_textarea.value = old.textContent;
    old.parentNode.replaceChild(desc_textarea, old);

    // Make a textarea for description for the time slot
    old = document.getElementById("time_slot");
    let time_slot_textarea = document.createElement("textarea");
    time_slot_textarea.className = "form-control";
    time_slot_textarea.id = "time_slot";
    time_slot_textarea.rows = "3";
    time_slot_textarea.value = old.textContent;
    old.parentNode.replaceChild(time_slot_textarea, old);

    // Make a input text for the type
    let span = document.createElement("span");
    span.id = "span_type";
    old = document.getElementById("type");
    let type_input = document.createElement("input");
    type_input.type = "text";
    type_input.className = "form-control";
    type_input.id = "type";
    type_input.value = old.textContent;
    type_input.setAttribute("list", "all_types");
    let datalist_types = document.createElement("datalist");
    datalist_types.id = "all_types";

    // Get all types from the backend
    let allDefaultTypes = await typeLibrary.getAllDefaultTypes();
    // Create an HTML list of proposition for Types
    for (let i = 0; i < allDefaultTypes.type.length; i++) {
      let newType = document.createElement("option");
      newType.value = allDefaultTypes.type[i].typeName;
      datalist_types.appendChild(newType);
    }
    span.appendChild(type_input);
    span.appendChild(datalist_types);
    old.parentNode.replaceChild(span, old);

    // Replace the button "Modifier" by a "Confirmer" one
    old = document.getElementById("modifyObjectButton");
    let new_button = document.createElement("input");
    new_button.type = "button";
    new_button.className = "btn btn-primary";
    new_button.value = "Confirmer";
    new_button.id = "confirmObjectButton";
    new_button.addEventListener("click", updateObject);
    old.parentNode.replaceChild(new_button, old);
  }
  // Convert to Text
  else {
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
    desc_text.innerHTML = old.value;
    old.parentNode.replaceChild(desc_text, old);

    // Make a simple paragraph for time slot
    old = document.getElementById("time_slot");
    let time_slot_text = document.createElement("p");
    time_slot_text.id = "time_slot";
    time_slot_text.rows = "3";
    time_slot_text.innerHTML = old.value;
    old.parentNode.replaceChild(time_slot_text, old);

    // Make a simple paragraph for type
    let span = document.createElement("span");
    span.id = "span_type";
    old = document.getElementById("span_type");
    let type_text = document.createElement("p");
    type_text.id = "type";
    type_text.innerHTML = document.getElementById("type").value;
    span.appendChild(type_text);
    old.parentNode.replaceChild(span, old);

    // Replace the button "Confirmer" by a "Modifier" one
    old = document.getElementById("confirmObjectButton");
    let new_button = document.createElement("input");
    new_button.type = "button";
    new_button.className = "btn btn-primary";
    new_button.value = "Modifier";
    new_button.id = "modifyObjectButton";
    new_button.addEventListener("click", changeToFormOrText);
    old.parentNode.replaceChild(new_button, old);

  }
  form = !form;

}

/**
 * Send to the backend all informations to update an object
 * @param {Event} e : evenement
 */
function updateObject(e) {
  e.preventDefault();
  /*TODO : request to update the object*/
  changeToFormOrText(e);
}

export default MyObjectPage;