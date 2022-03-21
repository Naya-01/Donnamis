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
   * Promote the member with his id to the admin status.
   *
   * @param id of the member
   */
  @Override
  public void promoteAdministrator(int id) {
    String query = "UPDATE donnamis.members SET role='administrator' WHERE id_member=?";
    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Confirm the registration of the member and remove his precedent reason.
   *
   * @param id of the member
   */
  @Override
  public void confirmDeniedMemberRegistration(int id) {
    String query = "UPDATE donnamis.members SET refusal_reason = NULL WHERE id_member=?";
    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    confirmRegistration(id);
  }

  /**
   * Confirm the registration of the member with his id.
   *
   * @param id of the member
   */
  @Override
  public void confirmRegistration(int id) {
    String query = "UPDATE donnamis.members SET status='valid' WHERE id_member=?";
    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Decline the registration of a member with his id and the reason.
   *
   * @param id     of the member
   * @param reason for denial
   */
  @Override
  public void declineRegistration(int id, String reason) {
    String query = "UPDATE donnamis.members SET status='denied' , "
        + "refusal_reason=? WHERE id_member=?";
    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.setString(1, reason);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

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
            + "refusal_reason,image FROM donnamis.members WHERE username = ?");
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
            + "refusal_reason,image FROM donnamis.members WHERE id_member = ?");
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
            resultSet.getString(9), resultSet.getString(10));
        memberDTOList.add(memberDTO);
      }
      return memberDTOList;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return memberDTOList;
  }

  private MemberDTO getMember(int memberId, String username, String lastName, String firstname,
      String status, String role, String phone, String password, String reasonRefusal,
      String image) {

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
    memberDTO.setImage(image);
    return memberDTO;
  }


  /**
   * Add a member in the DB and make a memberDTO.
   *
   * @param member : member we want to add in the DB
   * @return the member added.
   */
  @Override
  public MemberDTO createOneMember(MemberDTO member) {
    PreparedStatement preparedStatement = dalService.getPreparedStatement("insert into "
        + "donnamis.members (username, lastname, firstname, status, role, phone_number, "
        + "password, refusal_reason,image) values (?,?,?,?,?,?,?,?,?) RETURNING id_member;");
    try {
      preparedStatement.setString(1, member.getUsername());
      preparedStatement.setString(2, member.getLastname());
      preparedStatement.setString(3, member.getFirstname());
      preparedStatement.setString(4, member.getStatus());
      preparedStatement.setString(5, member.getRole());
      preparedStatement.setString(6, member.getPhone());
      preparedStatement.setString(7, member.getPassword());
      preparedStatement.setString(8, member.getReasonRefusal());
      preparedStatement.setString(9, member.getImage());

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
            + "refusal_reason,image FROM donnamis.members WHERE status = ?");

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