import {getSessionObject} from "../../utils/session";
import profilImage from "../../img/profil.png";
import noImage from "../../img/noImage.png"
import notificationImage from "../../img/notification.png"
import MemberLibrary from "../../Domain/MemberLibrary";
import InterestLibrary from "../../Domain/InterestLibrary";
import {RedirectWithParamsInUrl} from "../Router/Router";
import OfferLibrary from "../../Domain/OfferLibrary";

// Notification dictionnary to know which text show.
const notificationDictionnary = new Map([
  ['assigned', "L'objet vous a été attribué."],
  ['received', "Merci d'avoir récupérer l'objet !"],
  ['cancelled', "L'offre a été annulée"],
  ['not_collected', "Vous n'êtes pas venu chercher l'objet"],
  ['published', "a marquer un interet pour votre offre."],
  ['prevented', "est maintenant empêché de participer à la donnerie"],

]);

// Color dictionnary for notification states.
const colorDictionnary = new Map([
  ['assigned', 'text-success'],
  ['received', 'text-success'],
  ['cancelled', 'text-danger'],
  ['not_collected', 'text-danger'],
  ['published', 'text-info'],
  ['prevented', 'text-danger']
]);

/**
 * Make a navbar with different buttons.
 *
 * @returns {Promise<void>}
 * @constructor
 */
