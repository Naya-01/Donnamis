import {getSessionObject} from "../../utils/session";

const cardList = async (offers) => {
  let isMemberConnected = getSessionObject("user");
  let nbOffers = 0;

  let page = "";
  for (let i = 0; i < Math.ceil(offers.length / 3); i++) {
    page += `
      <div class="container-fluid align-content-center w-75 mt-3 mb-3">
        <div class="row row-cols-1 row-cols-md-3 g-4">`;

    let cnt = 1;
    while (cnt <= 3 && nbOffers < offers.length) {
      page += `
        <div class="col">
          <div class="card ${isMemberConnected ? "clickable" : ""}" 
             data-element-id="
                    ${isMemberConnected ? offers[nbOffers].idOffer : ""}"
             >
            <svg class="bd-placeholder-img card-img-top" width="100%" 
              height="180" xmlns="http://www.w3.org/2000/svg" role="img" 
              aria-label="Placeholder: Image cap" preserveAspectRatio="xMidYMid 
              slice" focusable="false"><title>Placeholder</title>
              <rect width="100%" height="100%" fill="#868e96"></rect><text 
              x="50%" y="50%" fill="#dee2e6" dy=".3em">Image cap</text>
            </svg>
            
            <div class="card-body">
              <p class="card-text">
                ${offers[nbOffers].object.description}
              </p>
            </div>
          </div>
        </div>`;
      nbOffers++;
      cnt++;
    }
    page += `
      </div>
    </div>`;
  }
  return page;
}

export default cardList;