
const htmlPage = `
          <div class="container mt-5">
        <div class="border border-5 border-dark p-5">
            <div class="fs-1 text-center">Inscription</div>
          <div class="mx-5">
            <form>
              <div class="form-group">
                <label>Pseudonyme</label>
                <input id="username" class="form-control" placeholder="pseudonyme" type="text">
              </div>
              <div class="row mt-3">
                <div class="form-group col">
                    <label>Nom</label>
                    <input id="lastname" class="form-control" placeholder="nom" type="text">
                </div>
                <div class="form-group col">
                    <label>Prenom</label>
                    <input id="firstname" class="form-control" placeholder="prenom" type="text">
                  </div>
              </div>
              <div class="form-group mt-3">
                <label>Rue</label>
                <input id="street" class="form-control" placeholder="rue" type="text">
              </div>
              <div class="row mt-3">
                <div class="col form-group">
                    <label>Numéro</label>
                    <input id="number" class="form-control" placeholder="numéro" type="text">
                </div>
                <div class="col form-group">
                    <label>Boîte</label>
                    <input id="box" class="form-control" placeholder="boîte" type="text">
                </div>
                <div class="col form-group">
                    <label>Code postal</label>
                    <input id="postalcode" class="form-control" placeholder="CP" type="text">
                </div>
              </div>
              <div class="row mt-3">
                <div class="col form-group mt-3">
                    <label>Commune</label>
                    <input id="commune" class="form-control" placeholder="commune" type="text">
                  </div>
                  <div class="col form-group mt-3">
                    <label>Pays</label>
                    <input id="commune" class="form-control" placeholder="pays" type="text">
                  </div>
              </div>
              
              <div class="form-group mt-3">
                <label>Mot de passe</label>
                <input id="password" class="form-control" placeholder="mot de passe" type="password">
              </div>
              <div class="text-center">
                <button class="btn btn-lg btn-primary mt-3" id="submitRegister" type="submit">S'inscrire</button>
              <div class="text-center">
                
            </form>
          </div>
        </div>
        <div class="" id="notif">
        </div>
      </div>
      `

const RegisterPage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;
};

export default RegisterPage;
