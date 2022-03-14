package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.MemberDTO;

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
   * Confirm the registration of the member with his id.
   *
   * @param id of the member
   */
  void confirmRegistration(int id);

  /**
   * Decline the registration of a member with his id and the reason.
   *
   * @param id     of the member
   * @param reason for denial
   */
  void declineRegistration(int id, String reason);


  /**
   * Promote the member with his id to the admin status.
   *
   * @param id of the member
   */
  void promoteAdministrator(int id);
}
