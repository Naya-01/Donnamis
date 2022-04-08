import noImage from "../../img/noImage.png";

const pageRender = () => {
  let image = noImage;
  let page = `
    <div class="container mt-5">
      <div class="text-center"><img src="${image}" width="15%"></div>
    </div>`;

  return page;
}

const ProfilPage = () => {

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = pageRender();

}

export default ProfilPage;