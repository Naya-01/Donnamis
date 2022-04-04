package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InterestDAOImpl implements InterestDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private InterestFactory interestFactory;

  /**
   * Get an interest we want to retrieve by the id of the interested member and the id of the
   * object.
   *
   * @param idObject : the object id of the interest we want to retrieve.
   * @param idMember :  the member id of the interest we want to retrieve.
   * @return the interest.
   */
  @Override
  public InterestDTO getOne(int idObject, int idMember) {
    String query = "select i.id_object, i.id_member, i.availability_date, i.status "
        + "from donnamis.interests i WHERE i.id_object=? AND i.id_member=?";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.setInt(2, idMember);
      return getInterestDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Make an interestDTO with the result set.
   *
   * @param preparedStatement : a prepared statement to execute the query.
   * @return the interest.
   */
  private InterestDTO getInterestDTO(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      // Create the interestDTO if we have a result
      InterestDTO interestDTO = interestFactory.getInterestDTO();
      interestDTO.setIdObject(resultSet.getInt(1));
      interestDTO.setIdMember(resultSet.getInt(2));
      interestDTO.setAvailabilityDate(resultSet.getDate(3).toLocalDate());
      interestDTO.setStatus(resultSet.getString(4));
      resultSet.close();

      return interestDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Add one interest in the DB.
   *
   * @param item : interestDTO object.
   * @return item.
   */
  @Override
  public InterestDTO addOne(InterestDTO item) {
    String query = "INSERT INTO donnamis.interests (id_object, id_member, availability_date, "
        + "status) VALUES (?,?,?,?);";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, item.getIdObject());
      preparedStatement.setInt(2, item.getIdMember());
      preparedStatement.setDate(3, Date.valueOf(item.getAvailabilityDate()));
      preparedStatement.setString(4, item.getStatus());
      preparedStatement.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return item;
  }

  /**
   * Get a list of interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  @Override
  public List<InterestDTO> getAll(int idObject) {
    String query = "SELECT id_object, id_member, availability_date, status "
        + "FROM donnamis.interests WHERE id_object = ? AND status != 'cancelled'";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();

      List<InterestDTO> interestDTOList = new ArrayList<>();
      while (resultSet.next()) {
        InterestDTO interestDTO = interestFactory.getInterestDTO();
        interestDTO.setIdObject(resultSet.getInt(1));
        interestDTO.setIdMember(resultSet.getInt(2));
        interestDTO.setAvailabilityDate(resultSet.getDate(3).toLocalDate());
        interestDTO.setStatus(resultSet.getString(4));

        interestDTOList.add(interestDTO);
      }
      resultSet.close();
      return interestDTOList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Update the status of an interest.
   *
   * @param idObject the object that we want to edit.
   * @param status the new status for the object
   * @return interest
   */
  public InterestDTO updateStatus(int idObject, String status) {
    String query = "UPDATE donnamis.interests SET status = ? "
        + "WHERE id_object= ? AND status = ? RETURNING availability_date, status, id_member"
        + ", id_object";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(2, idObject);

      if (status.equals("received")) {
        preparedStatement.setString(3, "assigned");
      } else {
        preparedStatement.setString(3, status);
      }
      preparedStatement.setString(1, status);

      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();

      if (!resultSet.next()) {
        return null;
      }

      InterestDTO interestDTO = interestFactory.getInterestDTO();
      interestDTO.setAvailabilityDate(resultSet.getDate(1).toLocalDate());
      interestDTO.setStatus(resultSet.getString(2));
      interestDTO.setIdMember(resultSet.getInt(3));
      interestDTO.setIdObject(resultSet.getInt(4));

      return interestDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
