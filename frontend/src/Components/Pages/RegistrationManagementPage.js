import profilImage from "../../img/profil.png";
import MemberLibrary from "../../Domain/MemberLibrary";
import SearchBar from "../Module/SearchBar";
import managementList from "../Module/ManagementList";
import autocomplete from "../Module/AutoComplete";
import NotificationSA from "../Module/NotificationSA";
import Member from "../../Domain/Member";
import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
const Toast = NotificationSA.prototype.getNotification("bottom");
const memberLibrary = new MemberLibrary();

const RegistrationManagementPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  let member = await memberLibrary.getUserByHisToken();
  if(member.role !== "administrator"){
    Redirect("/");
    return;
  }
  let actualStatus = 'waiting';
  await SearchBar("Inscriptions", true, true, false,
      "Rechercher une demande d'inscription", false);

  // Load base member
  let members = await MemberLibrary.prototype.getMemberBySearchAndStatus("","waiting");
  await baseMembersList(members);

  // Search members by enter
  const searchBar = document.getElementById("searchBar");
  searchBar.addEventListener("keypress", async (e) => {
    if (e.key === "Enter") {
      members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, actualStatus);
      await baseMembersList(members);
    }
    let finalArray = [];
    Array.prototype.push.apply(finalArray, members.map(m => m.username));
    Array.prototype.push.apply(finalArray, members.map(m => m.address.commune));
    Array.prototype.push.apply(finalArray, members.map(m => m.address.postcode));
    Array.prototype.push.apply(finalArray, members.map(m => m.lastname));
    autocomplete(searchBar, [...new Set(finalArray)]);
  });

  // Search members by click
  const search = document.getElementById("searchButton");
  search.addEventListener("click", async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, actualStatus);
    await baseMembersList(members);
  });

  // Filter : all waiting members
  const allWaitingMember = document.getElementById("btn-radio-all");
  allWaitingMember.addEventListener('click', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "waiting");
    actualStatus = 'waiting';
    await baseMembersList(members);
  });

  // Filter : all pending members
  const pendingMember = document.getElementById("btn-radio-pending");
  pendingMember.addEventListener('click', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "pending");
    actualStatus = 'pending';
    await baseMembersList(members);
  });

  // Filter : all denied members
  const deniedMember = document.getElementById("btn-radio-denied");
  deniedMember.addEventListener('click', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
        searchBar.value, "denied");
    actualStatus = 'denied';
    await baseMembersList(members);
  });

};

const baseMembersList = async (members) => {
  // Create member cards
  const memberCards = document.getElementById("page-body");
  if (Array.isArray(members)) {
    memberCards.innerHTML = ``;
    for (const member of members) {
      managementList(member.memberId, memberCards, profilImage,
          member.firstname + " " + member.lastname + " (" + member.username
          + ")",
          member.address.buildingNumber + " " + member.address.street + " " +
          member.address.postcode + " " + member.address.commune);

      // Show different buttons card depending on status
      if (member.status === "denied") {
        deniedMemberButtons(member.memberId, member.version);
      } else {
        normalMemberButtons(member.memberId, member.version);
      }
    }
  } else {
    memberCards.innerHTML = `<p>Aucun membre</p>`;
  }
}

const normalMemberButtons = (idMember, version) => {
  // Hide potential card textarea
  const cardForm = document.getElementById("card-form-" + idMember);
  cardForm.innerHTML = ``;

  // Add refuse and accept buttons
  const buttonDiv = document.getElementById("button-card-" + idMember);
  const refusedButtonId = "refused-button-" + idMember;
  const acceptedButtonId = "accepted-button-" + idMember;
  buttonDiv.innerHTML = `
     <button id="${refusedButtonId}" class="btn btn-danger mt-3" type="button">Refuser</button>
     <button id="${acceptedButtonId}" class="btn btn-success mt-3" type="button">Accepter</button>
  `;

  // Refuse member button
  const refusedButton = document.getElementById(refusedButtonId);
  const acceptedButton = document.getElementById(acceptedButtonId);
  refusedButton.addEventListener('click', () => {
    removeAllListeners(idMember);
    refuseMember(idMember, version);
  });

  // Accept member button
  acceptedButton.addEventListener('click', () => {
    removeAllListeners(idMember);
    acceptMember(idMember, version);
  });

};

