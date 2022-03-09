import {getSessionObject} from "../../utils/session";
import profilImage from "../../img/profil.png";
import {Redirect, refreshToken} from "../Router/Router";

const getUsername = async () => {
  if (!await refreshToken()) {
    return;
  }
  let userData;
  try {
    let options = {
      method: "POST",
      headers: {
        Authorization: getSessionObject("user").accessToken
      },
    };
    userData = await fetch("/api/auth/getuserbytoken/", options);
    if (!userData.ok) {
      Redirect("/connexion");
      Navbar();
      return;
    }
  } catch (err) {
    console.log(err);
  }
  userData = await userData.json();
  return userData.user.username;
}

const Navbar = async () => {
  const navbarWrapper = document.querySelector("#navbar");
  let navbar;

  // Get the user object from the localStorage
  let userSession = getSessionObject("user");
  if (!userSession) {
    navbar = `
          <nav class="navbar navbar-expand-lg navbar-dark bg-navbar">
            <div class="container-fluid">
              <a class="navbar-brand fs-1" href="#">DONNAMIS</a>
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
                      <a class="nav-link fs-4 " href="#" data-uri="/inscription">S'inscrire</a>
                    </li>
                    <li class="nav-item">
                      <a class="nav-link fs-4 " href="#" data-uri="/connexion" tabindex="-1">Se connecter</a>
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
        <a class="navbar-brand fs-1" href="#">DONNAMIS</a>
        <button aria-controls="navbarSupportedContent" aria-expanded="false"
                aria-label="Toggle navigation"
                class="navbar-toggler" data-bs-target="#navbarSupportedContent"
                data-bs-toggle="collapse" type="button">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link fs-4" href="#" data-uri="/">Accueil</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-4 " href="#">Objets</a>
                </li>
                <li class="nav-item button-dot">
                    <a class="nav-link fs-4" href="#">Objets attribués</a>
                    <span class="dot">5</span>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-4 " href="#">Mes objets</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-4 " href="#" data-uri="/addNewObjectPage">AJOUTER UN OBJET</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-4 " href="#">Inscriptions</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link fs-4 " href="#">Membres</a>
                </li>

            </ul>
            <div class="d-flex">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item m-auto">
                        <a class="nav-link fs-2 text-white fw-bold " href="#">${await getUsername()}</a>
                    </li>
                    <li class="nav-item dropdown px-5">
                        <a aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown"
                           href="#"
                           id="navbarDropdown" role="button">
                            <img alt="profil" src="${profilImage}">
                        </a>
                        <ul aria-labelledby="navbarDropdown" class="bg-navbar dropdown-menu">
                            <li><a
                                    class="dropdown-item dropdown-profil-element bg-navbar fs-4"
                                    href="#">Voir son profil</a>
                            </li>
                            <li><a
                                    class="dropdown-item dropdown-profil-element bg-navbar fs-4"
                                    href="#">Voir mes offres</a>
                            </li>
                            <li>
                                <hr class="dropdown-divider">
                            </li>
                            <li><a class="dropdown-item bg-navbar fs-4 text-danger fw-bolder" data-uri="/deconnexion" href="#">Se
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