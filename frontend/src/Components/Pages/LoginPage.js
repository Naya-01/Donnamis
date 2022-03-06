import {getSessionObject, setSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import Navbar from "../Navbar/Navbar";

const htmlPage = `
            <div class="container mt-5">
              <div class="border border-5 border-dark p-5">
                <div class="row mx-5">
                  <span class="px-5 vertical-text fs-1">Se connecter</span>
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
              <div class="" id="notif">
              </div>
            </div>
                  `;

const connectClientAndRedirect = async (username, password, remember) => {
  let userData;

  try {
    let options = {
      method: "POST",
      body: JSON.stringify({
        "username": username,
        "password": password,
        "rememberMe": remember,
      }),
      headers: {
        "Content-Type": "application/json",
      },
    };
    userData = await fetch("/api/auth/login/", options);
    if (!userData.ok) {
      userData.text().then((msg) => {
        let notif = document.getElementById("notif");
        notif.className = "alert alert-warning fs-3 text-center";
        notif.innerHTML = msg;
      })
    }
  } catch (err) {
    console.log(err);
  }
  if (userData.status === 200) {
    userData = await userData.json();

    let userLocalStorage = {
      refreshToken: userData.refresh_token,
      accessToken: userData.access_token,
    }

    setSessionObject("user", userLocalStorage);
    await Navbar();
    Redirect("/");
  }
}

const LoginPage = () => {
  if (getSessionObject("user")) {
    Redirect("/");
    return;
  }

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;

  let btnSubmit = document.getElementById("submitConnect");

  btnSubmit.addEventListener("click", e => {
    e.preventDefault();
    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;
    let remember = document.getElementById("rememberMe").checked;
    let notif = document.getElementById("notif");
    notif.className = "";
    notif.innerHTML = "";
    if (username.length === 0 && password.length === 0) {
      notif.className = "alert alert-warning fs-3 text-center";
      notif.innerHTML = "Veuillez introduire un nom et un mot de passe";
    } else if (username.length === 0) {
      notif.className = "alert alert-warning fs-3 text-center";
      notif.innerHTML = "Veuillez introduire un nom";
    } else if (password.length === 0) {
      notif.className = "alert alert-warning fs-3 text-center";
      notif.innerHTML = "Veuillez introduire un mot de passe";
    } else {
      connectClientAndRedirect(username, password, remember);
    }
  })

};

export default LoginPage;
