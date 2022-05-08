import SearchBar from "../Module/SearchBar";
import MemberLibrary from "../../Domain/MemberLibrary";
import ManagementList from "../Module/ManagementList";
import profileImage from "../../img/profil.png"
import OfferLibrary from "../../Domain/OfferLibrary";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import NotificationSA from "../Module/NotificationSA";
import autocomplete from "../Module/AutoComplete";
import noImage from "../../img/noImage.png";
import Member from "../../Domain/Member";
import {getSessionObject} from "../../utils/session";

const memberLibrary = new MemberLibrary();

/**
 * Render the Members page
 */
const MembersPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }
  let member = await memberLibrary.getUserByHisToken();
  if (member.role !== "administrator") {
    Redirect("/");
    return;
  }
  await SearchBar("Membres", true, false, false,
      "Rechercher un membre", false, false);

  let members = await MemberLibrary.prototype.getMemberBySearchAndStatus("", "valid");

  await memberCardList(members);

  // Search members by enter
  const searchBar = document.getElementById("searchBar");
  searchBar.addEventListener('keypress', async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
        searchBar.innerText.toLowerCase(), "valid");
    let finalArray = [];
    Array.prototype.push.apply(finalArray, members.map(m => m.username));
    Array.prototype.push.apply(finalArray, members.map(m => m.address.commune));
    Array.prototype.push.apply(finalArray, members.map(m => m.address.postcode));
    Array.prototype.push.apply(finalArray, members.map(m => m.lastname));
    autocomplete(searchBar, [...new Set(finalArray)]);
  });

  searchBar.addEventListener("keyup", async (e) => {
    if (e.key === "Enter") {
      members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "valid");
      await memberCardList(members);
    }
  });

  // Search members by click
  const search = document.getElementById("searchButton");
  search.addEventListener("click", async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "valid");
    await memberCardList(members);
  });
}

/**
 * Display an HTML member list
 *
 * @param members a list of member
 */
const memberCardList = async (members) => {
  // Create member cards
  const memberCards = document.getElementById("page-body");
  memberCards.innerHTML = ``;
  if (Array.isArray(members)) {
    for (const member of members) {
      let image;
      if (member.image) {
        image = "/api/member/getPicture/" + member.memberId;
      } else {
        image = profileImage;
      }

      ManagementList(member.memberId, memberCards, image,
          member.firstname + " " + member.lastname + " (" + member.username + ")",
          member.address.buildingNumber + " " + member.address.street + " " +
          member.address.postcode + " " + member.address.commune);

      // Show different buttons card depending on status
      const buttonDiv = document.getElementById("button-card-" + member.memberId);
      buttonDiv.innerHTML = `<h5 class="fs-6" id="admin-div-${member.memberId}" style="color: darkred;"></h5>`;
      if (member.role === "administrator") {
        displayAdmin(member);
        await offersButtons(member, true)
      } else {
        await offersButtons(member, true)
        promoteButton(member);
      }
      if (member.status !== "prevented") {
        setDisableButton(member);
      }
    }
  }
}

/**
 * Display the disable button
 *
 * @param member the member to get his card
 */
const setDisableButton = (member) => {
  const buttonDiv = document.getElementById("button-card-" + member.memberId);
  const buttonSetActive = document.createElement("button");
  buttonSetActive.id = "promote-" + member.memberId;
  buttonSetActive.className = "btn btn-warning mb-2 mx-1";
  buttonSetActive.type = "button";
  buttonSetActive.innerText = "Empêcher";
  buttonSetActive.addEventListener('click', async () => {
    let preventedMember = await MemberLibrary.prototype.memberToPrevent(member.memberId, member.version++);
    if (preventedMember !== undefined) {
      buttonSetActive.hidden = true;
      NotificationSA.prototype.getNotification().fire({
        icon: 'success',
        title: "L'utilisateur est temporairement empêché de participer à la donnerie."
      });
    }
  });
  buttonDiv.appendChild(buttonSetActive);
}

/**
 * Display admin hint
 *
 * @param member the member to get his card
 */
const displayAdmin = (member) => {
  const adminSign = document.getElementById("admin-div-" + member.memberId);
  adminSign.innerText = "Administrateur";
}

/**
 * Display the promote button
 *
 * @param member the member to get his card
 */
