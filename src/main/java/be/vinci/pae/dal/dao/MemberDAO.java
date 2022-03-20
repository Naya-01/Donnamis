package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;
import java.util.List;

public interface MemberDAO {

  /**
   * Promote the member with his id to the admin status.
   *
   * @param id of the member
   */
  void promoteAdministrator(int id);

  /**
   * Confirm the registration of the member and remove his precedent reason.
   *
   * @param id of the member
   */
  void confirmDeniedMemberRegistration(int id);

  /**
   * Confirm the registration of the member with his id.
   *
   * @param id of the member
   */
  void confirmRegistration(int id);

  /**
   * Decline the registration of a member with his id and the reason.
   *
   * @param id     of the member
   * @param reason for denial
   */
  void declineRegistration(int id, String reason);

  /**
   * Get a member we want to retrieve by his username.
   *
   * @param username : the username of the member we want to retrieve
   * @return the member
   */
  MemberDTO getOne(String username);

  /**
   * Get a member we want to retrieve by his id.
   *
   * @param id : the id of the member we want to retrieve
   * @return the member
   */
  MemberDTO getOne(int id);

  /**
   * Add a member in the DB and make a memberDTO.
   *
   * @param member : member we want to add in the DB
   * @return the member added.
   */
  MemberDTO createOneMember(MemberDTO member);

  /**
   * Get all subscription requests according to their status.
   *
   * @param status the status subscription members
   * @return a list of memberDTO
   */
  List<MemberDTO> getAllWithSubStatus(String status);

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members, pending -> pending members,
   *               denied -> denied members, valid -> valid members, empty -> all members
   * @return a list of MemberDTO
   */
  List<MemberDTO> getAll(String search, String status);
}
