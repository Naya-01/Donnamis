import SearchBar from "../Module/SearchBar";
import MemberLibrary from "../../Domain/MemberLibrary";
import ManagementList from "../Module/ManagementList";
import profileImage from "../../img/profil.png"
import itemImage from "../../img/item.jpg"
import OfferLibrary from "../../Domain/OfferLibrary";
import {RedirectWithParamsInUrl} from "../Router/Router";
import Notification from "../Module/Notification";
import autocomplete from "../Module/AutoComplete";

/**
 * Render the Members page
 */
const MembersPage = async () => {
  await SearchBar("Membres", true, false, false, "Rechercher un membre", false, false);

  let members = await MemberLibrary.prototype.getMemberBySearchAndStatus("","valid");

  baseMembersList(members);

  // Search members by enter
  const searchBar = document.getElementById("searchBar");
  searchBar.addEventListener('keypress', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.innerText.toLowerCase(), "valid");
    let finalArray = [];
    Array.prototype.push.apply(finalArray, members.map(m => m.username));
    Array.prototype.push.apply(finalArray, members.map(m => m.address.commune));
    Array.prototype.push.apply(finalArray, members.map(m => m.address.postcode));
    autocomplete(searchBar, finalArray);
  });

  searchBar.addEventListener("keyup", async (e) => {
    if (e.key === "Enter") {
      members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "valid");
      await baseMembersList(members);
    }
  });

  // Search members by click
  const search = document.getElementById("searchButton");
  search.addEventListener("click", async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "valid");
    await baseMembersList(members);
  });
}

const baseMembersList = (members) => {
  // Create member cards
  const memberCards = document.getElementById("page-body");
  memberCards.innerHTML = ``;
  for (const member of members) {
    ManagementList(member.memberId, document.getElementById("page-body"),
        profileImage,
        member.firstname + " " + member.lastname + " (" + member.username + ")",
        member.address.buildingNumber + " " + member.address.street + " " +
        member.address.postcode + " " + member.address.commune);

    // Show different buttons card depending on status
    const buttonDiv = document.getElementById("button-card-" + member.memberId);
    buttonDiv.innerHTML += `
        <h5 id="admin-div-${member.memberId}" style="color: darkred;"></h5>
      `;

    if (member.role === "administrator") {
      const adminSign = document.getElementById("admin-div-" + member.memberId);
      adminSign.innerText = "Administrateur";
    } else {
      const buttonPromote = document.createElement("button");
      buttonPromote.id = "promote-" + member.memberId;
      buttonPromote.className = "btn btn-lg btn-success mb-2";
      buttonPromote.type = "button";
      buttonPromote.innerText = "Promouvoir";
      buttonDiv.appendChild(buttonPromote);
    }


    buttonDiv.innerHTML += `
        <button id="offered-object-${member.memberId}" class="btn btn-lg btn-primary mb-2" type="button">Objets offerts</button>
        <button id="received-object-${member.memberId}" class="btn btn-lg btn-primary mb-2" type="button">Objets reçus</button>
      `;

    const offeredObjects = document.getElementById("offered-object-" + member.memberId);
    let isOfferedObjectsOpen = false;
    const receivedObjects = document.getElementById("received-object-" + member.memberId);
    let isReceivedObjectsOpen = false;
    const cardForm = document.getElementById("card-form-" + member.memberId);

    offeredObjects.addEventListener('click', async () => {
      cardForm.innerHTML = ``;
      if (isOfferedObjectsOpen) {
        offeredObjects.className = "btn btn-lg btn-primary mb-2";
        receivedObjects.className = "btn btn-lg btn-primary mb-2";
      } else {
        offeredObjects.className = "btn btn-lg btn-success mb-2";
        receivedObjects.className = "btn btn-lg btn-primary mb-2";
        const offers = await OfferLibrary.prototype.getOffers("", member.memberId.toLocaleString(), "", "")
        if (offers) {
          for (const offer of offers) {
            ManagementList(offer.idOffer, cardForm, itemImage, offer.object.description, offer.timeSlot, "offered");
            const subCardDiv = document.getElementById("member-card-" + offer.idOffer + "-offered");
            subCardDiv.className += " clickable";
            subCardDiv.addEventListener('click', () => {
              RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" + offer.idOffer);
            });
          }
        } else {
          cardForm.innerHTML = "Aucun objet";
        }
      }
      isOfferedObjectsOpen = !isOfferedObjectsOpen;
      isReceivedObjectsOpen = false;
    });

    receivedObjects.addEventListener('click', async () => {
      cardForm.innerHTML = ``;
      if (isReceivedObjectsOpen) {
        offeredObjects.className = "btn btn-lg btn-primary mb-2";
        receivedObjects.className = "btn btn-lg btn-primary mb-2";
      } else {
        offeredObjects.className = "btn btn-lg btn-primary mb-2";
        receivedObjects.className = "btn btn-lg btn-success mb-2";
        const offers = await OfferLibrary.prototype.getGivenOffers(member.memberId);
        if (offers) {
          for (const offer of offers) {
            ManagementList(offer.idOffer, cardForm, itemImage, offer.object.description, offer.timeSlot, "received");
            const subCardDiv = document.getElementById("member-card-" + offer.idOffer + "-received");
            subCardDiv.className += " clickable";
            subCardDiv.addEventListener('click', () => {
              RedirectWithParamsInUrl("/myObjectPage", "?idOffer=" + offer.idOffer);
            });
          }
        } else {
          cardForm.innerHTML = "Aucun objet";
        }
      }
      isReceivedObjectsOpen = !isReceivedObjectsOpen;
      isOfferedObjectsOpen = false;
    });

    const promoteMemberButton = document.getElementById("promote-" + member.memberId);
    if (promoteMemberButton) {
          promoteMemberButton.addEventListener('click', async () => {
        await MemberLibrary.prototype.updateStatus("", member.memberId, "", "administrator");
          Notification.prototype.getNotification().fire({
            icon: 'success',
            title: "Utilisateur promu !"
          });
          const adminSign = document.getElementById("admin-div-" + member.memberId);
          adminSign.innerText = "Administrateur";
          promoteMemberButton.parentNode.removeChild(promoteMemberButton);
      });
    }

  }
}



export default MembersPage;