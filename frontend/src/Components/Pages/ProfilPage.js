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
              <label for="firstname" class="form-label">Prénom</label>
              <input type="text" class="form-control" id="firstname" placeholder="prénom">
            </div>
            
            <div class="col-3">
              <label for="lastname" class="form-label">Nom</label>
              <input type="text" class="form-control" id="lastname" placeholder="nom">
            </div>
            
            <div class="col-3">
              <label for="username" class="form-label">Pseudonyme</label>
              <input type="text" class="form-control" id="username" placeholder="pseudonyme">
            </div>
            
            <div class="col-3">
               <label for="phone_number" class="form-label">Numéro de téléphone</label>
               <input id="phone_number" class="form-control" 
                    placeholder="numéro de téléphone" type="text">
            </div>
            
            <!-- SECOND LINE-->
            <div class="col-6">
              <label for="street" class="form-label">Rue</label>
              <input type="text" class="form-control" id="street" placeholder="rue">
            </div>
            
            <div class="col-3">
              <label for="building_number" class="form-label">Numéro</label>
              <input type="text" class="form-control" id="building_number" placeholder="numéro">
            </div>
            
            <div class="col-3">
              <label for="unit_number" class="form-label">Boîte</label>
              <input type="text" class="form-control" id="unit_number" placeholder="boîte">
            </div>
            
            
            <!-- THIRD LINE-->
            <div class="col-2"></div>
            
            <div class="col-3">
              <label for="postcode" class="form-label">Code postal</label>
              <input type="text" class="form-control" id="postcode" placeholder="code postal">
            </div>
            
            <div class="col-6">
              <label for="commune" class="form-label">Commune</label>
              <input type="text" class="form-control" id="commune" placeholder="commune">
            </div>
            
            <div class="col-1"></div>
            
            
            <!-- FOURTH LINE-->
            <div class="col-1"></div>
            
            <div class="col-5">
              <label for="password" class="form-label">Nouveau mot de passe</label>
              <input type="text" class="form-control" id="password" placeholder="Nouveau mot de passe">
            </div>
            
            <div class="col-5">
              <label for="confirm_password" class="form-label">Confirmer mot de passe</label>
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

const ProfilPage = () => {

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = modifyProfilRender();

}

export default ProfilPage;