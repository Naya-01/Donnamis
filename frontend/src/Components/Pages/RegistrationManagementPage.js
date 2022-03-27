import profilImage from "../../img/profil.png";
import MemberLibrary from "../../Domain/MemberLibrary";
import SearchBar from "../Module/SearchBar";

const RegistrationManagementPage = async () => {
  let user = await MemberLibrary.prototype.getUserByHisToken();
  console.log(user);

  let actualStatus = 'waiting';
  await SearchBar("Inscriptions", true, true, false,
      "Rechercher une demande d'inscription", false);

  // Load base member
  let members = await MemberLibrary.prototype.getMemberBySearchAndStatus("",
      "waiting");
  await baseMembersList(members);

  // Search members by enter
  const searchBar = document.getElementById("searchBar");
  searchBar.addEventListener("keypress", async (e) => {
    if (e.key === "Enter") {
      members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
          searchBar.value, actualStatus);
      await baseMembersList(members);
    }
  });

  // Search members by click
  const search = document.getElementById("searchButton");
  search.addEventListener("click", async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
        searchBar.value, actualStatus);
    await baseMembersList(members);
  });

  // Filter : all waiting members
  const allWaitingMember = document.getElementById("btn-radio-all");
  allWaitingMember.addEventListener('click', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
        searchBar.value, "waiting");
    actualStatus = 'waiting';
    await baseMembersList(members);
  });

  // Filter : all pending members
  const pendingMember = document.getElementById("btn-radio-pending");
  pendingMember.addEventListener('click', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
        searchBar.value, "pending");
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
  memberCards.innerHTML = ``;
  for (const member of members) {
    const buttonCardId = "button-card-" + member.memberId;

    const divCard = document.createElement("div");
    divCard.id = "member-card-" + member.memberId;
    divCard.className = "row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded";

    const profileImageDiv = document.createElement("div");
    profileImageDiv.className = "col-1 m-auto";

    const profileImage = document.createElement("img");
    profileImage.className = "img-thumbnail";
    profileImage.src = profilImage;
    profileImage.alt = "profile image"
    profileImageDiv.appendChild(profileImage);

    divCard.appendChild(profileImageDiv);

    const informationMemberDiv = document.createElement("div");
    informationMemberDiv.className = "col-7 mt-3";

    const memberBaseInformationSpan = document.createElement("span");
    memberBaseInformationSpan.className = "fs-4";
    memberBaseInformationSpan.innerText = " " + member.firstname + " "
        + member.lastname + " (" + member.username + ")";

    const memberAddressInformationSpan = document.createElement("span")
    memberAddressInformationSpan.className = "text-secondary fs-5";
    memberAddressInformationSpan.innerText = " " +
        member.address.buildingNumber + " " + member.address.street + " " +
        member.address.postcode + " " + member.address.commune + " " +
        member.address.country;

    informationMemberDiv.appendChild(memberBaseInformationSpan);
    informationMemberDiv.appendChild(document.createElement("br"));
    informationMemberDiv.appendChild(memberAddressInformationSpan);

    divCard.appendChild(informationMemberDiv);

    const buttonsCard = document.createElement("div");
    buttonsCard.className = "col-3 mb-4";

    const buttonInput = document.createElement("div");
    buttonInput.className = "d-grid gap-2 d-md-block";
    buttonInput.id = buttonCardId;

    buttonsCard.appendChild(buttonInput);
    divCard.appendChild(buttonsCard);

    const cardForm = document.createElement("div");
    cardForm.id = "card-form-" + member.memberId;
    divCard.appendChild(cardForm);

    memberCards.appendChild(divCard);

    // Show different buttons card depending on status
    if (member.status === "denied") {
      deniedMemberButtons(member.memberId);
    } else {
      normalMemberButtons(member.memberId);
    }
  }
}

