import Member from "../../Domain/Member";
import Address from "../../Domain/Address";
import Notification from "../Module/Notification";
import MemberLibrary from "../../Domain/MemberLibrary";

const memberLibrary = new MemberLibrary();

const Toast = new Notification().getNotification();

const htmlPage = `
          <div class="container mt-5">
        <div class="border border-5 border-dark p-5">
            <div class="fs-1 text-center">Inscription</div>
          <div class="mx-5">
            <form>
              <div class="form-group mt-3">
                <label>Pseudonyme</label>
                <input id="username" class="form-control" 
                  placeholder="pseudonyme" type="text">
              </div>
              
              <div class="row mt-3">
                <div class="form-group col">
                    <label>Nom</label>
                    <input id="lastname" class="form-control" placeholder="nom" 
                      type="text">
                </div>
                <div class="form-group col">
                    <label>Prenom</label>
                    <input id="firstname" class="form-control" 
                      placeholder="prenom" type="text">
                  </div>
              </div>
              <div class="row mt-3">
                <div class="form-group col">
                  <label>Rue</label>
                  <input id="street" class="form-control" placeholder="rue" 
                    type="text">
                </div>
                <div class="form-group col">
                <label>Numéro de téléphone</label>
                <input id="phone_number" class="form-control" 
                  placeholder="numéro de téléphone" type="text">
              </div>
              </div>
              
              <div class="row mt-3">
                <div class="col form-group">
                    <label>Numéro</label>
                    <input id="building_number" class="form-control" placeholder="numéro"
                      type="text">
                </div>
                <div class="col form-group">
                    <label>Boîte</label>
                    <input id="unit_number" class="form-control" placeholder="boîte" 
                      type="text">
                </div>
                <div class="col form-group">
                    <label>Code postal</label>
                    <input id="postcode" class="form-control" placeholder="CP" 
                      type="text">
                </div>
              </div>
              <div class="row mt-3">
                <div class="col form-group mt-3">
                    <label>Commune</label>
                    <input id="commune" class="form-control" 
                      placeholder="commune" type="text">
                  </div>
                  <div class="col form-group mt-3">
                    <label>Pays</label>
                    <input id="country" class="form-control" placeholder="pays"
                      type="text">
                  </div>
              </div>
              
              <div class="form-group mt-3">
                <label>Mot de passe</label>
                <input id="password" class="form-control" 
                  placeholder="mot de passe" type="password">
              </div>
              <div class="text-center">
                <button class="btn btn-lg btn-primary mt-3" id="submitRegister" 
                  type="submit">S'inscrire</button>
              <div class="text-center">
            </form>
          </div>
        </div>
      </div>
      `

const RegisterPage = async () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;

  let btnSubmit = document.getElementById("submitRegister");

  btnSubmit.addEventListener("click", async e => {
    e.preventDefault();
    //member fields
    let username = document.getElementById("username");
    let lastname = document.getElementById("lastname");
    let firstname = document.getElementById("firstname");
    let phoneNumber = document.getElementById("phone_number");
    let password = document.getElementById("password");

    //address fields
    let street = document.getElementById("street");
    let buildingNumber = document.getElementById("building_number");
    let unitNumber = document.getElementById("unit_number");
    let postcode = document.getElementById("postcode");
    let commune = document.getElementById("commune");
    let country = document.getElementById("country");

    const notNullFields = [username, lastname, firstname, password,
      buildingNumber, street, postcode, commune, country];

    notNullFields.forEach(function (item) {
      if (item.classList.contains("border-danger")) {
        item.classList.remove("border-danger");
      }
    });

    let allNotNullFieldsFilled = true;

    //check if all not null fields are filled
    notNullFields.forEach(function (item) {
      if (item.value.trim().length === 0) {
        item.classList.add("border-danger");
        if (allNotNullFieldsFilled) {
          allNotNullFieldsFilled = false;
        }
      }
    });

    if (!allNotNullFieldsFilled) {
      Toast.fire({
        icon: 'error',
        title: 'Veuillez remplir tout les champs obligatoires !'
      })
    } else {
      let address = new Address(unitNumber.value, buildingNumber.value,
          street.value, postcode.value, commune.value, country.value);
      let member = new Member(username.value, lastname.value, firstname.value,
          password.value, phoneNumber.value, address);

      // Requête DB inscription et redirect
      await memberLibrary.registerMember(member);
      Toast.fire({
        icon: 'success',
        title: `Vous êtes désormais dans l'attente de la validation d'un 
          administrateur de votre profil`
      });
    }
  });
};

export default RegisterPage;
