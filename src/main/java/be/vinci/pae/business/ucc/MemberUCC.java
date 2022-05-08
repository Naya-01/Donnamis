package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.MemberDTO;
import java.awt.image.BufferedImage;
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
   * Update the profil picture of the member.
   *
   * @param path    of the picture
   * @param id      of the member
   * @param version of the member
   * @return memberDTO updated
   */
  MemberDTO updateProfilPicture(String path, int id, Integer version);

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
   * Get the picture of an object.
   *
   * @param id of the oject
   * @return picture as file
   */
  BufferedImage getPicture(int id);

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
   * Update one or many attribute(s) of a member.
   *
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  MemberDTO updateMember(MemberDTO memberDTO);

  /**
   * Update a member status and update its assigned interests into a prevent status.
   *
   * @param memberDTO member who has a prevent
   * @return the member updated with a prevent status
   */
  MemberDTO preventMember(MemberDTO memberDTO);
}
