import {getSessionObject} from "../../utils/session";
import {Redirect, RedirectWithParamsInUrl} from "../Router/Router";
import searchBar from "../Module/SearchBar";
import noImage from "../../img/noImage.png";
import profilImage from "../../img/profil.png";
import OfferLibrary from "../../Domain/OfferLibrary";
import managementList from "../Module/ManagementList";
import button from "bootstrap/js/src/button";
import InterestLibrary from "../../Domain/InterestLibrary";
import Swal from "sweetalert2";

const dictionary = new Map([
  ['interested', 'Intéressé'],
  ['available', 'Publié'],
  ['assigned', 'Attribué'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé'],
  ['not_collected', 'Le receveur n\'est pas venu']
]);

/**
 * Render the page to see an object
 */
const MyObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }

  await searchBar("Mes objets", true, false, true, "Recherche un objet", true,
      true);

  let status = "";
  const searchBarDiv = document.getElementById("searchBar");
  const typeObject = document.getElementById("default-type-list");

  const actualizeCards = async () => {
    let type = typeObject.options[typeObject.selectedIndex].value;
    if (type === "Tout") {
      type = "";
    }
    await objectCards(searchBarDiv.value, type, status);
  }

  await actualizeCards();
  searchBarDiv.addEventListener('keyup', async (e) => {
    if (e.key === 'Enter') {
      await actualizeCards();
    }
  });

  const searchButtonDiv = document.getElementById("searchButton");
  searchButtonDiv.addEventListener('click', async () => {
    await actualizeCards();
  });

  let available = document.getElementById("btn-status-available");
  available.addEventListener('click', async () => {
    status = "available";
    await actualizeCards();
  });

  let given = document.getElementById("btn-status-given");
  given.addEventListener('click', async () => {
    status = "given";
    await actualizeCards();
  });

  let assigned = document.getElementById("btn-status-assigned");
  assigned.addEventListener('click', async () => {
    status = "assigned";
    await actualizeCards();
  });
  let interested = document.getElementById("btn-status-interested");
  interested.addEventListener('click', async () => {
    status = "interested";
    await actualizeCards();
  });

  let cancelled = document.getElementById("btn-status-cancelled");
  cancelled.addEventListener('click', async () => {
    status = "cancelled";
    await actualizeCards();
  });

  let not_collected = document.getElementById("btn-status-not_collected");
  not_collected.addEventListener('click', async () => {
    status = "not_collected";
    await actualizeCards();
  });

  let all = document.getElementById("btn-status-all");
  all.addEventListener('click', async () => {
    status = "";
    await actualizeCards();
  });

  const addButton = document.getElementById("add-new-object-button");
  addButton.addEventListener('click', () => {
    Redirect("/addNewObjectPage")
  });
}

/**
 * Display all the offers
 *
 * @param searchPattern the search pattern for the offers (can be empty)
 * @param type the type of the offers (can be empty)
 * @param status the status of the offers (can be empty)
 * @returns {Promise<void>}
 */
const objectCards = async (searchPattern, type, status) => {
  const memberCards = document.getElementById("page-body");
  const offers = await OfferLibrary.prototype.getOffers(searchPattern, true,
      type, status, "");
  memberCards.innerHTML = ``;
  if (!offers) { // objects is empty
    return;
  }
  for (const offer of offers) {
    let image = noImage;
    if (offer.object.image) {
      image = "/api/object/getPicture/" + offer.object.idObject;
    }
    managementList(offer.idOffer, memberCards, image,
        offer.object.type.typeName + ": " + offer.object.description,
        dictionary.get(offer.object.status));

    const card = document.getElementById("member-card-" + offer.idOffer);
    card.className += " clickable";

    const buttonCard = document.getElementById("button-card-" + offer.idOffer);
    if (offer.object.status !== "cancelled" && offer.object.status
        !== "given") {
      await cancelButton(buttonCard, offer);
    }

    if (offer.object.status !== "given" && offer.object.status !== "assigned") {
      await interestedButton(buttonCard, offer);
    }

    if (offer.object.status === "cancelled" || offer.object.status
        === "not_collected") {
      await reofferButton(buttonCard, offer);
    }

    if (offer.object.status === "assigned") {
      await assignedButtons(buttonCard, offer);
    }

    const informationDiv = document.getElementById(
        "information-object-" + offer.idOffer);
    informationDiv.addEventListener('click', () => {
      RedirectWithParamsInUrl("/objectDetails", "?idOffer=" + offer.idOffer);
    });

  }
}

const reofferButton = async (buttonCard, offer) => {
  const reofferButton = document.createElement("button");
  reofferButton.innerText = "Offrir à nouveau";
  reofferButton.type = "button";
  reofferButton.className = "btn btn-success mt-3 mx-1";
  reofferButton.addEventListener("click", async () => {
    if (!(await OfferLibrary.prototype.addOffer(
        offer.timeSlot,
        offer.object.idObject,
        offer.object.version
    ))) {
      return;
    }
    offer = await OfferLibrary.prototype.getLastOfferById(
        offer.object.idObject);
    buttonCard.innerHTML = ``;
    buttonCard.parentNode.parentNode.children[1].children[2].innerHTML = dictionary.get(
        offer.status);
    cancelButton(buttonCard, offer);
    await interestedButton(buttonCard, offer);
  });

  buttonCard.appendChild(reofferButton);
}

