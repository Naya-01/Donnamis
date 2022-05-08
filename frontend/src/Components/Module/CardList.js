import {getSessionObject} from "../../utils/session";
import noImage from "../../img/noImage.png";

//translation map depending on the offer status
const dictionary = new Map([
  ['interested', 'Intéressé'],
  ['available', 'Publié'],
  ['assigned', 'Attribué'],
  ['given', 'Donné'],
  ['cancelled', 'Annulé'],
  ['not_collected', 'Non récupéré']
]);

//color map depending on the offer status
const dictionaryColorStatus = new Map([
  ['interested', 'greenColor'],
  ['available', 'greenColor'],
  ['assigned', 'yellowColor'],
  ['given', ''],
  ['cancelled', 'redColor'],
  ['not_collected', 'redColor']
]);

/**
 * Make a table of card (offers)
 *
 * @param offers all offers data
 * @returns {Promise<string>}
 */
const cardList = async (offers) => {
  let isMemberConnected = getSessionObject("user");
  let nbOffers = 0;
  let defaultImage = noImage;
  let image;

  let page = "<div class='mt-5'>";
  //make rows of 3 columns
  for (let i = 0; i < Math.ceil(offers.length / 3); i++) {
    page += `
      <div class="container-fluid align-content-center w-75 mt-3 mb-3">
        <div class="row row-cols-1 row-cols-md-3 g-4">`;

    let cnt = 1;
    //make the columns for the row
    while (cnt <= 3 && nbOffers < offers.length) {
      //if the offer has an image
      if (offers[nbOffers].object.image) {
        let imageObject = "/api/object/getPicture/"
            + offers[nbOffers].object.idObject;
        image = imageObject;
      } //otherwise
      else {
        image = defaultImage;
      }
      //card content
      page += `
        <div class="col">
          <div class="card ${isMemberConnected ? "clickable" : ""}" 
             data-element-id="
                    ${isMemberConnected ? offers[nbOffers].idOffer : ""}">
            <img src="${image}" height="250px" >
            <div class="card-body">
              <h6 class="card-subtitle mb-2 
                ${dictionaryColorStatus.get(offers[nbOffers].status)}">
                    ${dictionary.get(offers[nbOffers].status)}
              </h6>
              <p class="card-text nowrap-class">
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