
let page = `
<div class="container-fluid align-content-center w-50 mt-5">
<h2>Objet donnés par Rayan</h2>
<div class="card mb-3">
  <svg class="bd-placeholder-img card-img-top" width="100%" height="180" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid slice" focusable="false"><title>Placeholder</title><rect width="100%" height="100%" fill="#868e96"></rect><text x="50%" y="50%" fill="#dee2e6" dy=".3em">Image cap</text></svg>

  <div class="card-body">
    <div class="row mt-1">
      <div class="col">
      <h5 class="card-title">Description </h5>
      <p class="text-secondary">objet.description</p>
      </div>
      <div class="col">
        <h5 class="card-title">Plage horaire </h5>
        <p class="text-secondary">offer.time_slot</p>
      </div>
    </div>
    
    <div class="row mt-1">
      <div class="col">
      <h5 class="card-title">Etat de l'objet </h5>
      <p class="text-secondary">objet.etat</p>
      </div>
      <div class="col">
        <h5 class="card-title">Type </h5>
        <p class="text-secondary">objet.type</p>
      </div>
    </div>
    
    <div class="row mt-1">
      <div class="container-fluid text-center">
          <button type="button" class="btn btn-primary w-auto">Je suis interessé</button>
          <p class="text-secondary">nb personne interessé</p>
      </div>
    </div>
    <div class="row mt-1">
      <div class="container text-end">
            <button type="button" class="btn btn-primary w-auto">Voir l'historique de l'offre</button>
        </div>
    </div>
  </div>
</div>
</div>
`;

const ObjectDetails = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = page;
};

export default ObjectDetails;