const normalMemberButtons = (idMember) => {
  // Hide potential card textarea
  const cardForm = document.getElementById("card-form-" + idMember);
  cardForm.innerHTML = ``;

  // Add refuse and accept buttons
  const buttonDiv = document.getElementById("button-card-" + idMember);
  const refusedButtonId = "refused-button-" + idMember;
  const acceptedButtonId = "accepted-button-" + idMember;
  buttonDiv.innerHTML = `
     <button id="${refusedButtonId}" class="btn btn-lg btn-danger" type="button">Refuser</button>
     <button id="${acceptedButtonId}" class="btn btn-lg btn-success" type="button">Accepter</button>
  `;

  // Refuse member button
  const refusedButton = document.getElementById(refusedButtonId);
  refusedButton.addEventListener('click', () => {
    refuseMember(idMember);
  });

  // Accept member button
  const acceptedButton = document.getElementById(acceptedButtonId);
  acceptedButton.addEventListener('click', () => {
    acceptMember(idMember);
  });
};

const deniedMemberButtons = (idMember) => {
  // Hide potential card textarea
  const cardForm = document.getElementById("card-form-" + idMember);
  cardForm.innerHTML = ``;

  // Add revoke decision button
  const revokeDecisionButtonId = "refused-button-" + idMember;
  const buttonDiv = document.getElementById("button-card-" + idMember);
  buttonDiv.innerHTML = `
     <button id="${revokeDecisionButtonId}" class="btn btn-lg btn-success" type="button">Revenir sur la décision</button>
  `;

  // Revoke decision button listener
  const reverseDecisionButton = document.getElementById(revokeDecisionButtonId);
  reverseDecisionButton.addEventListener('click', async () => {
    // set member valid
    await MemberLibrary.prototype.updateStatus("pending", idMember, "", "");

    // Hide the member card
    const cardMember = document.getElementById("member-card-" + idMember);
    cardMember.hidden = true;

    let pendingButton = document.getElementById("btn-radio-pending");
    pendingButton.click();
  });
};

const acceptMember = (idMember) => {
  // Change value of buttons
  const refusedButton = document.getElementById("refused-button-" + idMember);
  refusedButton.innerText = "Annuler";
  const acceptedButton = document.getElementById("accepted-button-" + idMember);
  acceptedButton.innerText = "Confirmer";

  // Create admin checkbox
  const inputAdmin = document.createElement("input");
  inputAdmin.className = "form-check-input mt-3 fs-4";
  inputAdmin.type = "checkbox";
  inputAdmin.id = "flexCheckDefault";

  const label = document.createElement("label");
  label.className = "form-check-label mt-2 fs-4";
  label.for = "flexCheckDefault";
  label.innerText = "Administrateur ?";

  const buttonDiv = document.getElementById("button-card-" + idMember);
  buttonDiv.appendChild(document.createElement("br"));
  buttonDiv.appendChild(inputAdmin);
  buttonDiv.appendChild(label);

  // Hide potential card textarea
  const cardForm = document.getElementById("card-form-" + idMember);
  cardForm.innerHTML = ``;

  // Cancel confirm member
  refusedButton.addEventListener('click', () => {
    normalMemberButtons(idMember);
  });

  // Confirm accept member
  acceptedButton.addEventListener('click', async () => {
    // accept member db
    let role = ""
    if (document.getElementById("flexCheckDefault").checked) {
      role = "administrator";
    }

    await MemberLibrary.prototype.updateStatus("valid", idMember, "", role);

    const cardMember = document.getElementById("member-card-" + idMember);
    cardMember.hidden = true;
  });
}

const refuseMember = (idMember) => {
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
  textArea.className = "form-control fs-5 mt-1";
  textArea.placeholder = "Indiquez la raison du refus";
  textArea.id = "raisonRefus";

  const label = document.createElement("label");
  label.className = "fs-4 mb-1";
  label.for = "raisonRefus";
  label.innerText = "Raison du refus";

  divTextArea.appendChild(textArea);
  divTextArea.appendChild(label);
  cardForm.appendChild(divTextArea);

  // Cancel ban
  refusedButton.addEventListener('click', () => {
    normalMemberButtons(idMember);
  });

  // Confirm ban
  acceptedButton.addEventListener('click', async () => {
    // refuse member db
    await MemberLibrary.prototype.updateStatus("denied", idMember, "", "");

    // Hide the card
    const cardMember = document.getElementById("member-card-" + idMember);
    cardMember.hidden = true;
  });

};

export default RegistrationManagementPage;
