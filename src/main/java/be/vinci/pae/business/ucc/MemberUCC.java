package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.MemberDTO;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
   * @param user : User object with all information.
   * @return token for the user.
   */
  ObjectNode register(MemberDTO user);

  /**
   * Get all subscription requests according to their status.
   *
   * @param status the status subscription members
   * @return a list of memberDTO
   */
  List<MemberDTO> getInscriptionRequest(String status);
}
