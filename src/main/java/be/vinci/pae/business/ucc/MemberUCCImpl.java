package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.ForbiddenException;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.dal.dao.MemberDAO;
import jakarta.inject.Inject;

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
      throw new NotFoundException("Membre non trouvé");
    }
    if (!member.checkPassword(password)) {
      throw new ForbiddenException("Mot de passe invalide");
    }
    if (memberDTO.getStatus().equals("denied")) {
      throw new UnauthorizedException(
          "Votre inscription est refusé pour la raison suivante : " + member.getReasonRefusal());
    }
    if (memberDTO.getStatus().equals("pending")) {
      throw new UnauthorizedException("Le statut du membre est en attente");
    }
    return memberDTO;

  }

  /**
   * Find a member with his id.
   *
   * @param id : id of the member.
   * @return memberDTO having this id.
   */
  @Override
  public MemberDTO getMember(int id) {
    MemberDTO memberDTO = memberDAO.getOne(id);
    if (memberDTO == null) {
      throw new NotFoundException("Member not found");
    }
    return memberDTO;
  }

  /**
   * Confirm the registration of the member with his id
   *
   * @param id of the member
   */
  @Override
  public void confirmRegistration(int id) {
    MemberDTO memberDTO = memberDAO.getOne(id);
    if (memberDTO.getStatus().equals("denied")) {
      memberDAO.confirmDeniedMemberRegistration(id);
    } else {
      memberDAO.confirmRegistration(id);
    }

  }

  /**
   * Decline the registration of a member with his id and the reason
   *
   * @param id     of the member
   * @param reason for denial
   */
  @Override
  public void declineRegistration(int id, String reason) {
    MemberDTO memberDTO = memberDAO.getOne(id);
    if (memberDTO.getStatus().equals("valid")) {
      throw new UnauthorizedException("Vous ne pouvez pas modifier un membre déjà validé");
    }
    memberDAO.declineRegistration(id, reason);
  }

  /**
   * Promote the member with his id to the admin status
   *
   * @param id of the member
   */
  @Override
  public void promoteAdministrator(int id) {
    MemberDTO memberDTO = memberDAO.getOne(id);
    if (memberDTO.getStatus().equals("administrator")) {
      // Check if the exception is the good one
      throw new ForbiddenException("Already administrator");
    }
    memberDAO.promoteAdministrator(id);
  }
}