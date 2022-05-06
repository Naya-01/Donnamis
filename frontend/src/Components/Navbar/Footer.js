const Footer = () => {
  const footerWrapper = document.querySelector("#footer");
  footerWrapper.className = "d-flex flex-wrap justify-content-between align-items-center py-3 mt-5 border-top bg-navbar";
  footerWrapper.innerHTML = `
      <div class="col-md-4 d-flex align-items-center">
        <a href="/" class="mb-3 me-2 mb-md-0 text-muted text-decoration-none lh-1">
          <svg class="bi" width="30" height="24"><use xlink:href="#bootstrap"></use></svg>
        </a>
        <span class="text-muted">© 2022 Donnamis PAE Project</span>
      </div>
  `;
}

export default Footer;