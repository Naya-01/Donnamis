package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;

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
}
