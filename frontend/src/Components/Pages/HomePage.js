import OfferLibrary from "../../Domain/OfferLibrary";
import {getSessionObject} from "../../utils/session";

const offerLibrary = new OfferLibrary();

/**
 * Render the HomePage
 */
const render = async () => {
  let lastOffers = await offerLibrary.getAllLastOffers();
  let isMemberConnected = getSessionObject("user");
  let actualOffer = 0;
  let lines;
  let nbOffers = lastOffers.length;
  if (lastOffers.length <= 3) {
    lines = 1;
  } else {
    lines = 2;
  }

  let page = "";
  for (let nbRows = 0; nbRows < lines; nbRows++) {
    page += `
      <div class="container-fluid align-content-center w-75 mt-3 mb-3">
        <div class="row row-cols-1 row-cols-md-3 g-4">`;

    //number max of columns =3
    let nbColumns = 0;
    //check if we still have offers and if the arrived to the end of row
    while (nbColumns < 3 && nbOffers !== 0) {
      page += `
        <div class="col">
          <div class="card ${isMemberConnected ? "clickable" : ""}" 
             data-element-id="
                    ${isMemberConnected ? lastOffers[actualOffer].idOffer : ""}"
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
                ${lastOffers[actualOffer].object.description}
              </p>
            </div>
          </div>
        </div>`;
      nbOffers--;
      nbColumns++;
      actualOffer++;
    }
    page += `
      </div>
    </div>`;
  }
  return page;
}
const HomePage = async () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = await render();

};

export default HomePage;
