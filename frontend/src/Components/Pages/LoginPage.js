import {getSessionObject, setSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import Navbar from "../Navbar/Navbar";

const Swal = require('sweetalert2')

const Toast = Swal.mixin({
  toast: true,
  position: 'bottom',
  showConfirmButton: false,
  timer: 5000,
  timerProgressBar: true,
  didOpen: (toast) => {
    toast.addEventListener('mouseenter', Swal.stopTimer)
    toast.addEventListener('mouseleave', Swal.resumeTimer)
  }
})

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
        Toast.fire({
          icon: 'error',
          title: msg
        })
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

    Toast.fire({
      icon: 'success',
      title: "Bienvenue !"
    })
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
    let username = document.getElementById("username");
    let password = document.getElementById("password");
    if (username.classList.contains("border-danger")) {
      username.classList.remove("border-danger");
    }
    if (password.classList.contains("border-danger")) {
      password.classList.remove("border-danger");
    }
    let remember = document.getElementById("rememberMe").checked;
    if (username.value.length === 0 || password.value.length === 0) {
      Toast.fire({
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
      connectClientAndRedirect(username, password, remember);
    }
  })

};

export default LoginPage;
