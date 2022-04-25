package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ConflictException;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;


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
    try {
      dalService.startTransaction();
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
      dalService.commitTransaction();
      return memberDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }

  }

  /**
   * Update the profil picture of the member.
   *
   * @param path of the picture
   * @param id   of the member
   * @return memberDTO updated
   */
  @Override
  public MemberDTO updateProfilPicture(String path, int id) {
    try {
      dalService.startTransaction();
      MemberDTO memberDTO = memberDAO.getOne(id);
      if (memberDTO == null) {
        throw new NotFoundException("Member not found");
      }
      memberDTO.setAddress(addressDAO.getAddressByMemberId(id));

      File f = new File(Config.getProperty("ImagePath") + memberDTO.getImage());
      if (f.exists()) {
        f.delete();
      }

      memberDTO = memberDAO.updateProfilPicture(path, id);
      dalService.commitTransaction();
      return memberDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Find a member with his id.
   *
   * @param id : id of the member.
   * @return memberDTO having this id.
   */
  @Override
  public MemberDTO getMember(int id) {
    try {
      dalService.startTransaction();
      MemberDTO memberDTO = memberDAO.getOne(id);
      if (memberDTO == null) {
        throw new NotFoundException("Member not found");
      }
      memberDTO.setAddress(addressDAO.getAddressByMemberId(id));
      dalService.commitTransaction();
      return memberDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Register a quidam.
   *
   * @param memberDTO : User object with all information.
   * @return token for the user.
   */
  @Override
  public MemberDTO register(MemberDTO memberDTO) {
    MemberDTO memberFromDao;
    try {
      dalService.startTransaction();

      memberDTO.setUsername(memberDTO.getUsername().replaceAll(" ", ""));

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
      memberDTO.setImage(null);
      if (memberDTO.getPhone() != null && memberDTO.getPhone().isBlank()) {
        memberDTO.setPhone(null);
      }

      //add the member
      memberFromDao = memberDAO.createOneMember(memberDTO);
      if (memberFromDao == null) {
        throw new FatalException("Le membre n'a pas pu être ajouté à la base de données");
      }

      AddressDTO addressOfMember = memberDTO.getAddress();
      if (addressOfMember.getUnitNumber() != null && addressOfMember.getUnitNumber().isBlank()) {
        addressOfMember.setUnitNumber(null);
      }
      addressOfMember.setIdMember(memberFromDao.getMemberId());
      //add the address
      AddressDTO addressDTO = addressDAO.createOne(addressOfMember);
      if (addressDTO == null) {
        throw new FatalException("L'adresse n'a pas pu être ajoutée à la base de données");
      }
      memberFromDao.setAddress(addressDTO);
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return memberFromDao;
  }

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members. pending -> pending members.
   *               denied -> denied members. valid -> valid members. empty -> all members.
   * @return a list of MemberDTO
   */
  @Override
  public List<MemberDTO> searchMembers(String search, String status) {
    try {
      dalService.startTransaction();
      List<MemberDTO> memberDTOList = memberDAO.getAll(search, status);
      if (memberDTOList == null || memberDTOList.isEmpty()) {
        throw new NotFoundException("Aucun membre");
      }
      dalService.commitTransaction();
      return memberDTOList;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }


  /**
   * Get the picture of an object.
   *
   * @param id of the oject
   * @return picture as file
   */
  public BufferedImage getPicture(int id) {
    MemberDTO memberDTO;
    BufferedImage picture;
    try {
      dalService.startTransaction();
      memberDTO = memberDAO.getOne(id);
      if (memberDTO == null) {
        throw new NotFoundException("Membre non trouvé");
      }
      memberDTO.setAddress(addressDAO.getAddressByMemberId(id));

      try {
        File file = new File(memberDTO.getImage());
        picture = ImageIO.read(file);
      } catch (IOException e) {
        throw new NotFoundException("Image inexistante sur le disque");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return picture;
  }

  /**
   * Update any attribute of a member.
   *
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  @Override
  public MemberDTO updateMember(MemberDTO memberDTO) {
    try {
      dalService.startTransaction();
      AddressDTO addressDTO;
      if (memberDTO.getAddress() != null) {
        memberDTO.getAddress().setIdMember(memberDTO.getMemberId());
        addressDTO = addressDAO.updateOne(memberDTO.getAddress());
      } else {
        addressDTO = addressDAO.getAddressByMemberId(memberDTO.getMemberId());
      }

      if (addressDTO == null) {
        throw new ForbiddenException("Problem with updating address");
      }

      MemberDTO modifierMemberDTO = memberDAO.updateOne(memberDTO);
      if (modifierMemberDTO == null) {
        throw new ForbiddenException("Problem with updating member");
      }
      modifierMemberDTO.setAddress(addressDTO);
      dalService.commitTransaction();
      return modifierMemberDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }
}