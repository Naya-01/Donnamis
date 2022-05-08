package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;
import java.util.List;

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
  MemberDTO getOne(Integer id);

  /**
   * Add a member in the DB and make a memberDTO. *
   *
   * @param member : member we want to add in the DB
   * @return the member added.
   */
  MemberDTO createOneMember(MemberDTO member);

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members, pending -> pending members,
   *               denied -> denied members, valid -> valid members, empty ("") -> all members,
   *               other than these -> all members
   * @return a list of MemberDTO
   */
  List<MemberDTO> getAll(String search, String status);

  /**
   * Update one or many attribute(s) of a member.
   *
   * @param memberDTO a memberDTO
   * @return the updated member
   */
  MemberDTO updateOne(MemberDTO memberDTO);

  /**
   * Update the profil picture of the member.
   *
   * @param path of the picture
   * @param id   of the member
   * @return memberDTO updated
   */
  MemberDTO updateProfilPicture(String path, int id);
}
