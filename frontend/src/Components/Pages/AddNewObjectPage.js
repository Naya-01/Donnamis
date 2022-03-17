import {Redirect} from "../Router/Router";
import {getSessionObject} from "../../utils/session";
import noImage from "../../img/noImage.png";
import TypeLibrary from "../../Domain/TypeLibrary";
import MemberLibrary from "../../Domain/MemberLibrary";
import OfferLibrary from "../../Domain/OfferLibrary";
import Notification from "../Module/Notification";

const typeLibrary = new TypeLibrary();
const memberLibrary = new MemberLibrary();
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
  // Get the id of the member
  let member = await memberLibrary.getUserByHisToken();
  idOfferor = member.user.memberId;

  // Get all types from the backend
  let allDefaultTypes = await typeLibrary.getAllDefaultTypes();

  // Create an HTML list of proposition for Types
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
        <div class="card">  
          <!-- Body of the card -->
          <div class="card-body">
            <p class="card-text">
            <form class="form_add">
              <div class="row justify-content-start">
                <!--TODO : make the image changeable-->
                <!-- The image -->
                <div class="col-4">
                  <div class="img_file_input">
                    <label for="file_input">
                      <img alt="no image"  height="75%" width="75%" src="${noImage}"/>
                    </label>
                    <input id="file_input" type="file"/>
                  </div>
                </div>
                <!-- The description -->
                <div class="col-8">
                  <div class="form_add">
                    <div class="mb-3">
                      <label for="description_object" class="form-label">Description</label>
                      <textarea class="form-control" id="description_object" rows="6"></textarea>
                    </div>
                  </div>
                </div>
              </div>
              <!-- The type -->
              <div class="row">
                <div class="form_add">
                  <div class="mb-3">
                    <label for="type_object" class="form-label">Type</label>
                    <input type="text" class="form-control" id="type_object" list="all_types">
                    <datalist id="all_types">`
      // Put the list of default types
      + allDefaultTypesHtml +
      `             </datalist>
                  </div>
                </div>
              </div>
              <!-- the time slot-->
              <div class="row"> 
                <div class="form_add">
                  <div class="mb-3">
                    <label for="availability_date" class="form-label">Plage horaire</label>
                    <textarea class="form-control" id="availability_date" rows="3"></textarea>
                  </div>
                </div>
              </div>
              <!-- The confirm button -->
              <button type="submit" class="btn btn-primary" id="addObjectButton">Confirmer</button>
            </form>
            </p>
            
          </div>
        </div>
      </div>
    </div>`;
  document.querySelector("#addObjectButton")
  .addEventListener("click", addObject);
};

/**
 * Send to the backend all informations to create an object
 * @param {Event} e : evenement
 */
async function addObject(e) {
  e.preventDefault();
  console.log("add object" + e.target);
  let description = document.getElementById("description_object").value;
  let typeName = document.getElementById("type_object").value;
  let timeSlot = document.getElementById("availability_date").value;
  console.log(description);
  console.log(timeSlot);
  //TODO : get the image if it exists
  //TODO call the backend
  offerLibrary.addOffer(timeSlot, description, typeName, idOfferor); //TODO : get the id type
  Redirect("/");
  let notif = new Notification().getNotification("top-end");
  notif.fire({
    icon: 'success',
    title: 'Votre objet a bien été publié !'
  })

}

export default AddNewObjectPage;