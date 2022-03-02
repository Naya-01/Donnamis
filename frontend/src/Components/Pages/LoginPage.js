const htmlPage = `
            <div class="container mt-5">
              <div class="border border-5 border-dark p-5">
                <div class="row mx-5">
                  <span class="px-5 vertical-text fs-1">Se connecter</span>
                </div>
                <div class="mx-5">
                  <form>
                    <div class="form-group">
                      <label>Email address</label>
                      <input class="form-control" placeholder="Enter email" type="email">
                    </div>
                    <div class="form-group mt-3">
                      <label>Password</label>
                      <input class="form-control" placeholder="Password" type="password">
                    </div>
                    <button class="btn btn-primary mt-3" type="submit">Submit</button>
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
