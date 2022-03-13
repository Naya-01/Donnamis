package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;

public interface MemberDAO {

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
   * Add a member in the DB and make a memberDTO with the parameters.
   *
   * @param username      : the username of the member we want to retrieve.
   * @param lastname      : the lastname of the member we want to retrieve.
   * @param firstname     : the firstname of the member we want to retrieve.
   * @param status        : the status of the member we want to retrieve.
   * @param role          : the role of the member we want to retrieve.
   * @param phoneNumber   : the phone number of the member we want to retrieve.
   * @param password      : the password of the member we want to retrieve.
   * @param idAddress     : the id address of the member we want to retrieve.
   * @param refusalReason : the refusal reason of the member we want to retrieve.
   * @return the member added.
   */
  MemberDTO addOneMember(String username, String lastname, String firstname, String status,
      String role, String phoneNumber, String password, int idAddress, String refusalReason);
}
