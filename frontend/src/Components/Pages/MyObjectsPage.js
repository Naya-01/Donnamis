import {getSessionObject} from "../../utils/session";
import {Redirect} from "../Router/Router";
import searchBar from "../Module/SearchBar";

/**
 * Render the page to see an object
 */
const MyObjectsPage = async () => {
  if (!getSessionObject("user")) {
    Redirect("/");
    return;
  }

  await searchBar("Mes objets", false, true, "Recherche un objet", true);


  const pageDiv = document.querySelector("#page");

}

export default MyObjectsPage;