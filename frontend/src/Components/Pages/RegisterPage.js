import {setSessionObject} from "../../utils/session";
import Navbar from "../Navbar/Navbar";
import {Redirect} from "../Router/Router";

const Swal = require('sweetalert2')

const Toast = Swal.mixin({
  toast: true,
  position: 'bottom',
  showConfirmButton: false,
  timer: 5000,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.addEventListener('mouseenter', Swal.stopTimer)
    toast.addEventListener('mouseleave', Swal.resumeTimer)
  }
})

const htmlPage = `
          <div class="container mt-5">
        <div class="border border-5 border-dark p-5">
            <div class="fs-1 text-center">Inscription</div>
          <div class="mx-5">
            <form>
              <div class="form-group mt-3">
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
              <div class="row mt-3">
                <div class="form-group col">
                  <label>Rue</label>
                  <input id="street" class="form-control" placeholder="rue" type="text">
                </div>
                <div class="form-group col">
                <label>Numéro de téléphone</label>
                <input id="phone_number" class="form-control" placeholder="numéro de téléphone" type="text">
              </div>
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
                    <input id="country" class="form-control" placeholder="pays" type="text">
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
      </div>
      `

const RegisterPage = async () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;

  let btnSubmit = document.getElementById("submitRegister");

  btnSubmit.addEventListener("click", async e => {
    e.preventDefault();
    let username = document.getElementById("username");
    if (username.classList.contains("border-danger")) {
      username.classList.remove("border-danger");
    }
    let lastname = document.getElementById("lastname");
    if (lastname.classList.contains("border-danger")) {
      lastname.classList.remove("border-danger");
    }
    let firstname = document.getElementById("firstname");
    if (firstname.classList.contains("border-danger")) {
      firstname.classList.remove("border-danger");
    }
    let street = document.getElementById("street");
    if (street.classList.contains("border-danger")) {
      street.classList.remove("border-danger");
    }
    let number = document.getElementById("number");
    if (number.classList.contains("border-danger")) {
      number.classList.remove("border-danger");
    }
    let box = document.getElementById("box");
    if (box.classList.contains("border-danger")) {
      box.classList.remove("border-danger");
    }
    let postalcode = document.getElementById("postalcode");
    if (postalcode.classList.contains("border-danger")) {
      postalcode.classList.remove("border-danger");
    }
    let commune = document.getElementById("commune");
    if (commune.classList.contains("border-danger")) {
      commune.classList.remove("border-danger");
    }
    let country = document.getElementById("country");
    if (country.classList.contains("border-danger")) {
      country.classList.remove("border-danger");
    }
    let password = document.getElementById("password");
    if (password.classList.contains("border-danger")) {
      password.classList.remove("border-danger");
    }

    if (username.value.length === 0 || lastname.value.length === 0
        || firstname.value.length === 0
        || street.value.length === 0 || number.value.length === 0
        || box.value.length === 0
        || postalcode.value.length === 0 || commune.value.length === 0
        || country.value.length === 0 || password.value.length === 0) {
      Toast.fire({
        icon: 'error',
        title: 'Veuillez remplir tout les champs obligatoires !'
      })

      if (username.value.length === 0) {
        username.classList.add("border-danger");
      }
      if (lastname.value.length === 0) {
        lastname.classList.add("border-danger");
      }
      if (firstname.value.length === 0) {
        firstname.classList.add("border-danger");
      }
      if (street.value.length === 0) {
        street.classList.add("border-danger");
      }
      if (number.value.length === 0) {
        number.classList.add("border-danger");
      }
      if (box.value.length === 0) {
        box.classList.add("border-danger");
      }
      if (postalcode.value.length === 0) {
        postalcode.classList.add("border-danger");
      }
      if (commune.value.length === 0) {
        commune.classList.add("border-danger");
      }
      if (country.value.length === 0) {
        country.classList.add("border-danger");
      }
      if (password.value.length === 0) {
        password.classList.add("border-danger");
      }

    } else {

      // Requête DB inscription et redirect
      await registerMember(username.value, lastname.value, firstname.value,
          password.value, box.value, number.value, street.value,
          postalcode.value, commune.value, country.value)
      await Navbar();
      Redirect("/");
      Toast.fire({
        icon: 'success',
        title: 'Bienvenue !'
      })
    }

  });

};

const registerMember = async (username, lastname, firstname,
    password, unitNumber, buildingNumber, street, postcode,
    commune, country) => {

  let userData;
  try {
    let options = {
      method: "POST",
      body: JSON.stringify({
            "username": username,
            "lastname": lastname,
            "firstname": firstname,
            "password": password,
            //"phone": phoneNumber,
            "address": {
              "unitNumber": unitNumber,
              "buildingNumber": buildingNumber,
              "street": street,
              "postcode": postcode,
              "commune": commune,
              "country": country
            }
          }
      ),
      headers: {
        "Content-Type": "application/json",
      },
    };
    userData = await fetch("/api/auth/register/", options);
    if (!userData.ok) {
      userData.text().then((msg) => {
        Toast.fire({
          icon: 'error',
          title: msg
        })
      })
    }
  } catch (err) {
    console.log(err);
  }
  if (userData.status === 200) {
    userData = await userData.json();

    let userLocalStorage = {
      refreshToken: userData.refresh_token,
      accessToken: userData.access_token,
    }

    setSessionObject("user", userLocalStorage);
  }
}

export default RegisterPage;
