const pageRender = () => {
  let page = `
    <div class="container mt-5">
      <p>Salut</p>
    </div>`;

  return page;
}

const ProfilPage = () => {

  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = pageRender();

}

export default ProfilPage;