package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.ForbiddenException;
import be.vinci.pae.business.exceptions.InternalServerErrorException;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import jakarta.inject.Inject;
import java.util.List;

public class MemberUCCImpl implements MemberUCC {

  @Inject
  private MemberDAO memberDAO;

  @Inject
  private AddressDAO addressDAO;

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
   * @param memberDTO : User object with all information.
   * @return token for the user.
   */
  @Override
  public MemberDTO register(MemberDTO memberDTO) {
    //check if the member already exists
    MemberDTO memberExistent = memberDAO.getOne(memberDTO.getUsername());
    if (memberExistent != null) {
      return null;
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
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members pending -> pending members
   *               denied -> denied members valid -> valid members empty -> all members
   * @return a list of MemberDTO
   */
  @Override
  public List<MemberDTO> searchMembers(String search, String status) {
    List<MemberDTO> memberDTOList = memberDAO.getAll(search, status);
    if (memberDTOList == null || memberDTOList.isEmpty()) {
      throw new NotFoundException("Aucun membre");
    }
    return memberDTOList;
  }

  /**
   * Update any attribute of a member.
   *
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  @Override
  public MemberDTO updateMember(MemberDTO memberDTO) {
    MemberDTO modifierMemberDTO = memberDAO.updateOne(memberDTO);
    if (modifierMemberDTO == null) {
      throw new ForbiddenException("Problem with updating member");
    }
    return modifierMemberDTO;
  }
}