const Navbar = async () => {
  const navbarWrapper = document.querySelector("#navbar");
  let navbar;

  // Get the user object from the localStorage
  let userSession = getSessionObject("user");
  let username = undefined;
  let user_role = undefined;
  let image = profilImage;
  if (userSession) {
    let memberLibraryModal = new MemberLibrary();
    let user = await memberLibraryModal.getUserByHisToken();
    username = user.username;
    user_role = user.role;
    if (user.image) {
      image = "/api/member/getPicture/" + user.memberId
    }
  }
  if (username === undefined) { // Navbar for the quidams
    navbar = `
          <nav class="navbar navbar-expand navbar-dark bg-navbar">
            <div class="container-fluid">
              <a class="navbar-brand mx-2 fs-3" href="#" data-uri="/">DONNAMIS</a>
              
              <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link fs-5" href="#" data-uri="/">Accueil</a>
                    </li>
                </ul>
                <div class="d-flex">
                  <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                      <a class="nav-link fs-5 " href="#" data-uri="/register">S'inscrire</a>
                    </li>
                    <li class="nav-item">
                      <a class="nav-link fs-5 " href="#" data-uri="/login" tabindex="-1">Se connecter</a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </nav>
     `;
    navbarWrapper.innerHTML = navbar;
  } else { // Navbar for the members connected
    navbar = `<nav class="navbar navbar-expand navbar-dark bg-navbar">
    <div class="container-fluid">
        <a class="navbar-brand mx-2 fs-3" href="#" data-uri="/">DONNAMIS</a>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link fs-5" href="#" data-uri="/">Accueil</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-5" data-uri="/offers" href="#">Offres</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-5" data-uri="/assignedObjects" href="#">Objets attribués</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-5" href="#" data-uri="/myObjectsPage">Mes objets</a>
                </li>`
    // If the member is admin
    if (user_role === "administrator") {
      navbar += `<li class="nav-item">
                    <a class="nav-link fs-5" data-uri="/registrationManagement" href="#">Inscriptions</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-5" data-uri="/members" href="#">Membres</a>
                </li>`;
    }

    navbar +=
        `</ul>
            <div class="d-flex">
                <ul class="navbar-nav me-auto mb-2 mb-0">`

    //Notification

    let notificationCount = await InterestLibrary.prototype.getInterestCount();

    if (notificationCount === 0) {
      navbar += `
             <li class="nav-item dropdown mx-2">
                    <a aria-expanded="false" class="nav-link " id="notificationButton" data-bs-toggle="dropdown" href="#" id="navbarDropdown" role="button">
                    <div id="button-dot">
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-bell-fill" viewBox="0 0 16 16">
                      <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901z"></path>
                    </svg>
                  </div>
                </a>
                <ul id="notificationContent" aria-labelledby="navbarDropdown" class="dropdown-menu bg-navbar dropdown-menu-end"></ul>
             </li>
    `
    } else {
      navbar += `
             <li class="nav-item dropdown mx-2">
                    <a aria-expanded="false" class="nav-link " id="notificationButton" data-bs-toggle="dropdown" href="#" id="navbarDropdown" role="button">
                    <div id="button-dot">
<!--                    <img  id="navbar-notification-picture" alt="profil" src="${notificationImage}">-->
                    <svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" fill="currentColor" class="bi bi-bell-fill" viewBox="0 0 16 16">
                      <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2zm.995-14.901a1 1 0 1 0-1.99 0A5.002 5.002 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901z"></path>
                    </svg>
                    <span id="dot">${notificationCount}</span>
                  </div>
                </a>
                <ul id="notificationContent" aria-labelledby="navbarDropdown" class="dropdown-menu bg-navbar dropdown-menu-end"></ul>
             </li>
    `
    }

    // Profil navbar
    navbar += `
                    <li class="nav-item m-auto">
                        <span class="text-white fw-bold mx-2" href="#">${username}</span>
                    </li>
                    <li class="nav-item dropdown">
                        <a aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown"
                           href="#" id="navbarDropdown" role="button">
                            <img style="width: 35px; height: 35px;" class="img-thumbnail" id="navbar-profil-picture" alt="profil" src="${image}">
                        </a>
                        <ul aria-labelledby="navbarDropdown" class="bg-navbar dropdown-menu dropdown-menu-end">
                            <li>
                              <a class="dropdown-item dropdown-profil-element bg-navbar" href="#" data-uri="/profil">
                                Voir son profil
                              </a>
                            </li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li><a class="dropdown-item bg-navbar text-danger fw-bolder" data-uri="/logout" href="#">Se
                                déconnecter</a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</nav>`;

    navbarWrapper.innerHTML = navbar;

    // We get the different element to add content and event listeners.

    let notificationButton = document.getElementById("notificationButton");
    let notificationContentUL = document.getElementById("notificationContent");

    // We fetch the notifications only when we click on the notification button.
    notificationButton.addEventListener("click", async e => {
      let notifications = ``;

      let allNotificationsFetch = await InterestLibrary.prototype.getAllNotifications();
      if (allNotificationsFetch.length !== 0) {
        for (const interest of allNotificationsFetch) {
          let memberId = interest.member.memberId;
          let objectId = interest.object.idObject;
          let description = interest.object.description;
          let notificationPicture;
          // If there is an object picture, then we show it, otherwise we show the default picture.
          if (interest.object.image) {
            notificationPicture = "/api/object/getPicture/"
                + interest.object.idObject
          } else {
            notificationPicture = noImage;
          }

          // A notification HTML code
          if (interest.status === 'published' || interest.status
              === 'prevented') {
            notifications += `<li>
                              <div class="dropdown-item dropdown-profil-element bg-navbar fs-5 notif-items " id="notification-${memberId}-${objectId}" href="#">
                                <div class="row">
                                    <div class="fs-5">
                                      <img src="${notificationPicture}" class="notificationPicture" alt="objectPicture">
                                      <span>${description}</span>
                                    </div>
                                    <div class="fs-5 text-end fw-bolder ${colorDictionnary.get(
                interest.status)}">
                                      <span>
                                        ${interest.member.username + " "
            + notificationDictionnary.get(interest.status)}
                                      </span>
                                      <button class="mx-2 btn btn-secondary" id="shown-${memberId}-${objectId}">
                                        Marquer comme lu
                                      </button>
                                      <button class="btn btn-warning" id="goto-${memberId}-${objectId}">
                                        Voir l'offre
                                      </button>
                                    </div>
                                </div>
                              </div>
                            </li>
                            `;
          } else {
            notifications += `<li>
                              <div class="dropdown-item dropdown-profil-element bg-navbar fs-5 notif-items " id="notification-${memberId}-${objectId}" href="#">
                                <div class="row">
                                    <div class="fs-5">
                                      <img src="${notificationPicture}" class="notificationPicture" alt="objectPicture">
                                      <span>${description}</span>
                                    </div>
                                    <div class="fs-5 text-end fw-bolder ${colorDictionnary.get(
                interest.status)}">
                                      <span>
                                        ${notificationDictionnary.get(
                interest.status)}
                                      </span>
                                      <button class="mx-2 btn btn-secondary" id="shown-${memberId}-${objectId}">
                                        Marquer comme lu
                                      </button>
                                      <button class="btn btn-warning" id="goto-${memberId}-${objectId}">
                                        Voir l'offre
                                      </button>
                                    </div>
                                </div>
                              </div>
                            </li>
                            `;
          }

        }

        // HTML code to show all notification shown
        notifications += `<li>
                        <div class="dropdown-item fs-5" href="#">
                          <div class="row">
                            <button id="allRead" class="btn btn-lg btn-primary">
                                Tout marquer comme lu
                            </button>
                          </div>
                        </div>
                      </li>`

        notificationContentUL.innerHTML = notifications;

        // Mark all the notifications shown and close the notification menu.
        let markAllReadBtn = document.getElementById("allRead");
        markAllReadBtn.addEventListener("click", async e => {
          await InterestLibrary.prototype.markAllNotificationShown();
          let notification = document.getElementById("dot");
          notification.id = "";
          notification.innerText = "";
          let markAllReadBtn = document.getElementById("allRead")
          markAllReadBtn.remove();
        });

        // Disable the default behavior of a dropdown (disappear when we click on the div)
        let notifItems = document.getElementsByClassName("notif-items");
        for (const item of notifItems) {
          item.addEventListener("click", e => {
            event.stopPropagation();
          });
        }

        // Foreach notification we will add the eventListeners
        for (const interest of allNotificationsFetch) {
          let divNotification = document.getElementById(
              "notification-" + interest.member.memberId + "-"
              + interest.object.idObject);

          let btnShown = document.getElementById(
              "shown-" + interest.member.memberId + "-"
              + interest.object.idObject);

          /**
           * Remove the notification and mark it as shown.
           * When there is no notification left, we update the complete innerHTML.
           */
          btnShown.addEventListener("click", async e => {
            divNotification.remove();
            notificationCount--;
            let notification = document.getElementById("dot");
            if (notificationCount === 0) {
              notification.id = "";
              notification.innerText = "";
              let markAllReadBtn = document.getElementById("allRead")
              markAllReadBtn.remove();
              let li = document.createElement("li");
              li.innerHTML = `
                              <div class="dropdown-item dropdown-profil-element bg-navbar fs-5 notif-items" href="#">
                                <div class="row">
                                    <div class="fs-5 text-warning">
                                      <span>Aucune notifications pour le moment...</span>
                                    </div>
                                </div>
                              </div>
                            `
              notificationContentUL.appendChild(li);
            } else {
              notification.innerText = notificationCount;
            }
            await InterestLibrary.prototype.markNotificationShown(
                interest.object.idObject, interest.member.memberId);
          });

          let btnGoto = document.getElementById(
              "goto-" + interest.member.memberId + "-"
              + interest.object.idObject);

          //Redirect to the offer page
          btnGoto.addEventListener("click", async e => {
            let lastOffer = await OfferLibrary.prototype.getLastOfferById(
                interest.object.idObject);
            document.getElementById("notificationButton").click();
            RedirectWithParamsInUrl("/objectDetails",
                "?idOffer=" + lastOffer.idOffer);
          });

        }
      } else {
        // If there is no notification to show
        notifications += `<li>
                              <div class="dropdown-item dropdown-profil-element bg-navbar fs-5 notif-items" href="#">
                                <div class="row">
                                    <div class="fs-5 text-warning">
                                      <span>Aucune notifications pour le moment...</span>
                                    </div>
                                </div>
                              </div>
                            </li> `

        notificationContentUL.innerHTML = notifications;
      }

    })

  }

};

export default Navbar;