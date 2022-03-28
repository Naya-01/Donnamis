import SearchBar from "../Module/SearchBar";
import MemberLibrary from "../../Domain/MemberLibrary";
import ManagementList from "../Module/ManagementList";
import profileImage from "../../img/profil.png"

/**
 * Render the Members page
 */
const MembersPage = async () => {
  await SearchBar("Membres", true, false, false, "Rechercher un membre", false, false);

  let members = await MemberLibrary.prototype.getMemberBySearchAndStatus("","valid");

  baseMembersList(members);

  // Search members by enter
  const searchBar = document.getElementById("searchBar");
  searchBar.addEventListener("keypress", async (e) => {
    if (e.key === "Enter") {
      members = await MemberLibrary.prototype.getMemberBySearchAndStatus(searchBar.value, "valid");
      await baseMembersList(members);
    }
  });

  // Search members by click
  const search = document.getElementById("searchButton");
  search.addEventListener("click", async () => {
    members = await MemberLibrary.prototype.getMemberBySearchAndStatus(
        searchBar.value, "valid");
    await baseMembersList(members);
  });

}

const baseMembersList = (members) => {
  // Create member cards
  const memberCards = document.getElementById("page-body");
  memberCards.innerHTML = ``;
  for (const member of members) {

    ManagementList(member.memberId, document.getElementById("page-body"), profileImage,
        member.firstname + " " + member.lastname + " (" + member.username + ")",
        member.address.buildingNumber + " " + member.address.street + " " +
        member.address.postcode + " " + member.address.commune + " " + member.address.country)

    // Show different buttons card depending on status
    if (member.status === "denied") {
    } else {
    }
  }
}

export default MembersPage;