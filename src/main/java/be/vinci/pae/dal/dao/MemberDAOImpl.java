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
   * Get a member we want to retrieve by his pseudo.
   *
   * @param pseudo : the pseudo of the member we want to retrieve
   * @return the member
   */
  @Override
  public MemberDTO getOne(String pseudo) {

    PreparedStatement preparedStatement = dalService.getPreparedStatement(
        "SELECT id_membre, pseudo, nom, prenom, etat, role, telephone, password, "
            + "id_adresse, raison_refus FROM donnamis.membres WHERE pseudo = ?");
    try {

      preparedStatement.setString(1, pseudo);
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      MemberDTO memberDTO = memberFactory.getMemberDTO();
      memberDTO.setMemberId(resultSet.getInt(1));
      memberDTO.setPseudo(resultSet.getString(2));
      memberDTO.setName(resultSet.getString(3));
      memberDTO.setFirstname(resultSet.getString(4));
      memberDTO.setStatus(resultSet.getString(5));
      memberDTO.setRole(resultSet.getString(6));
      memberDTO.setPhone(resultSet.getString(7));
      memberDTO.setPassword(resultSet.getString(8));
      memberDTO.setAddresse(resultSet.getInt(9));
      memberDTO.setReasonRefusal(resultSet.getString(10));

      return memberDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}