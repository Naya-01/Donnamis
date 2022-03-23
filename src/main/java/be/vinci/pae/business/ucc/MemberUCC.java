package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.MemberDTO;
import java.util.List;

public interface MemberUCC {

  /**
   * Log in a quidam by a username and a password.
   *
   * @param username : username of the member.
   * @param password : password of the member.
   * @return member having the username and password.
   */
  MemberDTO login(String username, String password);

  /**
   * Find a member with his id.
   *
   * @param id : id of the member.
   * @return memberDTO having this id.
   */
  MemberDTO getMember(int id);

  /**
   * Register a quidam.
   *
   * @param memberDTO : User object with all information.
   * @return token for the user.
   */
  MemberDTO register(MemberDTO memberDTO);

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members, pending -> pending members,
   *               denied -> denied members, valid -> valid members, empty -> all members
   * @return a list of MemberDTO
   */
  List<MemberDTO> searchMembers(String search, String status);

  /**
   * Update any attribute of a member.
   *
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  MemberDTO updateMember(MemberDTO memberDTO);
}
