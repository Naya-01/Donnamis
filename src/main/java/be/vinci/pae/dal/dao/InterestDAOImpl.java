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
  private InterestFactory interestFactory;
  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private MemberDAO memberDAO;
  @Inject
  private ObjectDAO objectDAO;

  /**
   * Get an interest we want to retrieve by the id of the interested member and the id of the
   * object.
   *
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return the interest.
   */
  @Override
  public InterestDTO getOne(int idObject, int idMember) {
    String query =
        "select i.id_object, i.id_member, i.availability_date, i.status, i.send_notification "
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
   * Get an assign interest.
   *
   * @param idObject the object id of the interest we want to retrieve.
   * @return the interest.
   */
  @Override
  public InterestDTO getAssignedInterest(int idObject) {
    String query =
        "select i.id_object, i.id_member, i.availability_date, i.status, i.send_notification"
            + "from donnamis.interests i WHERE i.id_object=? AND i.status=?";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.setString(2, "assigned");
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
      try {
        interestDTO.setObject(objectDAO.getOne(resultSet.getInt("id_object")));
        interestDTO.setIdMember(resultSet.getInt("id_member"));
        interestDTO.setAvailabilityDate(resultSet.getDate("availability_date").toLocalDate());
        interestDTO.setStatus(resultSet.getString("status"));
        interestDTO.setNotification(resultSet.getBoolean("send_notification"));
        interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));
      } catch (SQLException e) {
        throw new FatalException(e);
      }

      resultSet.close();
      preparedStatement.close();

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
        + "status,send_notification) VALUES (?,?,?,?,?);";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, item.getObject().getIdObject());
      preparedStatement.setInt(2, item.getIdMember());
      preparedStatement.setDate(3, Date.valueOf(item.getAvailabilityDate()));
      preparedStatement.setString(4, item.getStatus());
      preparedStatement.setBoolean(5, item.isNotificated());
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

    String query = "SELECT id_object, id_member, availability_date, status, send_notification "
        + "FROM donnamis.interests WHERE id_object = ? AND status != 'cancelled'";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      return getInterestsDTOS(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a list of "published" interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  @Override
  public List<InterestDTO> getAllPublished(int idObject) {

    String query = "SELECT id_object, id_member, availability_date, status,send_notification "
        + "FROM donnamis.interests WHERE id_object = ? AND status = 'published'";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      return getInterestsDTOS(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  /**
   * Get a list of notificated interest in an id object.
   *
   * @param idMember the member we want to retrieve notifications
   * @return a list of interest, by an id member
   */
  @Override
  public List<InterestDTO> getAllNotification(int idMember) {

    String query = "SELECT id_object, id_member, availability_date, status,send_notification "
        + "FROM donnamis.interests WHERE id_member = ? AND send_notification = ? ";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idMember);
      preparedStatement.setBoolean(2, true);
      return getInterestsDTOS(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get interestDTOs list.
   *
   * @param preparedStatement with the data.
   * @return a list of DTOs
   */
  private List<InterestDTO> getInterestsDTOS(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      // Create the interestDTO if we have a result
      List<InterestDTO> interestDTOList = new ArrayList<>();
      while (resultSet.next()) {
        InterestDTO interestDTO = interestFactory.getInterestDTO();
        interestDTO.setObject(objectDAO.getOne(resultSet.getInt("id_object")));
        interestDTO.setIdMember(resultSet.getInt("id_member"));
        interestDTO.setAvailabilityDate(resultSet.getDate("availability_date").toLocalDate());
        interestDTO.setStatus(resultSet.getString("status"));
        interestDTO.setNotification(resultSet.getBoolean("send_notification"));
        interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));
        interestDTOList.add(interestDTO);
      }

      preparedStatement.close();
      resultSet.close();
      return interestDTOList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Update the notification field to know if we have to send one.
   *
   * @param interestDTO with the notification attribute.
   * @return the interest updated.
   */
  public InterestDTO updateNotification(InterestDTO interestDTO) {
    String query = "UPDATE donnamis.interests SET send_notification = ? "
        + "WHERE id_object= ? AND id_member = ? RETURNING id_object, id_member,"
        + " availability_date, status,send_notification ";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setBoolean(1, interestDTO.isNotificated());
      preparedStatement.setInt(2, interestDTO.getObject().getIdObject());
      preparedStatement.setInt(3, interestDTO.getIdMember());

      return getInterestDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Update the status of an interest.
   *
   * @param interestDTO the object that we want to edit the status.
   * @return interest
   */
  public InterestDTO updateStatus(InterestDTO interestDTO) {

    String query = "UPDATE donnamis.interests SET status = ? "
        + "WHERE id_object= ? AND id_member = ? RETURNING availability_date, status, id_member"
        + ", id_object";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setString(1, interestDTO.getStatus());
      preparedStatement.setInt(2, interestDTO.getObject().getIdObject());
      preparedStatement.setInt(3, interestDTO.getIdMember());

      return getInterestDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
