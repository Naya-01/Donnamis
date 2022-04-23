import {getSessionObject} from "../../utils/session";
import noImage from "../../img/noImage.png";

const dictionnary = new Map([
  ['interested', 'Disponible'],
  ['available', 'Disponible'],
  ['assigned', 'En cours de donnation'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé']
]);

const dictionnaryColorStatus = new Map([
  ['interested', 'greenColor'],
  ['available', 'greenColor'],
  ['assigned', 'yellowColor'],
  ['given', ''],
  ['cancelled', 'redColor']
]);

const cardList = async (offers) => {
  let isMemberConnected = getSessionObject("user");
  let nbOffers = 0;
  let defaultImage = noImage;
  let image;

  console.log(offers);
  let page = "<div class='mt-5'>";
  for (let i = 0; i < Math.ceil(offers.length / 3); i++) {
    page += `
      <div class="container-fluid align-content-center w-75 mt-3 mb-3">
        <div class="row row-cols-1 row-cols-md-3 g-4">`;

    let cnt = 1;
    while (cnt <= 3 && nbOffers < offers.length) {
      if (offers[nbOffers].object.image) {
        let imageObject = "/api/object/getPicture/"
            + offers[nbOffers].object.idObject;
        image = imageObject;
      } else {
        image = defaultImage;
      }
      page += `
        <div class="col">
          <div class="card ${isMemberConnected ? "clickable" : ""}" 
             data-element-id="
                    ${isMemberConnected ? offers[nbOffers].idOffer : ""}">
            <img src="${image}" height="250px" >
            <div class="card-body">
              <h6 class="card-subtitle mb-2 
                ${dictionnaryColorStatus.get(offers[nbOffers].status)}">
                    ${dictionnary.get(offers[nbOffers].status)}
              </h6>
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
    </div>
</div>`;
  }
  return page;
}

export default cardList;