const acceptMember = (idMember, version) => {
  // Change value of buttons
  const refusedButton = document.getElementById("refused-button-" + idMember);
  refusedButton.innerText = "Annuler";
  const acceptedButton = document.getElementById("accepted-button-" + idMember);
  acceptedButton.innerText = "Confirmer";

  // Create admin checkbox
  const inputAdmin = document.createElement("input");
  inputAdmin.className = "form-check-input";
  inputAdmin.type = "checkbox";
  inputAdmin.id = "flexCheckDefault";

  const label = document.createElement("label");
  label.className = "form-check-label mx-1";
  label.for = "flexCheckDefault";
  label.setAttribute('for', 'flexCheckDefault');
  label.innerText = "Administrateur ?";

  const buttonDiv = document.getElementById("button-card-" + idMember);
  buttonDiv.appendChild(document.createElement("br"));
  buttonDiv.appendChild(label);
  buttonDiv.appendChild(inputAdmin);

  // Hide potential card textarea
  const cardForm = document.getElementById("card-form-" + idMember);
  cardForm.innerHTML = ``;

  // Cancel confirm member
  refusedButton.addEventListener('click', () => {
    removeAllListeners(idMember);
    normalMemberButtons(idMember);
  });

  // Confirm accept member
  acceptedButton.addEventListener('click', async () => {
    removeAllListeners(idMember);

    document.getElementById("member-card-" + idMember).hidden = true;

    // accept member db
    let role = "";
    let title = "Le membre a été accepté !";
    if (document.getElementById("flexCheckDefault").checked) {
      role = "administrator";
      title = "Le membre a été accepté en tant qu'administrateur !";
    }
    let memberToUpdate = new Member(null, null, null,
        null, null, null, version, role, null, "valid", idMember);
    await MemberLibrary.prototype.updateMember(memberToUpdate);
    await Toast.fire({
      icon: 'success',
      title: title
    });

  });
}

const refuseMember = (idMember, version) => {
  // Change value of buttons
  const refusedButton = document.getElementById("refused-button-" + idMember);
  refusedButton.innerText = "Annuler";
  const acceptedButton = document.getElementById("accepted-button-" + idMember);
  acceptedButton.innerText = "Confirmer";

  // Add textarea
  const cardForm = document.getElementById("card-form-" + idMember);

  const divTextArea = document.createElement("div");
  divTextArea.className = "form-floating m-auto";

  const textArea = document.createElement("textarea");
  textArea.className = "form-control mt-1";
  textArea.placeholder = "Indiquez la raison du refus";
  textArea.id = "raisonRefus";
  textArea.rows = 100;
  textArea.maxLength = 504;
  textArea.setAttribute("style", "resize: none; height: 100px;");

  const label = document.createElement("label");
  label.className = "mb-1";
  label.for = "raisonRefus";
  label.innerText = "Raison du refus";

  divTextArea.appendChild(textArea);
  divTextArea.appendChild(label);
  cardForm.appendChild(divTextArea);

  // Cancel ban
  refusedButton.addEventListener('click', () => {
    removeAllListeners(idMember);
    normalMemberButtons(idMember);
  });

  // Confirm ban
  acceptedButton.addEventListener('click', async () => {
    // get the refusal reason
    let refusalReason = document.getElementById("raisonRefus").value;

    if (refusalReason.length === 0) {
      await Toast.fire({
        icon: 'error',
        title: 'Veuillez entrer une raison de refus !'
      });
      return;
    }

    removeAllListeners(idMember);

    // Hide the card
    document.getElementById("member-card-" + idMember).hidden = true;

    let memberToUpdate = new Member(null, null, null,
        null, null, null, version, null, refusalReason, "denied", idMember);

    // refuse member db
    await MemberLibrary.prototype.updateMember(memberToUpdate);
  });

};

const deniedMemberButtons = (idMember, version) => {
  // Hide potential card textarea
  const cardForm = document.getElementById("card-form-" + idMember);
  cardForm.innerHTML = ``;

  // Add revoke decision button
  const revokeDecisionButtonId = "refused-button-" + idMember;
  const buttonDiv = document.getElementById("button-card-" + idMember);
  buttonDiv.innerHTML = `
     <button id="${revokeDecisionButtonId}" class="btn btn-success mt-3" type="button">Revenir sur la décision</button>
  `;

  // Revoke decision button listener
  const reverseDecisionButton = document.getElementById(revokeDecisionButtonId);
  reverseDecisionButton.addEventListener('click', async () => {
    // Hide the member card
    document.getElementById("member-card-" + idMember).hidden = true;

    let memberToUpdate = new Member(null, null, null,
        null, null, null, version, null, null, "pending", idMember);
    await MemberLibrary.prototype.updateMember(memberToUpdate);
    // set member valid

    // let pendingButton = document.getElementById("btn-radio-pending");
    // pendingButton.click();
  });
};

const removeAllListeners = (idMember) => {
  const refusedButton = document.getElementById("refused-button-" + idMember);
  const acceptedButton = document.getElementById("accepted-button-" + idMember);

  acceptedButton.replaceWith(acceptedButton.cloneNode(true));
  refusedButton.replaceWith(refusedButton.cloneNode(true));
}

export default RegistrationManagementPage;