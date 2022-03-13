import profilImage from "../../img/profil.png";

const htmlPage = `
                  <div class="container mt-5">
                      <h1 class="fs-1">Inscriptions</h1>
                      <div class="text-center">
                        
                        <!-- Search Bar -->
                        <div class="input-group mb-3 mt-5" >
                        <div class="btn-group mx-2" role="group" aria-label="Basic radio toggle button group">
                          <input type="radio" class="btn-check" name="btnradio" id="btnradio3" autocomplete="off">
                          <label class="btn btn-outline-secondary" for="btnradio3">Aucun</label>
                          
                          <input type="radio" class="btn-check" name="btnradio" id="btnradio1" autocomplete="off">
                          <label class="btn btn-outline-dark" for="btnradio1">En attente</label>
                        
                          <input type="radio" class="btn-check" name="btnradio" id="btnradio2" autocomplete="off">
                          <label class="btn btn-outline-danger" for="btnradio2">Refusé</label>
                        </div>
                          <input type="text" class="form-control fs-4" id="searchBar" placeholder="Rechercher une demande d'inscription">
                          <button class="btn btn-outline-primary fs-4" id="searchButton" type="button">Rechercher</button>
                        </div>
                         
                        <!-- Classic user -->
                        <div class="row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded">
                          <div class="col-1 m-auto">
                             <img class="img-thumbnail" src="${profilImage}" alt="profil">
                          </div>
                          <div class="col-7 mt-3">
                            <span class="fs-4"> membre.prenom membre.nom (membre.pseudo)</span>
                            <br>
                            <span class="text-secondary fs-5"> membre.prenom membre.nom (membre.pseudo)</span>
                          </div>
                          <div class="col-3 mb-4">.
                             <div class="d-grid gap-2 d-md-block ">
                               <button class="btn btn-lg btn-danger" type="button">Refuser</button>
                               <button class="btn btn-lg btn-success" type="button">Accepter</button>
                             </div>
                          </div>
                        </div>
                        
                        <!-- Accept User -->
                        
                        <div class="row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded">
                          <div class="col-1 m-auto">
                             <img class="img-thumbnail" src="${profilImage}" alt="profil">
                          </div>
                          <div class="col-7 mt-3">
                            <span class="fs-4"> membre.prenom membre.nom (membre.pseudo)</span>
                            <br>
                            <span class="text-secondary fs-5"> membre.prenom membre.nom (membre.pseudo)</span>
                          </div>
                          <div class="col-3 mb-4">.
                             <div class="d-grid gap-2 d-md-block ">
                               <button class="btn btn-lg btn-danger" type="button">Retour</button>
                               <button class="btn btn-lg btn-success" type="button">Confirmer</button>
                               <br>
                               <input class="form-check-input mt-3 fs-4" type="checkbox" value="" id="flexCheckDefault">
                               <label class="form-check-label mt-2 fs-4" for="flexCheckDefault">
                                  Administrateur ?
                               </label>
                             </div>
                          </div>
                        </div>
                        
                        <!-- User refused -->
                        
                        <div class="row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded">
                          <div class="col-1 m-auto">
                             <img class="img-thumbnail" src="${profilImage}" alt="profil">
                          </div>
                          <div class="col-7 mt-3">
                            <span class="fs-4"> membre.prenom membre.nom (membre.pseudo)</span>
                            <br>
                            <span class="text-secondary fs-5"> membre.prenom membre.nom (membre.pseudo)</span>
                          </div>
                          <div class="col-3 mb-4">.
                             <div class="d-grid gap-2 d-md-block ">
                               <button class="btn btn-lg btn-danger" type="button">Annuler</button>
                               <button class="btn btn-lg btn-success" type="button">Confirmer</button>
                             </div>
                          </div>
                          <div class="form-floating m-auto">
                            <textarea class="form-control fs-5 mt-1" placeholder="Indiquez la raison du refus" id="raisonRefus" style="height: 100px"></textarea>
                            <label class="fs-4 mb-1" for="raisonRefus">Raison du refus</label>
                          </div>
                        </div>
                      </div>
                      
                      <\hr>
                      
                      <!-- REFUSED USER -->
                      
                      <div class="row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded">
                          <div class="col-1 m-auto">
                             <img class="img-thumbnail" src="${profilImage}" alt="profil">
                          </div>
                          <div class="col-7 mt-3">
                            <span class="fs-4"> membre.prenom membre.nom (membre.pseudo)</span>
                            <br>
                            <span class="text-secondary fs-5"> membre.prenom membre.nom (membre.pseudo)</span>
                            <br>
                            <span class="fs-4 text-danger">Refusé : membres.raison_refus</span>
                          </div>
                          <div class="col-3 mb-4 m-auto">.
                              <button class="btn btn-lg btn-success" type="button">Revenir sur la décision</button>
                          </div>
                        </div>
                      
                  </div>
                `

const RegistrationManagementPage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;

  // Quand on presse enter on lance la recherche
  let searchBar = document.getElementById("searchBar");
  searchBar.addEventListener("keypress", async (e) => {
    if (e.key === "Enter") {
      console.log(searchBar.value)
      // requête DB + affichage
    }
  });
  // Quand on appuye sur la loupe on lance la recherche
  let search = document.getElementById("searchButton");
  search.addEventListener("click", async (e) => {
    e.preventDefault();
    console.log(searchBar.value)
    // requête DB + affichage
  });

};

export default RegistrationManagementPage;
