package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.dal.dao.MemberDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;

public class MemberUCCImpl implements MemberUCC {

  @Inject
  private MemberDAO memberDAO;

  /**
   * Log in a quidam by a username and a password.
   *
   * @param username : username of the member.
   * @param password : password of the member.
   * @return member having the username and password.
   */
  @Override
  public MemberDTO login(String username, String password) {
    MemberDTO memberDTO = memberDAO.getOne(username);
    Member member = (Member) memberDTO;
    if (memberDTO == null) {
      throw new NotFoundException("Member not found");
    }
    if (!member.checkPassword(password)) {
      throw new ForbiddenException("Password invalid");
    }
    if (memberDTO.getStatus().equals("refused")) {
      throw new UnauthorizedException("Member status is refused");
    }
    return memberDTO;

  }

  /**
   * @param id : id of the member.
   * @return memberDTO having this id.
   */
  public MemberDTO getMember(int id) {
    MemberDTO memberDTO = memberDAO.getOne(id);
    if (memberDTO == null) {
      throw new NotFoundException("Member not found");
    }
    return memberDTO;
  }
}