import {Redirect} from "../Router/Router";
import {getSessionObject} from "../../utils/session";
import noImage from "../../img/noImage.png";

/**
 * Render the page to add a new object
 */
const AddNewObjectPage = async () => {
  // If he's not log in he's redirect to the homepage
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  // Get all types from the backend
  let allDefaultTypes;
  try {
    let options = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject("user").accessToken,
      },
    };
    allDefaultTypes = await fetch("api/type/allDefault", options);
  } catch (err) {
    console.log(err);
  }
  if (allDefaultTypes.status === 200) {
    allDefaultTypes = await allDefaultTypes.json();
  }
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
  let type = document.getElementById("type_object").value;
  let date = document.getElementById("availability_date").value;
  console.log(description);
  console.log(type);
  console.log(date);
  //TODO : get the image if it exists
  //TODO call the backend
  try {
    let response;
    let options = {
      method: "POST",
      body: JSON.stringify({
        "description": description,
        "type": type,
        "time_slot": date,
      }),
      headers: {
        "Content-Type": "application/json",
      },
    };
    //TODO: put fetch here in response
    response = true;
    if (!response) { //!response.ok
      //TODO : add SweetAlert2 to say it is insert
      Redirect("/");
    }
  } catch (err) {
    console.log(err);
  }
}

export default AddNewObjectPage;