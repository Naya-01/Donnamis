import HomePage from "../Pages/HomePage";
import LoginPage from "../Pages/LoginPage";
import Logout from "../Logout/Logout";
import AddNewObjectPage from "../Pages/AddNewObjectPage";
import Navbar from "../Navbar/Navbar";
import ObjectDetailsPage from "../Pages/ObjectDetailsPage";
import RegistrationManagementPage from "../Pages/RegistrationManagementPage";
import AllObjectsPage from "../Pages/AllObjectsPage";
import RegisterPage from "../Pages/RegisterPage";
import MyObjectsPage from "../Pages/MyObjectsPage";
import MembersPage from "../Pages/MembersPage";
import AssignedObjectsPage from "../Pages/AssignedObjectsPage";
import ProfilPage from "../Pages/ProfilPage";

import {
  getSessionObject,
  removeSessionObject,
  setSessionObject
} from "../../utils/session";

// Configure your routes here
const routes = {
  "/": HomePage,
  "/addNewObjectPage": AddNewObjectPage,
  "/registrationManagement": RegistrationManagementPage,
  "/offers": AllObjectsPage,
  "/login": LoginPage,
  "/logout": Logout,
  "/register": RegisterPage,
  "/objectDetails": ObjectDetailsPage,
  "/myObjectsPage": MyObjectsPage,
  "/members": MembersPage,
  "/assignedObjects": AssignedObjectsPage,
  "/profil": ProfilPage
};

const refreshToken = async () => {
  let refreshData;
  try {
    let options = {
      method: "GET",
      headers: {
        Authorization: getSessionObject("user").refreshToken
      },
    };
    refreshData = await fetch("/api/auth/refreshToken/", options);
    if (!refreshData.ok) {
      throw new Error(
          "fetch error : " + refreshData.status + " : " + refreshData.statusText
      );
    }
  } catch (err) {
    console.log(err);
  }
  if (refreshData.status === 200) {
    //update AccessToken
    refreshData = await refreshData.json();
    let actualData = getSessionObject("user");
    if (actualData === undefined) {
      return;
    }
    actualData.accessToken = refreshData.access_token;
    setSessionObject("user", actualData);
    return true;
  } else {
    removeSessionObject("user");
    await Navbar();
    Redirect("/");
    return false;
  }
}

/**
 * Deal with call and auto-render of Functional Components following click
 * events on Navbar, Load / Refresh operations, Browser history operation
 * (back or next) or redirections.
 * A Functional Component is responsible to auto-render itself : Pages, ...
 */

const Router = () => {
  /* Manage click on the Navbar */
  let navbarWrapper = document.querySelector("#navbar");
  navbarWrapper.addEventListener("click", (e) => {
    let uri = e.target.dataset.uri;
    if (getSessionObject("user")) {
      if (!refreshToken()) {
        return;
      }
    }

    if (uri) {
      e.preventDefault();
      window.history.pushState({}, uri, window.location.origin + uri);
      const componentToRender = routes[uri];
      if (routes[uri]) {
        componentToRender();
      } else {
        throw Error("La ressource " + uri + " n'existe pas");
      }
    }
  });

  window.addEventListener("load", (e) => {
    if (getSessionObject("user")) {
      if (!refreshToken()) {
        return;
      }
    }

    const componentToRender = routes[window.location.pathname];
    if (!componentToRender) {
      throw Error(
          "La ressource " + window.location.pathname + " n'existe pas."
      );
    }

    componentToRender();
  });

  window.addEventListener("popstate", () => {
    const componentToRender = routes[window.location.pathname];
    componentToRender();
  });
};

/**
 * Call and auto-render of Functional Components associated to the given URL
 * @param {*} uri - Provides an URL that is associated to a functional component
 * in the
 * routes array of the Router
 */
const Redirect = (uri) => {
  // use Web History API to add current page URL to the user's navigation
  // history & set right URL in the browser (instead of "#")
  window.history.pushState({}, uri, window.location.origin + uri);
  // render the requested component
  const componentToRender = routes[uri];
  if (routes[uri]) {
    componentToRender();
  } else {
    throw Error("La ressource " + uri + " n'existe pas.");
  }
};

/**
 * Redirect to another page adding information in URL.
 *
 * @param page path to destination (.e.g /registerPage)
 * @param action information to add (.e.g ?id=5)
 * @constructor
 */
const RedirectWithParamsInUrl = (page, action) => {
  window.history.pushState({}, page, window.location.origin + page
      + action);
  const componentToRender = routes[page];
  if (routes[page]) {
    componentToRender();
  } else {
    throw Error("La " + page + " n'existe pas");
  }
};

export {Router, Redirect, refreshToken, RedirectWithParamsInUrl};
