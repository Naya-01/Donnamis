package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.ForbiddenException;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.dal.dao.MemberDAO;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import java.util.List;

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
   * Register a quidam.
   *
   * @param user : User object with all information.
   * @return token for the user.
   */
  @Override
  public ObjectNode register(MemberDTO user) {

    return null;
  }

  /**
   * Get all subscription requests according to their status.
   *
   * @param status the status subscription members
   * @return a list of memberDTO
   */
  @Override
  public List<MemberDTO> getInscriptionRequest(String status) {
    List<MemberDTO> memberDTOList = memberDAO.getAllWithSubStatus(status);
    if (memberDTOList == null || memberDTOList.isEmpty()) {
      throw new NotFoundException("Aucune requête d'inscription");
    }
    return memberDTOList;
  }
}