package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAOImpl implements MemberDAO {

  @Inject
  private DALService dalService;
  @Inject
  private MemberFactory memberFactory;


  /**
   * Get a member we want to retrieve by his username.
   *
   * @param username : the username of the member we want to retrieve
   * @return the member
   */
  @Override
  public MemberDTO getOne(String username) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "SELECT id_member, username, lastname, firstname, status, role, phone_number, password, "
            + " refusal_reason FROM donnamis.members WHERE username = ?");
    try {

      preparedStatement.setString(1, username);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return findMember(preparedStatement);
  }

  /**
   * Get a member we want to retrieve by his id.
   *
   * @param id : the id of the member we want to retrieve
   * @return the member
   */
  public MemberDTO getOne(int id) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "SELECT id_member, username, lastname, firstname, status, role, phone_number, password, "
            + " refusal_reason FROM donnamis.members WHERE id_member = ?");
    try {
      preparedStatement.setInt(1, id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return findMember(preparedStatement);
  }

  /**
   * Make a memberDTO with the result set.
   *
   * @param preparedStatement : a prepared statement to execute the query.
   * @return the member.
   */
  public MemberDTO findMember(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      MemberDTO memberDTO = memberFactory.getMemberDTO();
      memberDTO.setMemberId(resultSet.getInt(1));
      memberDTO.setUsername(resultSet.getString(2));
      memberDTO.setLastname(resultSet.getString(3));
      memberDTO.setFirstname(resultSet.getString(4));
      memberDTO.setStatus(resultSet.getString(5));
      memberDTO.setRole(resultSet.getString(6));
      memberDTO.setPhone(resultSet.getString(7));
      memberDTO.setPassword(resultSet.getString(8));
      // TODO : set the adress
      memberDTO.setReasonRefusal(resultSet.getString(9));

      return memberDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }


  /**
   * Add a member in the DB and make a memberDTO.
   *
   * @param member : member we want to add in the DB
   * @return the member added.
   */
  @Override
  public MemberDTO addOneMember(MemberDTO member) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement("insert into "
        + "donnamis.members (username, lastname, firstname, status, role, phone_number, "
        + "password, refusal_reason) values (?,?,?,?,?,?,?,?) RETURNING id_member;");
    try {
      preparedStatement.setString(1, member.getUsername());
      preparedStatement.setString(2, member.getLastname());
      preparedStatement.setString(3, member.getFirstname());
      preparedStatement.setString(4, member.getStatus());
      preparedStatement.setString(5, member.getRole());
      preparedStatement.setString(6, member.getPhone());
      preparedStatement.setString(7, member.getPassword());
      preparedStatement.setString(8, member.getReasonRefusal());

      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      //get id of new member
      int idNewMember = resultSet.getInt(1);

      //update memberDTO
      member.setMemberId(idNewMember);

      return member;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }

}