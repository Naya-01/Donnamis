/**
 * Render the Objects page
 */

import searchBar from "../Module/SearchBar";

const AllObjectsPage = () => {
  const pageDiv = document.querySelector("#page");

  searchBar();
  pageDiv.innerHTML += `
    
  `;
};

export default AllObjectsPage;
