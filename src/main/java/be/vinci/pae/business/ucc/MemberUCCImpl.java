package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.dal.dao.MemberDAO;
import jakarta.inject.Inject;

public class MemberUCCImpl implements MemberUCC {

  @Inject
  private MemberDAO memberDAO;

  /**
   * Log in a quidam by a pseudo and a password.
   *
   * @param pseudo   : pseudo of the member.
   * @param password : password of the member.
   * @return member having the pseudo and password.
   */
  @Override
  public MemberDTO login(String pseudo, String password) {
    MemberDTO memberDTO = memberDAO.getOne(pseudo);

    Member member = (Member) memberDTO;
    if (memberDTO == null || !member.checkPassword(password) || memberDTO.getStatus()
        .equals("refused")) {
      return null;
    }
    return memberDTO;

  }
}