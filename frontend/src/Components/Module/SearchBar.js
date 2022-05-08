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
 * @param hasDate if the searchbar has date
 */
const searchBar = async (pageName, hasNav, hasFilter, hasType, placeholder, hasNewObjectButton, hasStatus, hasDate = false) => {
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
          `<div class="btn-group-sm mx-2" role="group" aria-label="Basic radio toggle button group">
              <input type="radio" class="btn-check" checked name="btnradio" id="btn-radio-all" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-radio-all">Tous</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-radio-pending" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-radio-pending">En attente</label>
              <br>
              <input type="radio" class="btn-check" name="btnradio" id="btn-radio-denied" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-radio-denied">Refusé</label>
            </div>`;
    }
    if (hasStatus) {
      searchBarHtml += `
            <div class="btn-group-sm mx-2" role="group" aria-label="Basic radio toggle button group">
              <input type="radio" class="btn-check" checked name="btnradio" id="btn-status-all" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-all">Tous</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-available" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-available">Publié</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-interested" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-interested">Intéressé</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-assigned" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-assigned">Attribué</label>
            <br>
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-given" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-given">Donné</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-cancelled" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-cancelled">Annulé</label>
              
              <input type="radio" class="btn-check" name="btnradio" id="btn-status-not_collected" autocomplete="off">
              <label class="btn-sm btn-outline-secondary" for="btn-status-not_collected">Non récupéré</label>
              
            </div>
        `;
    }

    if (hasDate) {
      searchBarHtml += `
        <input class="form-control"  id="date" name="date" placeholder="JJ/MM/AAAA" type="text" style="max-width: 120px"/>
      `;
    }

    if (hasType) {
      searchBarHtml +=
          `
<!--<div class="dropdown">-->
<!--  <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">-->
<!--    Dropdown button-->
<!--  </button>-->
<!--  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">-->
<!--    <a class="dropdown-item" href="#">Action</a>-->
<!--    <a class="dropdown-item" href="#">Another action</a>-->
<!--    <a class="dropdown-item" href="#">Something else here</a>-->
<!--  </div>-->
<!--</div>-->

<button class="input-group-text dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
                 Type
               </button>
               <select id="default-type-list" class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
                    <option class="dropdown-item" value="Tout" href="#"> Tout </option>`;
      const types = await TypeLibrary.prototype.getAllDefaultTypes();
      for (const type of types.type) {
        searchBarHtml += `<option class="dropdown-item" value="${type.typeName}" href="#"> ${type.typeName} </option>`;
      }
      searchBarHtml += `</select>`;
    }

    searchBarHtml +=
        `<input type="text" class="form-control" id="searchBar" placeholder="${placeholder}">
            <button class="btn btn-outline-secondary fs-4" id="searchButton" type="button">Rechercher</button>`
    if (hasNewObjectButton) {
      searchBarHtml += `<button id="add-new-object-button" type="submit" class="btn btn-secondary mx-2">Ajouter un objet</button>`;
    }
    searchBarHtml += `</div>
        <div id="page-body"></div>
      </div>`;
  }
  searchBarHtml += `</div>`;
  pageDiv.innerHTML = searchBarHtml;
};

export default searchBar;