const assignedButtons = (buttonCard, offer) => {
  const viewReceiverButton = document.createElement("button");
  viewReceiverButton.innerText = "Voir le receveur";
  viewReceiverButton.type = "button";
  viewReceiverButton.className = "btn btn-primary mt-3 mx-1";

  const nonRealisedOfferButton = document.createElement("button");
  nonRealisedOfferButton.innerText = "Non réalisée";
  nonRealisedOfferButton.type = "button";
  nonRealisedOfferButton.className = "btn btn-danger mt-3 mx-1";
  nonRealisedOfferButton.addEventListener("click", async () => {
    if (!(await OfferLibrary.prototype.notCollectedObject(
        offer.idOffer,
        offer.version++,
        offer.object.version++
    ))) {
      return;
    }
    offer = await OfferLibrary.prototype.getLastOfferById(
        offer.object.idObject);
    buttonCard.parentNode.parentNode.children[1].children[2].innerHTML = 'Le receveur n\'est pas venu';
    buttonCard.innerHTML = ``;
    cancelButton(buttonCard, offer);
    await interestedButton(buttonCard, offer);
    await reofferButton(buttonCard, offer);
  });

  const offeredObjectButton = document.createElement("button");
  offeredObjectButton.innerText = "Objet donné";
  offeredObjectButton.type = "button";
  offeredObjectButton.className = "btn btn-success mt-3 mx-1";
  offeredObjectButton.addEventListener("click", async () => {
    if (!(await OfferLibrary.prototype.giveObject(
        offer.object.idObject,
        offer.version++,
        offer.object.version++
    ))) {
      return;
    }
    buttonCard.innerHTML = ``;
    buttonCard.parentNode.parentNode.children[1].children[2].innerHTML = 'Donné';
  });
  buttonCard.appendChild(nonRealisedOfferButton);
  buttonCard.appendChild(offeredObjectButton);
}

const cancelButton = (buttonCard, offer) => {
  const cancelButton = document.createElement("button");
  cancelButton.innerText = "Annuler";
  cancelButton.type = "button";
  cancelButton.className = "btn btn-danger mt-3 mx-1";
  cancelButton.addEventListener("click", async () => {
    if (!(await OfferLibrary.prototype.cancelObject(
        offer.idOffer,
        offer.version,
        offer.object.version
    ))) {
      return;
    }
    offer = await OfferLibrary.prototype.getLastOfferById(
        offer.object.idObject);
    buttonCard.parentNode.parentNode.children[1].children[2].innerHTML = 'Annulé';
    buttonCard.innerHTML = ``;
    await interestedButton(buttonCard, offer);
    await reofferButton(buttonCard, offer);
  });

  buttonCard.appendChild(cancelButton);
}

const interestedButton = async (buttonCard, offer) => {
  if (offer.status !== "cancelled") {
    const viewAllInterestedMembers = document.createElement("button");

    viewAllInterestedMembers.innerText = "Voir les interessés";
    viewAllInterestedMembers.type = "button";
    viewAllInterestedMembers.className = "btn btn-primary mt-3 mx-1";
    viewAllInterestedMembers.addEventListener("click", async e => {
      let interests = await InterestLibrary.prototype.getAllInterests(
          offer.object.idObject);
      if (interests === undefined) {
        interests = [];
      }
      var allInterests = `<div class="container">`

      for (const interest of interests) {

        if (interest.status !== "published") {
          continue;
        }

        let phone;

        if (interest.isCalled) {
          phone = "(Appelez moi :  "
              + interest.member.phone
              + ")";
        } else {
          phone = "";
        }
        let username = interest.member.username;
        let name = interest.member.firstname + " " + interest.member.lastname;
        let availabilityDate = "Date de disponibilité : "
            + interest.availabilityDate[2]
            + "/" + interest.availabilityDate[1] + "/"
            + interest.availabilityDate[0];

        let image;
        if (interest.member.image) {
          image = "/api/member/getPicture/" + interest.member.memberId;
        } else {
          image = profilImage;
        }
        allInterests += `
              <div class="row border border-1 border-dark mt-5 shadow p-3 mb-5 bg-body rounded">
                <div class="col-2 m-auto">
                  <img class="img-thumbnail" src="${image}" alt="image">
                </div>
                <div class="col-7 mt-3">
                  <p class="fs-4">${username} (${name}) ${phone}</p>
                  <span class="text-secondary fs-5">${availabilityDate}</span>
                </div>`

        allInterests += ` 
                <div class="col-3 mt-2">
                  <button class="btn btn-lg btn-primary" id="${"interest-"
        + interest.member.memberId}">Choisir</button>
                </div>`

        allInterests += `</div>`
      }

      allInterests += `</div>`

      Swal.fire({
        title: '<strong>Membres interessés</strong>',
        html: allInterests,
        width: 1000,
        scrollbarPadding: true,
        showCloseButton: true,
        showConfirmButton: false,
        showCancelButton: true,
        focusConfirm: false,
        cancelButtonText: 'Annuler',
      })

      for (const interest of interests) {
        const btn = document.getElementById(
            "interest-" + interest.member.memberId);
        if (btn) {
          btn.addEventListener("click", async e => {
            if (!(await InterestLibrary.prototype.assignOffer(
                interest.idObject, interest.idMember,
                interest.version++, offer.version, offer.object.version
            ))) {
              return;
            }
            offer = await OfferLibrary.prototype.getLastOfferById(
                offer.object.idObject);
            buttonCard.parentNode.parentNode.children[1].children[2].innerHTML = 'Attribué';
            await reofferButton(buttonCard, offer);
            buttonCard.innerHTML = ``;
            cancelButton(buttonCard, offer);
            assignedButtons(buttonCard, offer);
          })
        }
      }

    });
    const countInterestedMembers = await InterestLibrary.prototype.getInterestedCount(
        offer.object.idObject);
    const notificationInterested = document.createElement("span");
    notificationInterested.className = "badge badge-light";
    notificationInterested.innerText = countInterestedMembers.count;
    viewAllInterestedMembers.appendChild(notificationInterested);

    buttonCard.appendChild(viewAllInterestedMembers);
  }
}

export default MyObjectsPage;