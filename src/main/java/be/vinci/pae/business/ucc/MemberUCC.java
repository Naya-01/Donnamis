package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.dal.dao.MemberDAO;

public class MemberUCC {

  private MemberDAO memberDAO = new MemberDAO();

  /**
   * Log in a quidam by a pseudo and a password.
   *
   * @param pseudo   : pseudo of the member.
   * @param password : password of the member.
   * @return member having the pseudo and password.
   */
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