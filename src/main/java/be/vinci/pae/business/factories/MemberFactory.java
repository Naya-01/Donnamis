package be.vinci.pae.business.factories;


import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.business.domain.dto.MemberDTO;

public class MemberFactory {

  public MemberDTO getMemberDTO() {
    return new MemberImpl();
  }

}
