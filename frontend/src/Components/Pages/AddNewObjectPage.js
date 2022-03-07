/**
 * Render the page to add a new object
 */

 import profilImage from "../../img/profil.png";

 const AddNewObjectPage = () => { 
    const pageDiv = document.querySelector("#page");
    pageDiv.innerHTML = `<div class="card" style="width: 18rem;">
                            <img class="card-img-top" alt="profil" src="${profilImage}">
                            <div class="card-body">
                            <h5 class="card-title">Card title</h5>
                            <p class="card-text">
                                <form class="form_add">
                                    <div class="form_add">
                                        <label for="name">Description </label>
                                        <input type="text" name="name" id="name" required>
                                    </div>
                                </form>
                            
                            
                            </p>
                            <a href="#" class="btn btn-primary">Go somewhere</a>
                            </div>
                        </div>`;
  };
  
  export default AddNewObjectPage;