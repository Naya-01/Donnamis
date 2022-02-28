package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.MemberDTO;

public interface MemberUCC {

  /**
   * Log in a quidam by a pseudo and a password.
   *
   * @param pseudo   : pseudo of the member.
   * @param password : password of the member.
   * @return member having the pseudo and password.
   */
  MemberDTO login(String pseudo, String password);
}
