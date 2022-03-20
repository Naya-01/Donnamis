package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.ConflictException;
import be.vinci.pae.business.exceptions.ForbiddenException;
import be.vinci.pae.business.exceptions.InternalServerErrorException;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.util.List;

public class MemberUCCImpl implements MemberUCC {

  @Inject
  private MemberDAO memberDAO;
  @Inject
  private AddressDAO addressDAO;
  @Inject
  private DALService dalService;

  /**
   * Log in a quidam by a username and a password.
   *
   * @param username : username of the member.
   * @param password : password of the member.
   * @return member having the username and password.
   */
  @Override
  public MemberDTO login(String username, String password) {
    MemberDTO memberDTO = null;
    try {
      dalService.startTransaction();
      memberDTO = memberDAO.getOne(username);
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
    } catch (NotFoundException | ForbiddenException | UnauthorizedException e) {
      dalService.rollBackTransaction();
      e.printStackTrace();
    }
    dalService.commitTransaction();
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
   * Confirm the registration of the member with his id.
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
   * Decline the registration of a member with his id and the reason.
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
   * Promote the member with his id to the admin status.
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

  /*
   * Register a quidam.
   *
   * @param memberDTO : User object with all information.
   * @return token for the user.
   */
  @Override
  public MemberDTO register(MemberDTO memberDTO) {
    //check if the member already exists
    MemberDTO memberExistent = memberDAO.getOne(memberDTO.getUsername());
    if (memberExistent != null) {
      throw new ConflictException("Ce membre existe déjà");
    }

    //set the MemberDTO
    Member member = (Member) memberDTO;
    memberDTO.setPassword(
        member.hashPassword(memberDTO.getPassword())); //hashPassword of the member
    memberDTO.setStatus("pending");
    memberDTO.setRole("member");
    memberDTO.setReasonRefusal(null);

    //add the member
    MemberDTO memberFromDao = memberDAO.createOneMember(memberDTO);
    if (memberFromDao == null) {
      throw new InternalServerErrorException("Le membre n'a pas pû être ajouté à la base de"
          + " données");
    }

    AddressDTO addressOfMember = memberDTO.getAddress();
    //add the address
    if (addressOfMember.getUnitNumber() != null && addressOfMember.getUnitNumber().isBlank()) {
      addressOfMember.setUnitNumber(null);
    }
    addressOfMember.setIdMember(memberFromDao.getMemberId());
    //add the address
    AddressDTO addressDTO = addressDAO.createOne(addressOfMember);
    if (addressDTO == null) {
      throw new InternalServerErrorException("L'adresse n'a pas pû être ajoutée à la base de"
          + " données");
    }
    memberFromDao.setAddress(addressDTO);
    return memberFromDao;
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