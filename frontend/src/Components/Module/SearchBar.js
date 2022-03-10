const searchBar = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <form class="form row">
      <div class="input-group mb-3 col">
        <div class="input-group-prepend">
          <div class="dropdown">
            <button class="btn btn-outline-secondary dropdown-toggle" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown" aria-expanded="false">
              Type
            </button>
            <ul class="dropdown-menu" aria-labelledby="dropdownMenuButton1">
              <li><a class="dropdown-item" href="#">Action</a></li>
              <li><a class="dropdown-item" href="#">Another action</a></li>
              <li><a class="dropdown-item" href="#">Something else here</a></li>
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

};

export default searchBar;