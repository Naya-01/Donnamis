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
            + "refusal_reason FROM donnamis.members WHERE username = ?");
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
            + "refusal_reason FROM donnamis.members WHERE id_member = ?");
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
  @Override
  public MemberDTO addOneMember(String username, String lastname, String firstname, String status,
      String role, String phoneNumber, String password, int idAddress, String refusalReason) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement("insert into "
        + "donnamis.members (username, lastname, firstname, status, role, phoneNumber, "
        + "password, idAddress, refusalReason) values (?,?,?,?,?,?,?,?,?) RETURNING id_member;");
    try {

      preparedStatement.setString(1, username);
      preparedStatement.setString(2, lastname);
      preparedStatement.setString(3, firstname);
      preparedStatement.setString(4, status);
      preparedStatement.setString(5, role);
      preparedStatement.setString(6, phoneNumber);
      preparedStatement.setString(7, password);
      preparedStatement.setInt(8, idAddress);
      preparedStatement.setString(9, refusalReason);

      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      //get id of new member
      int idNewMember = resultSet.getInt(1);

      //create memberDTO
      MemberDTO memberDTO = memberFactory.getMemberDTO();
      memberDTO.setMemberId(idNewMember);
      memberDTO.setUsername(username);
      memberDTO.setLastname(lastname);
      memberDTO.setFirstname(firstname);
      memberDTO.setStatus(status);
      memberDTO.setRole(role);
      memberDTO.setPhone(phoneNumber);
      memberDTO.setPassword(password);
      memberDTO.setAddress(idAddress);
      memberDTO.setReasonRefusal(refusalReason);

      return memberDTO;

    } catch (SQLException e) {
      e.printStackTrace();
    }

    return null;
  }


}