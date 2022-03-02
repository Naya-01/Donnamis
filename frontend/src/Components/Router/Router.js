import HomePage from "../Pages/HomePage";
import LoginPage from "../Pages/LoginPage";
import Logout from "../Logout/Logout";

// Configure your routes here
const routes = {
  "/": HomePage,
  "/connexion": LoginPage,
  "/deconnexion": Logout,
};

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

export {Router, Redirect};
