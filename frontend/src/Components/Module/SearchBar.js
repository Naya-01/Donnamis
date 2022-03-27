import TypeLibrary from "../../Domain/TypeLibrary";

const searchBar = async (pageName, hasNav, hasFilter, hasType, placeholder,
    hasNewObjectButton) => {
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
    if (hasType) {
      searchBarHtml +=
          `<button class="input-group-text dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
                 Type
               </button>
               <ul id="default-type-list" class="dropdown-menu" aria-labelledby="dropdownMenuButton1">`;
      const types = await TypeLibrary.prototype.getAllDefaultTypes();
      for (const type of types.type) {
        searchBarHtml += `<li><a class="dropdown-item" href="#">${type.typeName}</a></li>`;
      }
      searchBarHtml += `</ul>`;
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