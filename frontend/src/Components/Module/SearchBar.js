import TypeLibrary from "../../Domain/TypeLibrary";

/**
 * A module to display a search page
 *
 * @param pageName the name of the page (display it h1)
 * @param hasNav if you want a searchbar
 * @param hasFilter boolean if there is filter for member status
 * @param hasType if has type dropdown selector
 * @param placeholder the bar search placeholder
 * @param hasNewObjectButton if you want a button next to the navbar
 * @param hasStatus boolean if there is filter for offer status
 * @returns {Promise<void>}
 */
const searchBar = async (pageName, hasNav, hasFilter, hasType, placeholder,
    hasNewObjectButton, hasStatus) => {
  const pageDiv = document.querySelector("#page");
  let searchBarHtml = ``;
  searchBarHtml = `
    <div class="container mt-5">
      <h1 class="fs-1">${pageName}</h1>`;
  if (hasNav) {
    searchBarHtml +=
        `<div class="text-center">
          <div class="input-group mb-3 mt-5" >`;

    if (hasFilter) {
      searchBarHtml +=
          `<div class="btn-group mx-2" role="group" aria-label="Basic radio toggle button group">
              <input type="radio" class="btn-check" checked name="btnradio" id="btn-radio-all" autocomplete="off">
              <label class="btn btn-outline-secondary" for="btn-radio-all">Tous</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-radio-pending" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-radio-pending">En attente</label>
            
              <input type="radio" class="btn-check" name="btnradio" id="btn-radio-denied" autocomplete="off">
              <label class="btn btn-outline-danger" for="btn-radio-denied">Refusé</label>
            </div>`;
    }
    if (hasStatus) {
      searchBarHtml += `<div class="btn-group mx-2" role="group" aria-label="Basic radio toggle button group">
              <input type="radio" class="btn-check" checked name="btnradio" id="btn-status-all" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-all">Tous</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-available" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-available">Publié</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-interested" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-interested">Intéressé</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-assigned" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-assigned">Attribué</label>
            
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-given" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-given">Donné</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-cancelled" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-cancelled">Annulé</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-not_collected" autocomplete="off">
              <label class="btn btn-outline-dark" for="btn-status-not_collected">Non récupéré</label>
              
            </div>`;
    }
    if (hasType) {
      searchBarHtml +=
          `<select  id="default-type-list" class="input-group-text form-select">
  <option value="Tout" selected>Tout les types</option>`
      const types = await TypeLibrary.prototype.getAllDefaultTypes();
      for (const type of types.type) {
        searchBarHtml += `<option class="dropdown-item" value="${type.typeName}" href="#"> ${type.typeName} </option>`;
      }
      searchBarHtml += `</select>`

    }

    searchBarHtml +=
        `<input type="text" class="form-control fs-4" id="searchBar" placeholder="${placeholder}">
            <button class="btn btn-outline-primary fs-4" id="searchButton" type="button">Rechercher</button>`
    if (hasNewObjectButton) {
      searchBarHtml += `<button id="add-new-object-button" type="submit" class="btn btn-primary mx-2">Ajouter un objet</button>`;
    }
    searchBarHtml += `</div>
        <div id="page-body"></div>
      </div>`;
  }
  searchBarHtml += `</div>`;
  pageDiv.innerHTML = searchBarHtml;
};

export default searchBar;