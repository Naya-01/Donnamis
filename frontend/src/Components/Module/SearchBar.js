import {getSessionObject} from "../../utils/session";

const searchBar = async () => {
  const pageDiv = document.querySelector("#page");
  let searchBar = `
    <form class="form row">
      <div class="input-group mb-3 col">
        <div class="input-group-prepend">
          <div class="dropdown">
            <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
              Type
            </button>
            <ul id="default-type-list" class="dropdown-menu" aria-labelledby="dropdownMenuButton1">`;

  const types = await getDefaultTypes();
  for (const type in types) {
    searchBar += `<li><a class="dropdown-item" href="#">${types[type].typeName}</a></li>`;
  }
  searchBar += `
            </ul>
          </div>
        </div>
        <input id="search-bar-input" type="text" class="form-control" aria-label="Text input with dropdown button">
        <div class="input-group-append">
          <button class="btn btn-outline-primary" type="button">Rechercher</button>
        </div>
      </div>
      <div class="col">
        <button type="submit" class="btn btn-primary mb-2">Ajouter un objet</button>
      </div>
    </form>
  `;
  pageDiv.innerHTML = searchBar;

};

const getDefaultTypes = async () => {
  try {
    let options = {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": getSessionObject("user").refreshToken
      },
    };
    let userData = await fetch(
        "/api/types/allDefault", options);
    if (!userData.ok) {
      return false;
    }
    return await userData.json();
  } catch (err) {
    console.log(err);
  }
}

export default searchBar;