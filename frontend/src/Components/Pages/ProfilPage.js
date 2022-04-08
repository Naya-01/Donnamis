import noImage from "../../img/noImage.png";

const modifyProfilRender = () => {
  let image = noImage;
  let page = `
    <div class="container mt-5">
      <div class="text-center">
        <img src="${image}" class="rounded-circle" width="15%">
        <p>role</p>
        
        <div class=" ps-5 pe-5 pb-5">
          <form class="row g-3">
          
            <!-- FIRST LINE-->
            <div class="col-3">
              <strong><label for="firstname" class="form-label">Prénom</label></strong>
              <input type="text" class="form-control" id="firstname" placeholder="prénom">
            </div>
            
            <div class="col-3">
              <strong><label for="lastname" class="form-label">Nom</label></strong>
              <input type="text" class="form-control" id="lastname" placeholder="nom">
            </div>
            
            <div class="col-3">
              <strong><label for="username" class="form-label">Pseudonyme</label></strong>
              <input type="text" class="form-control" id="username" placeholder="pseudonyme">
            </div>
            
            <div class="col-3">
               <strong><label for="phone_number" class="form-label">Numéro de téléphone</label></strong>
               <input id="phone_number" class="form-control" 
                    placeholder="numéro de téléphone" type="text">
            </div>
            
            <!-- SECOND LINE-->
            <div class="col-6">
              <strong><label for="street" class="form-label">Rue</label></strong>
              <input type="text" class="form-control" id="street" placeholder="rue">
            </div>
            
            <div class="col-3">
              <strong><label for="building_number" class="form-label">Numéro</label></strong>
              <input type="text" class="form-control" id="building_number" placeholder="numéro">
            </div>
            
            <div class="col-3">
              <strong><label for="unit_number" class="form-label">Boîte</label></strong>
              <input type="text" class="form-control" id="unit_number" placeholder="boîte">
            </div>
            
            
            <!-- THIRD LINE-->
            <div class="col-2"></div>
            
            <div class="col-3">
              <strong><label for="postcode" class="form-label">Code postal</label></strong>
              <input type="text" class="form-control" id="postcode" placeholder="code postal">
            </div>
            
            <div class="col-6">
              <strong><label for="commune" class="form-label">Commune</label></strong>
              <input type="text" class="form-control" id="commune" placeholder="commune">
            </div>
            
            <div class="col-1"></div>
            
            
            <!-- FOURTH LINE-->
            <div class="col-1"></div>
            
            <div class="col-5">
              <strong><label for="password" class="form-label">Nouveau mot de passe</label></strong>
              <input type="text" class="form-control" id="password" placeholder="Nouveau mot de passe">
            </div>
            
            <div class="col-5">
              <strong><label for="confirm_password" class="form-label">Confirmer mot de passe</label></strong>
              <input type="text" class="form-control" id="confirm_password" placeholder="confirmer nouveau mot de passe">
            </div>
            
            <div class="col-1"></div>
            
            <!-- LAST LINE-->
            <div class="col-12">
              <button type="submit" class="btn btn-primary" id="submit_cancel_modify">Annuler</button>
              <button type="submit" class="btn btn-primary" id="submit_valid_modify">Modifier</button>
            </div>
          </form>
        </div> 
      </div>
    </div>`;

  return page;
}

const profilRender = () => {
  let image = noImage;
  let page = `
    <div class="container mt-5">
      <div class="text-center">
        <img src="${image}" class="rounded-circle" width="15%">
        <p>Membre</p>
        
        <div class=" ps-5 pe-5 pb-5">
          <form class="row g-3">
          
            <!-- FIRST LINE-->
            <div class="col-2"></div>
            <div class="col-2">
              <strong><label for="firstname" class="form-label">Prénom</label></strong>
              <p id="firstname">Caroline</p>
            </div>
            
            <div class="col-2">
              <strong><label for="lastname" class="form-label">Nom</label></strong>
              <p id="lastname">Line</p>
            </div>
            
            <div class="col-2">
              <strong><label for="username" class="form-label">Pseudonyme</label></strong>
              <p id="username">Caro</p>
            </div>
            
            <div class="col-2">
               <strong><label for="phone_number" class="form-label">Numéro de téléphone</label></strong>
               <p id="phone_number">/</p>
            </div>
            
            <div class="col-2"></div>
            
            <!-- SECOND LINE-->
            <div class="col-3"></div>
            <div class="col-2">
              <strong><label for="street" class="form-label">Rue</label></strong>
              <p id="street">Rue de l'Eglise</p>
            </div>
            
            <div class="col-2">
              <strong><label for="building_number" class="form-label">Numéro</label></strong>
              <p id="building_number">11</p>
            </div>
            
            <div class="col-2">
              <strong><label for="unit_number" class="form-label">Boîte</label></strong>
              <p id="unit_number">B1</p>
            </div>
            <div class="col-3"></div>
            
            <!-- THIRD LINE-->
            <div class="col-4"></div>
            
            <div class="col-2">
              <strong><label for="postcode" class="form-label">Code postal</label></strong>
              <p id="postcode">4987</p>
            </div>
            
            <div class="col-2">
              <strong><label for="commune" class="form-label">Commune</label></strong>
              <p id="commune">Stoumont</p>
            </div>
            
            <div class="col-4"></div>
            
            <!-- LAST LINE-->
            <div class="col-12">
              <button type="submit" class="btn btn-primary" id="submit_valid_modify">Modifier</button>
            </div>
          </form>
        </div> 
      </div>
    </div>`;

  return page;
}

const ProfilPage = () => {

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = profilRender();

}

export default ProfilPage;