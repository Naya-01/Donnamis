package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    List<MemberDTO> memberDTOList = getMemberList(preparedStatement);
    if (memberDTOList.size() != 1) {
      return null;
    }
    return memberDTOList.get(0);
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
    List<MemberDTO> memberDTOList = getMemberList(preparedStatement);
    if (memberDTOList.size() != 1) {
      return null;
    }
    return memberDTOList.get(0);
  }

  /**
   * Make a memberDTO with the result set.
   *
   * @param preparedStatement : a prepared statement to execute the query.
   * @return the member.
   */
  private List<MemberDTO> getMemberList(PreparedStatement preparedStatement) {
    List<MemberDTO> memberDTOList = new ArrayList<>();
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      while (resultSet.next()) {
        MemberDTO memberDTO = getMember(resultSet.getInt(1), resultSet.getString(2),
            resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
            resultSet.getString(6), resultSet.getString(7), resultSet.getString(8),
            resultSet.getString(9));
        memberDTOList.add(memberDTO);
      }
      return memberDTOList;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return memberDTOList;
  }

  private MemberDTO getMember(int memberId, String username, String lastName, String firstname,
      String status, String role, String phone, String password, String reasonRefusal) {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(memberId);
    memberDTO.setUsername(username);
    memberDTO.setLastname(lastName);
    memberDTO.setFirstname(firstname);
    memberDTO.setStatus(status);
    memberDTO.setRole(role);
    memberDTO.setPhone(phone);
    memberDTO.setPassword(password);
    memberDTO.setReasonRefusal(reasonRefusal);
    return memberDTO;
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
      //create memberDTO
      return getMember(resultSet.getInt(1), username, lastname, firstname, status, role,
          phoneNumber, password, refusalReason);

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get all subscription requests according to their status.
   *
   * @param status the status subscription members
   * @return a list of memberDTO
   */
  @Override
  public List<MemberDTO> getAllWithSubStatus(String status) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "SELECT id_member, username, lastname, firstname, status, role, phone_number, password, "
            + "refusal_reason FROM donnamis.members WHERE status = ?");

    try {
      preparedStatement.setString(1, status);
      preparedStatement.executeQuery();
      return getMemberList(preparedStatement);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}