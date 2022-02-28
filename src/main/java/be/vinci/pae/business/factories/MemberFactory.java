package be.vinci.pae.business.factories;


import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.business.domain.dto.MemberDTO;

public class MemberFactory {

  /**
   * This function is used for the injection, it returns an implementation member.
   *
   * @return member implementation
   */
  public MemberDTO getMemberDTO() {
    return new MemberImpl();
  }

}
