package be.vinci.pae.ihm.manager;

import be.vinci.pae.business.domain.dto.MemberDTO;

public interface Token {

  /**
   * Make a token with a long expire date.
   *
   * @param memberDTO : member want to be authenticated
   * @return a token
   */
  String withRememberMe(MemberDTO memberDTO);

  /**
   * Make a token with a short expire date.
   *
   * @param memberDTO : member want to be authenticated
   * @return a token
   */
  String withoutRememberMe(MemberDTO memberDTO);

  /**
   * Verify a token given.
   *
   * @param token a string of the token
   * @return the member bound to the given token or null
   */
  MemberDTO verifyToken(String token);
}
