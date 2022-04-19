import {getSessionObject} from "../../utils/session";
import profilImage from "../../img/profil.png";
import MemberLibrary from "../../Domain/MemberLibrary";

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
    console.log(user)
    if (user.image) {
      image = "/api/member/getPicture/" + user.memberId
    }
  }
  if (username === undefined) {
    navbar = `
          <nav class="navbar navbar-expand-lg navbar-dark bg-navbar">
            <div class="container-fluid">
              <a class="navbar-brand fs-1" href="#" data-uri="/">DONNAMIS</a>
              <button aria-controls="navbarSupportedContent" aria-expanded="false"
                      aria-label="Toggle navigation"
                      class="navbar-toggler" data-bs-target="#navbarSupportedContent"
                      data-bs-toggle="collapse" type="button">
                <span class="navbar-toggler-icon"></span>
              </button>
              <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                  <li class="nav-item">
                    <a class="nav-link fs-4 " href="#" data-uri="/">Accueil</a>
                  </li>
                </ul>
                <div class="d-flex">
                  <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                      <a class="nav-link fs-4 " href="#" data-uri="/register">S'inscrire</a>
                    </li>
                    <li class="nav-item">
                      <a class="nav-link fs-4 " href="#" data-uri="/login" tabindex="-1">Se connecter</a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </nav>
     `
    navbarWrapper.innerHTML = navbar;
  } else {
    navbar = `<nav class="navbar navbar-expand-lg navbar-dark bg-navbar">
    <div class="container-fluid">
        <a class="navbar-brand fs-1" href="#" data-uri="/">DONNAMIS</a>
        <button aria-controls="navbarSupportedContent" aria-expanded="false"
                aria-label="Toggle navigation"
                class="navbar-toggler" data-bs-target="#navbarSupportedContent"
                data-bs-toggle="collapse" type="button">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link fs-5" href="#" data-uri="/">Accueil</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-5 " data-uri="/objects" href="#">Offres</a>
                </li>
                <li class="nav-item button-dot">
                    <a class="nav-link fs-5" data-uri="/assignedObjects" href="#">Objets attribués</a>
<!--                    <span class="dot">5</span>-->
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-5 " href="#" data-uri="/myObjectsPage">Mes offres</a>
                </li>`
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
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item m-auto">
                        <span class="fs-5 text-white fw-bold mx-2" href="#">${username}</span>
                    </li>
                    <li class="nav-item dropdown">
                        <a aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown"
                           href="#"
                           id="navbarDropdown" role="button">
                            <img class="img-thumbnail" id="navbar-profil-picture" alt="profil" src="${image}">
                        </a>
                        <ul aria-labelledby="navbarDropdown" class="bg-navbar dropdown-menu dropdown-menu-end">
                            <li>
                              <a class="dropdown-item dropdown-profil-element bg-navbar fs-5" href="#" data-uri="/profil">
                                Voir son profil
                              </a>
                            </li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li><a class="dropdown-item bg-navbar fs-5 text-danger fw-bolder" data-uri="/logout" href="#">Se
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

  }

};

export default Navbar;