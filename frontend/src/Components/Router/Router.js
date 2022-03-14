import HomePage from "../Pages/HomePage";
import LoginPage from "../Pages/LoginPage";
import Logout from "../Logout/Logout";
import AddNewObjectPage from "../Pages/AddNewObjectPage";
import Navbar from "../Navbar/Navbar";
import MyObjectPage from "../Pages/MyObjectPage";
import {
  getSessionObject,
  removeSessionObject,
  setSessionObject
} from "../../utils/session";
import AllObjectsPage from "../Pages/AllObjectsPage";
import ObjectDetails from "../Pages/ObjectDetails";
import RegisterPage from "../Pages/RegisterPage";

// Configure your routes here
const routes = {
  "/": HomePage,
  "/objects": AllObjectsPage,
  "/detailsObject": ObjectDetails,
  "/login": LoginPage,
  "/logout": Logout,
  "/register": RegisterPage,
  "/addNewObjectPage" : AddNewObjectPage,
  "/myObjectPage": MyObjectPage
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
    Redirect("/login");
    return false;
  }
}

/**
 * Deal with call and auto-render of Functional Components following click events
 * on Navbar, Load / Refresh operations, Browser history operation (back or next) or redirections.
 * A Functional Component is responsible to auto-render itself : Pages, Header...
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
 * @param {*} uri - Provides an URL that is associated to a functional component in the
 * routes array of the Router
 */
const Redirect = (uri) => {
  // use Web History API to add current page URL to the user's navigation history & set right URL in the browser (instead of "#")
  window.history.pushState({}, uri, window.location.origin + uri);
  // render the requested component
  const componentToRender = routes[uri];
  if (routes[uri]) {
    componentToRender();
  } else {
    throw Error("La ressource " + uri + " n'existe pas.");
  }
};

export {Router, Redirect, refreshToken};