const promoteButton = (member) => {
  const buttonDiv = document.getElementById("button-card-" + member.memberId);
  const buttonPromote = document.createElement("button");
  buttonPromote.id = "promote-" + member.memberId;
  buttonPromote.className = "btn btn-success mb-2 mx-1";
  buttonPromote.type = "button";
  buttonPromote.innerText = "Promouvoir";
  buttonPromote.addEventListener('click', async () => {
    let memberToUpdate = new Member(null, null, null, null, null,
        null, member.version++, "administrator", null, null, member.memberId);
    const isInserted = await MemberLibrary.prototype.updateMember(memberToUpdate);
    if (isInserted) {
      displayAdmin(member);
      buttonPromote.hidden = true;
      NotificationSA.prototype.getNotification().fire({
        icon: 'success',
        title: "Utilisateur promu !"
      });
    }
  });
  buttonDiv.appendChild(buttonPromote);
}

/**
 * Show offers on a member card
 *
 * @param member the member to retrieve his offers
 * @param actualizeOfferStats if we need to re-display stats about offers of a member
 */
const offersButtons = async (member, actualizeOfferStats) => {
  const buttonDiv = document.getElementById("button-card-" + member.memberId);
  const countdata = await OfferLibrary.prototype.getCountOffers(member.memberId);
  if (actualizeOfferStats) {
    const informationMember = document.getElementById(
        "information-object-" + member.memberId);
    informationMember.innerHTML +=
        `<p class="text-secondary">
          ${countdata.nbNotCollected} objet(s) non-cherché(s) | ${countdata.nbGiven} objet(s) donné(s)
          </p>`;
  }

  const offeredObjectButton = document.createElement("button");
  offeredObjectButton.id = "offered-object-" + member.memberId;
  offeredObjectButton.className = "btn btn-primary mb-2 mx-1";
  offeredObjectButton.type = "button";
  offeredObjectButton.innerText = "Objets offerts (" + countdata.nbOffers + ")";
  buttonDiv.appendChild(offeredObjectButton);

  const receivedObjectButton = document.createElement("button");
  receivedObjectButton.id = "received-object-" + member.memberId;
  receivedObjectButton.className = "btn btn-primary mb-2 mx-1";
  receivedObjectButton.type = "button";
  receivedObjectButton.innerText = "Objets reçus (" + countdata.nbReceived + ")";
  buttonDiv.appendChild(receivedObjectButton);

  const offeredObjects = document.getElementById("offered-object-" + member.memberId);
  let isOfferedObjectsOpen = false;
  const receivedObjects = document.getElementById("received-object-" + member.memberId);
  let isReceivedObjectsOpen = false;
  const cardForm = document.getElementById("card-form-" + member.memberId);

  offeredObjects.addEventListener('click', async () => {
    const offers = await OfferLibrary.prototype.getOffers("",
        member.memberId.toLocaleString(), "", "", "")
    await offerButton(cardForm, offeredObjects, receivedObjects, isOfferedObjectsOpen, offers);
    isOfferedObjectsOpen = !isOfferedObjectsOpen;
    isReceivedObjectsOpen = false;
  });

  receivedObjects.addEventListener('click', async () => {
    const offers = await OfferLibrary.prototype.getGivenOffers(member.memberId);
    await offerButton(cardForm, receivedObjects, offeredObjects, isReceivedObjectsOpen, offers);
    isReceivedObjectsOpen = !isReceivedObjectsOpen;
    isOfferedObjectsOpen = false;
  });
}

/**
 * Dropdown button to show received and offered offers
 *
 * @param cardForm the div of the member card
 * @param mainOffers main offers div
 * @param secondaryOffers secondary offers div
 * @param isMainOffersOpen if the main offers are showed
 * @param mainOffersList the list of main offers
 */
const offerButton = async (cardForm, mainOffers, secondaryOffers, isMainOffersOpen, mainOffersList) => {
  cardForm.innerHTML = ``;
  if (isMainOffersOpen) {
    mainOffers.className = "btn btn-primary mb-2 mx-1";
    secondaryOffers.className = "btn btn-primary mb-2 mx-1";
  } else {
    mainOffers.className = "btn btn-success mb-2 mx-1";
    secondaryOffers.className = "btn btn-primary mb-2 mx-1";
    if (mainOffersList) {
      for (const offer of mainOffersList) {
        let image = noImage;
        if (offer.object.image) {
          image = "/api/object/getPicture/" + offer.object.idObject;
        }
        ManagementList(offer.idOffer, cardForm, image, offer.object.description, offer.timeSlot, "offered");
        const subCardDiv = document.getElementById("member-card-" + offer.idOffer + "-offered");
        subCardDiv.className += " clickable";
        subCardDiv.addEventListener('click', () => {
          RedirectWithParamsInUrl("/objectDetails", "?idOffer=" + offer.idOffer);
        });
      }
    } else {
      cardForm.innerHTML = "Aucun objet";
    }
  }
}

export default MembersPage;