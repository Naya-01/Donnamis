const searchBar = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = `
    <form class="form row">
      <div class="input-group mb-3 col">
        <div class="input-group-prepend">
          <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Type</button>
          <div class="dropdown-menu">
            <a class="dropdown-item" href="#">Action</a>
            <a class="dropdown-item" href="#">Another action</a>
            <a class="dropdown-item" href="#">Something else here</a>
            <div role="separator" class="dropdown-divider"></div>
            <a class="dropdown-item" href="#">Separated link</a>
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