import {setSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import Navbar from "../Navbar/Navbar";
import MemberLibrary from "../../Domain/MemberLibrary";
import NotificationSA from "../Module/NotificationSA";

const Toast = NotificationSA.prototype.getNotification("bottom")

const htmlPage = `
            <div class="container mt-5">
              <div class="border border-5 border-dark p-5">
                <div class="row mx-5">
                  <div class="fs-1 text-center mb-2">Connexion</div>
                </div>
                <div class="mx-5">
                  <form>
                    <div class="form-group">
                      <label>Pseudonyme</label>
                      <input id="username" class="form-control" placeholder="Entrez votre pseudonyme" type="text">
                    </div>
                    <div class="form-group mt-3">
                      <label>Mot de passe</label>
                      <input id="password" class="form-control" placeholder="Entrez votre mot de passe" type="password">
                    </div>
                    <div class="mt-3 form-check">
                      <input id="rememberMe" type="checkbox" class="form-check-input">
                      <label class="form-check-label">Se souvenir de moi</label>
                    </div>
                    <button class="btn btn-primary mt-3" id="submitConnect" type="submit">Se connecter</button>
                  </form>
                </div>
              </div>
            </div>
                  `;

/**
 * Connect the member.
 *
 * @param username username of the member
 * @param password password of the member
 * @param remember boolean field if we want to remember our data
 * @returns {Promise<void>}
 */
const connectClientAndRedirect = async (username, password, remember) => {
  //login the member with the api
  let userData = await MemberLibrary.prototype.login(username, password,
      remember)

  let userLocalStorage = {
    refreshToken: userData.refresh_token,
    accessToken: userData.access_token,
  }

  setSessionObject("user", userLocalStorage);
  await Navbar();
  Redirect("/");
}

/**
 * Make the Login page.
 *
 * @constructor
 */
const LoginPage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;

  let btnSubmit = document.getElementById("submitConnect");

  //if click on submit button for login
  btnSubmit.addEventListener("click", async e => {
    e.preventDefault();
    let username = document.getElementById("username");
    let password = document.getElementById("password");
    if (username.classList.contains("border-danger")) {
      username.classList.remove("border-danger");
    }
    if (password.classList.contains("border-danger")) {
      password.classList.remove("border-danger");
    }
    let remember = document.getElementById("rememberMe").checked;
    //check fields
    if (username.value.length === 0 || password.value.length === 0) {
      await Toast.fire({
        icon: 'error',
        title: 'Veuillez remplir les champs obligatoires !'
      })

      if (password.value.length === 0) {
        password.classList.add("border-danger");
      }

      if (username.value.length === 0) {
        username.classList.add("border-danger");
      }

    } else {
      // connect the member
      await connectClientAndRedirect(username.value, password.value, remember);
    }
  })

};

export default LoginPage;
