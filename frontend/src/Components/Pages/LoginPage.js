const htmlPage = `
            <div class="container mt-5">
              <div class="border border-5 border-dark p-5">
                <div class="row mx-5">
                  <span class="px-5 vertical-text fs-1">Se connecter</span>
                </div>
                <div class="mx-5">
                  <form>
                    <div class="form-group">
                      <label>Pseudonyme</label>
                      <input class="form-control" placeholder="Entrez votre pseudonyme" type="email">
                    </div>
                    <div class="form-group mt-3">
                      <label>Mot de passe</label>
                      <input class="form-control" placeholder="Entrez votre mot de passe" type="password">
                    </div>
                    <div class="mt-3 form-check">
                      <input type="checkbox" class="form-check-input">
                      <label class="form-check-label">Se souvenir de moi</label>
                    </div>
                    <button class="btn btn-primary mt-3" type="submit">Se connecter</button>
                  </form>
                </div>
              </div>
            </div>
                  `;

const LoginPage = () => {
  const pageDiv = document.querySelector("#page");
  pageDiv.innerHTML = htmlPage;
};

export default LoginPage;
