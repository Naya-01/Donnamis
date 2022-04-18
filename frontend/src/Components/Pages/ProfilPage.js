import noImage from "../../img/noImage.png";
import MemberLibrary from "../../Domain/MemberLibrary";
import Address from "../../Domain/Address";
import Member from "../../Domain/Member";
import Notification from "../Module/Notification";
import Navbar from "../Navbar/Navbar";
import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";

const pageDiv = document.querySelector("#page");
const translationRoles = new Map([
  ['member', 'Membre'],
  ['administrator', 'Administrateur']
]);

const memberLibrary = new MemberLibrary();
const toast = new Notification().getNotification("top-end");

const regOnlyNumbersAndDash = new RegExp('^[0-9-]+$');
const regNumberPhone =
    new RegExp('^[+]?[(]?[0-9]{3}[)]?[- .]?[0-9]{3}[- .]?[0-9]{4,6}$');
//starting with numbers
const regOnlyLettersAndNumbers = new RegExp('^[0-9]+[a-zA-Z]?$');
const regOnlyLettersAndDash = new RegExp('^[a-zA-Z éàùöèê\'ûî-]+$');

let member = null;
let image;
let provImage = null;

const modifyProfilRender = async () => {
  let page = `
    <div class="container mt-5">
      <div class="text-center">
        <img src="${image}" class="rounded-circle clickable" width="15%" alt="profil image" id="image">
        <input type="file" id="upload" style="display:none" name="upload">
        <p>${translationRoles.get(member.role)}</p>
        
        <div class=" ps-5 pe-5 pb-5">
          <form class="row g-3">
          
            <!-- FIRST LINE-->
            <div class="col-3">
              <strong><label for="firstname" class="form-label">Prénom</label></strong>
              <input type="text" class="form-control" id="firstname" placeholder="prénom" value="${member.firstname}">
            </div>
            
            <div class="col-3">
              <strong><label for="lastname" class="form-label">Nom</label></strong>
              <input type="text" class="form-control" id="lastname" placeholder="nom" value="${member.lastname}">
            </div>
            
            <div class="col-3">
              <strong><label for="username" class="form-label">Pseudonyme</label></strong>
              <input type="text" class="form-control" id="username" placeholder="pseudonyme" value="${member.username}">
            </div>
            
            <div class="col-3">
               <strong><label for="phone_number" class="form-label">Numéro de téléphone</label></strong>
               <input id="phone_number" class="form-control" 
                    placeholder="numéro de téléphone" type="text" value="${member.phone
  === undefined ? ""
      : member.phone}">
            </div>
            
            <!-- SECOND LINE-->
            <div class="col-6">
              <strong><label for="street" class="form-label">Rue</label></strong>
              <input type="text" class="form-control" id="street" placeholder="rue" value="${member.address.street}">
            </div>
            
            <div class="col-3">
              <strong><label for="building_number" class="form-label">Numéro</label></strong>
              <input type="text" class="form-control" id="building_number" placeholder="numéro" value="${member.address.buildingNumber}">
            </div>
            
            <div class="col-3">
              <strong><label for="unit_number" class="form-label">Boîte</label></strong>
              <input type="text" class="form-control" id="unit_number" placeholder="boîte" value="${member.address.unitNumber
  === undefined
      ? ""
      : member.address.unitNumber}">
            </div>
            
            
            <!-- THIRD LINE-->
            <div class="col-2"></div>
            
            <div class="col-3">
              <strong><label for="postcode" class="form-label">Code postal</label></strong>
              <input type="text" class="form-control" id="postcode" placeholder="code postal" value="${member.address.postcode}">
            </div>
            
            <div class="col-6">
              <strong><label for="commune" class="form-label">Commune</label></strong>
              <input type="text" class="form-control" id="commune" placeholder="commune" value="${member.address.commune}">
            </div>
            
            <div class="col-1"></div>
            
            
            <!-- FOURTH LINE-->
            <div class="col-1"></div>
            
            <div class="col-5">
              <strong><label for="password" class="form-label">Nouveau mot de passe</label></strong>
              <input type="password" class="form-control" id="password" placeholder="nouveau mot de passe">
            </div>
            
            <div class="col-5">
              <strong><label for="confirm_password" class="form-label">Confirmer mot de passe</label></strong>
              <input type="password" class="form-control" id="confirm_password" placeholder="confirmer nouveau mot de passe">
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

  pageDiv.innerHTML = page;

  const cancelButton = document.querySelector("#submit_cancel_modify");
  const validModifyButton = document.querySelector("#submit_valid_modify");
  const imageField = document.getElementById("image");
  let fileInput = document.querySelector('input[name=upload]');

  imageField.onclick = function () {
    fileInput.click();
  }

  fileInput.onchange = function () {

    let reader = new FileReader();

    reader.onloadend = function () {
      imageField.src = reader.result;
      provImage = reader.result;
    }

    if (fileInput.files[0]) {
      reader.readAsDataURL(fileInput.files[0]);
    }
  }

  cancelButton.addEventListener("click", e => {
    e.preventDefault();
    profilRender();
  })

  validModifyButton.addEventListener("click", async e => {
    e.preventDefault();

    const username = document.getElementById("username");
    const lastname = document.getElementById("lastname");
    const firstname = document.getElementById("firstname");
    const phoneNumber = document.getElementById("phone_number");

    const street = document.getElementById("street");
    const buildingNumber = document.getElementById(
        "building_number");
    const unitNumber = document.getElementById("unit_number");
    const postcode = document.getElementById("postcode");
    const commune = document.getElementById("commune");

    const password = document.getElementById("password");
    const confirmPassword = document.getElementById(
        "confirm_password");

    let nullFields = [phoneNumber.value.trim(), unitNumber.value.trim()];
    let notNullFields = [username, lastname, firstname, street, buildingNumber,
      postcode, commune];

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
      toast.fire({
        icon: 'error',
        title: 'Veuillez remplir tout les champs obligatoires !'
      })
      return;
    }

    if (phoneNumber.classList.contains("border-danger")) {
      phoneNumber.classList.remove("border-danger");
    }
    if (unitNumber.classList.contains("border-danger")) {
      unitNumber.classList.remove("border-danger");
    }
    if (password.classList.contains("border-danger")) {
      password.classList.remove("border-danger");
    }
    if (confirmPassword.classList.contains("border-danger")) {
      confirmPassword.classList.remove("border-danger");
    }

    if (username.value.trim().length > 50) {
      username.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le pseudonyme est trop grand'
      })
      return;
    }
    if (lastname.value.trim().length > 50 ||
        !regOnlyLettersAndDash.test(lastname.value.trim())) {
      lastname.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le nom est trop grand ou est invalide'
      })
      return;
    }

    if (firstname.value.trim().length > 50 ||
        !regOnlyLettersAndDash.test(firstname.value.trim())) {
      firstname.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le prénom est trop grand ou est invalide'
      })
      return;
    }

    if (phoneNumber.value.trim().length !== 0
        && !regNumberPhone.test(phoneNumber.value.trim())) {
      phoneNumber.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le numéro de téléphone est invalide'
      })
      return;
    }

    if (unitNumber.value.trim().length > 15) {
      unitNumber.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le numéro de boite est trop grand ou est invalide'
      })
      return;
    }

    if (buildingNumber.value.trim().length > 8 ||
        !regOnlyLettersAndNumbers.test(buildingNumber.value.trim())) {
      buildingNumber.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le numéro de maison est trop grand ou est invalide'
      })
      return;
    }

    if (street.value.trim().length > 50 ||
        !regOnlyLettersAndDash.test(street.value.trim())) {
      street.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le nom de rue est trop grand ou est invalide'
      })
      return;
    }

    if (postcode.value.trim().length > 15 ||
        !regOnlyNumbersAndDash.test(postcode.value.trim())) {
      postcode.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le numéro de code postal est trop grand ou est invalide'
      })
      return;
    }

    if (commune.value.trim().length > 50 ||
        !regOnlyLettersAndDash.test(commune.value.trim())) {
      commune.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le nom de commune est trop grand ou est invalide'
      })
      return;
    }

    if (password.value.trim().length !== 0 && confirmPassword.value.trim()
        !== password.value.trim()) {
      password.classList.add("border-danger");
      confirmPassword.classList.add("border-danger");
      toast.fire({
        icon: 'error',
        title: 'Le nouveau mot de passe et le mot de passe de confirmation ne sont pas identiques'
      })
      return;
    }

    for (let i = 0; i < nullFields.length; i++) {
      if (nullFields[i].length === 0) {
        nullFields[i] = null;
      }
    }

    let newAddress = new Address(nullFields[1] === null ? null : nullFields[1],
        buildingNumber.value.trim(), street.value.trim(),
        postcode.value.trim(), commune.value.trim());

    let newMember = new Member(username.value.split(' ').join(''),
        lastname.value.trim(), firstname.value.trim(), password.value.trim(),
        nullFields[0] === null ? null : nullFields[0], newAddress,
        member.memberId);

    let memberWithImage;
    if (fileInput.files[0] !== undefined) {
      let formData = new FormData();
      formData.append('file', fileInput.files[0]);
      memberWithImage = await memberLibrary.setImage(formData);
    }

    let memberUpdated = await memberLibrary.updateMember(newMember);
    if (username.value.trim() !== member.username) {
      await Navbar();
    }
    if (memberUpdated != null) {
      member = memberUpdated;
    }

    if (memberWithImage !== undefined) {
      image = provImage;
    }
    await profilRender();

  });
}

const profilRender = async () => {
  let page = `
    <div class="container mt-5">
      <div class="text-center">
        <img src="${image}" class="rounded-circle" width="15%" alt="profil image">
        <p>${translationRoles.get(member.role)}</p>
        
        <div class=" ps-5 pe-5 pb-5">
          <form class="row g-3">
          
            <!-- FIRST LINE-->
            <div class="col-2"></div>
            <div class="col-2">
              <strong><label for="firstname" class="form-label">Prénom</label></strong>
              <p id="firstname">${member.firstname}</p>
            </div>
            
            <div class="col-2">
              <strong><label for="lastname" class="form-label">Nom</label></strong>
              <p id="lastname">${member.lastname}</p>
            </div>
            
            <div class="col-2">
              <strong><label for="username" class="form-label">Pseudonyme</label></strong>
              <p id="username">${member.username}</p>
            </div>
            
            <div class="col-2">
               <strong><label for="phone_number" class="form-label">Numéro de téléphone</label></strong>
               <p id="phone_number">${member.phone === undefined ? "/"
      : member.phone}</p>
            </div>
            
            <div class="col-2"></div>
            
            <!-- SECOND LINE-->
            <div class="col-3"></div>
            <div class="col-2">
              <strong><label for="street" class="form-label">Rue</label></strong>
              <p id="street">${member.address.street}</p>
            </div>
            
            <div class="col-2">
              <strong><label for="building_number" class="form-label">Numéro</label></strong>
              <p id="building_number">${member.address.buildingNumber}</p>
            </div>
            
            <div class="col-2">
              <strong><label for="unit_number" class="form-label">Boîte</label></strong>
              <p id="unit_number">${member.address.unitNumber === undefined
      ? "/"
      : member.address.unitNumber}</p>
            </div>
            <div class="col-3"></div>
            
            <!-- THIRD LINE-->
            <div class="col-4"></div>
            
            <div class="col-2">
              <strong><label for="postcode" class="form-label">Code postal</label></strong>
              <p id="postcode">${member.address.postcode}</p>
            </div>
            
            <div class="col-2">
              <strong><label for="commune" class="form-label">Commune</label></strong>
              <p id="commune">${member.address.commune}</p>
            </div>
            
            <div class="col-4"></div>
            
            <!-- LAST LINE-->
            <div class="col-12">
              <button type="submit" class="btn btn-primary" id="submit_modify">Modifier</button>
            </div>
          </form>
        </div> 
      </div>
    </div>`;

  pageDiv.innerHTML = page;

  const modifyButton = document.querySelector("#submit_modify");

  modifyButton.addEventListener("click", async e => {
    e.preventDefault();

    await modifyProfilRender();
  })

}

const ProfilPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  member = await memberLibrary.getUserByHisToken();
  if (member.image) {
    image = "/api/member/getPicture/" + member.memberId;
  } else {
    image = noImage;
  }
  provImage = image;
  await profilRender();

}

export default ProfilPage;