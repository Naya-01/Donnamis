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
   * @param id : id of the member.
   * @return memberDTO having this id.
   */
  MemberDTO getMember(int id);
}
