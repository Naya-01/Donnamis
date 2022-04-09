import noImage from "../../img/noImage.png";
import MemberLibrary from "../../Domain/MemberLibrary";
import Address from "../../Domain/Address";
import Member from "../../Domain/Member";
import Navbar from "../Navbar/Navbar";

const pageDiv = document.querySelector("#page");
const translationRoles = new Map([
  ['member', 'Membre'],
  ['administrator', 'Administrateur']
]);

const memberLibrary = new MemberLibrary();
let member = null;

const modifyProfilRender = async () => {
  let image = noImage;
  let page = `
    <div class="container mt-5">
      <div class="text-center">
        <img src="${image}" class="rounded-circle" width="15%">
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

  pageDiv.innerHTML = page;

  const cancelButton = document.querySelector("#submit_cancel_modify");
  const validModifyButton = document.querySelector("#submit_valid_modify");

  cancelButton.addEventListener("click", e => {
    e.preventDefault();
    profilRender();
  })

  validModifyButton.addEventListener("click", async e => {
    e.preventDefault();
    const username = document.getElementById("username").value.split(' ').join(
        '');
    const lastname = document.getElementById("lastname").value.trim();
    const firstname = document.getElementById("firstname").value.trim();
    const phoneNumber = document.getElementById("phone_number").value.trim();

    const street = document.getElementById("street").value.trim();
    const buildingNumber = document.getElementById(
        "building_number").value.trim();
    const unitNumber = document.getElementById("unit_number").value.trim();
    const postcode = document.getElementById("postcode").value.trim();
    const commune = document.getElementById("commune").value.trim();

    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById(
        "confirm_password").value.trim();

    let fields = [username, lastname, firstname, phoneNumber, street,
      buildingNumber, unitNumber, postcode, commune, confirmPassword];

    for (let i = 0; i < fields.length; i++) {
      if (fields[i].length === 0) {
        fields[i] = null;
      }
    }

    let newAddress = new Address(fields[6], fields[5], fields[4], fields[7],
        fields[8]);

    let newMember = new Member(fields[0], fields[1], fields[2], fields[9],
        fields[3], newAddress, member.memberId);

    let memberUpdated = await memberLibrary.updateMember(newMember);
    if (fields[1] !== member.username)
      await Navbar();
    if (memberUpdated != null) {
      member = memberUpdated;
    }
    await profilRender();

  });
}

const profilRender = async () => {
  let image = noImage;
  let page = `
    <div class="container mt-5">
      <div class="text-center">
        <img src="${image}" class="rounded-circle" width="15%">
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
  member = await memberLibrary.getUserByHisToken();
  await profilRender();

}

export default ProfilPage;