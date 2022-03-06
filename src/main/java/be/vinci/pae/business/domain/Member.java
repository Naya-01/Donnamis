package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.MemberDTO;

public interface Member extends MemberDTO {

  /**
   * Check the password of the member.
   *
   * @param password : password of the member that need to be checked
   */
  boolean checkPassword(String password);

  /**
   * Hash the password of the member.
   *
   * @param password : password of the member that need to be hashed
   */
  String hashPassword(String password);

}
