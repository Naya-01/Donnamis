package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;
import java.util.List;

public interface MemberDAO {

  /**
   * Get a member we want to retrieve by his username.
   *
   * @param <T>      class type
   * @param username : the username of the member we want to retrieve
   * @return the member
   */
  <T> MemberDTO getOne(String username);

  /**
   * Get a member we want to retrieve by his id.
   *
   * @param <T> class type
   * @param id  : the id of the member we want to retrieve
   * @return the member
   */
  <T> MemberDTO getOne(Integer id);

  /**
   * Add a member in the DB and make a memberDTO. *
   *
   * @param <T>    class type
   * @param member : member we want to add in the DB
   * @return the member added.
   */
  <T> MemberDTO createOneMember(MemberDTO member);

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param <T>    class type
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members, pending -> pending members,
   *               denied -> denied members, valid -> valid members, empty ("") -> all members,
   *               other than these -> all members
   * @return a list of MemberDTO
   */
  <T> List<MemberDTO> getAll(String search, String status);

  /**
   * Update any attribute of a member.
   *
   * @param <T>       class type
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  <T> MemberDTO updateOne(MemberDTO memberDTO);

  /**
   * Update the profil picture of the member.
   *
   * @param path of the picture
   * @param id   of the member
   * @return memberDTO updated
   */
  MemberDTO updateProfilPicture(String path, int id);
}
