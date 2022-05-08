import {Redirect} from "../Router/Router";
import {getSessionObject} from "../../utils/session";
import noImage from "../../img/noImage.png";
import TypeLibrary from "../../Domain/TypeLibrary";
import MemberLibrary from "../../Domain/MemberLibrary";
import NotificationSA from "../Module/NotificationSA";
import ObjectLibrary from "../../Domain/ObjectLibrary";
import OfferLibrary from "../../Domain/OfferLibrary";

const typeLibrary = new TypeLibrary();
const memberLibrary = new MemberLibrary();
const objectLibrary = new ObjectLibrary();
const bottomNotification = new NotificationSA().getNotification();
const offerLibrary = new OfferLibrary();
let idOfferor;

/**
 * Render the page to add a new object
 */
const AddNewObjectPage = async () => {
  // If he's not log in he's redirect to the homepage
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  // Get the id of the member connected
  let member = await memberLibrary.getUserByHisToken();
  idOfferor = member.memberId;

  // Get all types
  let allDefaultTypes = await typeLibrary.getAllDefaultTypes();

  // Create an HTML list of options for Types
  let allDefaultTypesHtml = "";
  for (let i = 0; i < allDefaultTypes.type.length; i++) {
    allDefaultTypesHtml += `<option value=\"`;
    allDefaultTypesHtml += allDefaultTypes.type[i].typeName;
    allDefaultTypesHtml += `\">`;
  }

  // Construct all the HTML
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <div class="container p-3">
      <div class="mx-5 my-5">
        <h2 class=pb-3>Ajouter un objet</h2>
        <div class="card">  
          <!-- Body of the card -->
          <div class="card-body">
            <div class="card-text">
              <form class="form_add">
                <div class="row justify-content-start p-2">
                  <!-- The image -->
                  <div class="col-4">
                    <div class="img_file_input">
                      <label for="file_input">
                        <img id="img" alt="no image" class="clickable" width="75%" src="${noImage}"/>
                      </label>
                      <input id="file_input" name="file" type="file" accept = ".jpg, .jpeg, .png"/>
                    </div>
                  </div>
                  <!-- The description -->
                  <div class="col-8">
                    <div class="form_add">
                      <div class="mb-3">
                        <h5><label for="description_object" class="form-label">Description</label></h5>
                        <textarea class="form-control" id="description_object" rows="6"></textarea>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- The type -->
                <div class="row p-2">
                  <div class="form_add">
                    <div class="mb-3">
                      <h5><label for="type_object" class="form-label">Type</label></h5>
                      <input type="text" class="form-control" id="type_object" list="all_types">
                      <datalist id="all_types">`
      // Put the list of default types
      + allDefaultTypesHtml +
      `             </datalist>
                    </div>
                  </div>
                </div>
                <!-- the time slot-->
                <div class="row p-2"> 
                  <div class="form_add">
                    <div class="mb-3">
                      <h5><label for="availability_date" class="form-label">Plage horaire</label></h5>
                      <textarea class="form-control" id="availability_date" rows="3"></textarea>
                    </div>
                  </div>
                </div>
                <!-- The confirm button -->
                <div class="text-center"> 
                  <button type="submit" class="btn btn-primary" id="addObjectButton">Confirmer</button>
                </div>
              </form>
            </div>
            
          </div>
        </div>
      </div>
    </div>`;

  document.querySelector("#addObjectButton").addEventListener("click",
      addObject);
  let input_file = document.getElementById("file_input");
  // display the image if the user upload a file
  input_file.onchange = () => {
    const [file] = input_file.files
    if (file) {
      document.getElementById("img").src = URL.createObjectURL(file);
    }
  }
};

/**
 * Send to the backend all informations to create an object
 * @param {Event} e : event
 */
async function addObject(e) {
  e.preventDefault();
  let descriptionHTML = document.getElementById("description_object");
  let typeNameHTML = document.getElementById("type_object");
  let timeSlotHTML = document.getElementById("availability_date");
  let description = descriptionHTML.value;
  let typeName = typeNameHTML.value;
  let timeSlot = timeSlotHTML.value;

  let emptyFields = 0;
  // check the description
  if (description.trim().length === 0) {
    descriptionHTML.classList.add("border-danger");
    emptyFields++;
  } else {
    descriptionHTML.classList.remove("border-danger");
  }
  // check the type name
  if (typeName.trim().length === 0) {
    typeNameHTML.classList.add("border-danger");
    emptyFields++;
  } else {
    typeNameHTML.classList.remove("border-danger");
  }
  // check the time slot
  if (timeSlot.trim().length === 0) {
    timeSlotHTML.classList.add("border-danger");
    emptyFields++;
  } else {
    timeSlotHTML.classList.remove("border-danger");
  }
  // if there is an empty field
  if (emptyFields > 0) {
    bottomNotification.fire({
      icon: 'error',
      title: 'Vous devez remplir les champs obligatoires'
    })
    return;
  }

  let canBeUpload = false;
  let fileInput = document.getElementById("file_input");
  // check the format of the image if it exists
  if (fileInput.files[0] !== undefined) {
    let types = ["image/jpeg", "image/jpg", "image/png"]
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
    }
  }
  // Call the backend to add the offer
  let newOffer = await offerLibrary.addFirstOffer(timeSlot, description,
      typeName);
  let idObject = newOffer.object.idObject;
  // if there is an image
  if(canBeUpload){
      let formData = new FormData();
      formData.append('file', fileInput.files[0]);
      await objectLibrary.setImage(formData, idObject);
  }
  Redirect("/");
  let notif = new NotificationSA().getNotification("top-end");
  notif.fire({
    icon: 'success',
    title: 'Votre objet a bien été publié !'
  })

}

export default AddNewObjectPage;