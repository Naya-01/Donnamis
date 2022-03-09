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
   * Add a member in the DB and make a memberDTO with the parameters
   *
   * @param username : the username of the member we want to retrieve
   * @return the new member
   */
  MemberDTO addOneMember(String username, String lastname, String firstname, String status,
      String role, String phone_number, String password, int id_address, String refusal_reason);
}
