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


}