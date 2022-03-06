package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.MemberDTO;

public interface MemberFactory {

  /**
   * This function is used for the injection, it returns an implementation member.
   *
   * @return member implementation
   */
  MemberDTO getMemberDTO();
}
