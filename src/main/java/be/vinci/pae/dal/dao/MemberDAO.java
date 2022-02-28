package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;

public interface MemberDAO {

  /**
   * Get a member we want to retrieve by his pseudo.
   *
   * @param pseudo : the pseudo of the member we want to retrieve
   * @return the member
   */
  MemberDTO getOne(String pseudo);
}
