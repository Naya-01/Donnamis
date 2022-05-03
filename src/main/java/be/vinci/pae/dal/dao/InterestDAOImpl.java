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
        "select i.id_object, i.id_member, i.availability_date, i.status, i.send_notification, "
            + "i.version, i.be_called from donnamis.interests i "
            + "WHERE i.id_object=? AND i.id_member=?";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.setInt(2, idMember);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestDTO(resultSet);
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
        "select i.id_object, i.id_member, i.availability_date, i.status, i.send_notification, "
            + "i.be_called, i.version "
            + "from donnamis.interests i WHERE i.id_object=? AND i.status=? ";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.setString(2, "assigned");
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestDTO(resultSet);
    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  /**
   * Make an interestDTO with the result set.
   *
   * @param resultSet : contain the result of the query.
   * @return the interest.
   */
  private InterestDTO getInterestDTO(ResultSet resultSet) {
    try {
      if (!resultSet.next()) {
        return null;
      }
      // Create the interestDTO if we have a result
      InterestDTO interestDTO = interestFactory.getInterestDTO();
      try {
        interestDTO.setIdObject(resultSet.getInt("id_object"));
        interestDTO.setIdMember(resultSet.getInt("id_member"));
        interestDTO.setAvailabilityDate(
            resultSet.getDate("availability_date").toLocalDate());
        interestDTO.setStatus(resultSet.getString("status"));
        interestDTO.setIsNotificated(resultSet.getBoolean("send_notification"));
        interestDTO.setIsCalled(resultSet.getBoolean("be_called"));
        interestDTO.setVersion(resultSet.getInt("version"));
      } catch (SQLException e) {
        throw new FatalException(e);
      }

      resultSet.close();

      return interestDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get interestDTOs list.
   *
   * @param resultSet : contain the result of the query.
   * @return a list of DTOs
   */
  private List<InterestDTO> getInterestsDTOSList(ResultSet resultSet) {
    try {

      List<InterestDTO> interestDTOList = new ArrayList<>();
      while (resultSet.next()) {
        InterestDTO interestDTO = interestFactory.getInterestDTO();
        interestDTO.setIdMember(resultSet.getInt("id_member"));
        interestDTO.setAvailabilityDate(resultSet.getDate(3).toLocalDate());
        interestDTO.setStatus(resultSet.getString(4));
        interestDTO.setVersion(resultSet.getInt("version"));
        interestDTO.setIdObject(resultSet.getInt("id_object"));
        interestDTO.setIsCalled(resultSet.getBoolean("be_called"));

        interestDTOList.add(interestDTO);
      }
      resultSet.close();
      if (interestDTOList.isEmpty()) {
        return null;
      }
      return interestDTOList;
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
        + "status,send_notification,be_called, version) VALUES (?,?,?,?,?,?,?) "
        + "RETURNING id_object, id_member, "
        + "availability_date, status, send_notification, version, be_called";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, item.getIdObject());
      preparedStatement.setInt(2, item.getIdMember());
      preparedStatement.setDate(3, Date.valueOf(item.getAvailabilityDate()));
      preparedStatement.setString(4, item.getStatus());
      preparedStatement.setBoolean(5, true);
      preparedStatement.setBoolean(6, item.getIsCalled());
      preparedStatement.setInt(7, 1);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestDTO(resultSet);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a list of interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  @Override
  public int getAllCount(int idObject) {

    String query = "SELECT count(*) as nb "
        + " FROM donnamis.interests WHERE id_object = ? ";

    return getnbInterests(idObject, query);
  }

  private Integer getnbInterests(Integer idObject, String query) {
    int nbInterests;
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return 0;
      }
      nbInterests = resultSet.getInt("nb");
      resultSet.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return nbInterests;
  }

  /**
   * Get the number of all interests.
   *
   * @param idObject the object we want to retrieve the interests
   * @return the number of all interests
   */
  @Override
  public Integer getAllPublishedCount(Integer idObject) {

    String query = "SELECT count(i.*) as nb FROM donnamis.interests i "
        + "WHERE i.id_object = ? AND i.status = 'published'";
    return getnbInterests(idObject, query);

  }

  /**
   * Get a list of "published" interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  @Override
  public List<InterestDTO> getAllPublished(int idObject) {
    String query = "SELECT id_object, id_member, availability_date, status, send_notification, "
        + "version, be_called "
        + "FROM donnamis.interests WHERE id_object = ? AND status = 'published'";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestsDTOSList(resultSet);
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
  public List<InterestDTO> getAllNotifications(int idMember) {

    String query =
        "SELECT DISTINCT i.id_member, i.id_object, i.availability_date, i.status, i.version "
            + ",i.send_notification,i.be_called "
            + "FROM donnamis.interests i , donnamis.objects o "
            + "WHERE (i.id_object = o.id_object AND o.id_offeror = ? AND i.status = 'published' "
            + "AND i.send_notification = true) "
            + "   OR (i.id_member = ? AND i.send_notification = true AND i.status != 'published') "
            + "ORDER BY i.availability_date DESC";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idMember);
      preparedStatement.setInt(2, idMember);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestsDTOSList(resultSet);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Mark all notifications shown. /!\ There is no version update because of the non-sensibility of
   * the send_notification field /!\
   *
   * @param idMember to mark all his notifications showns.
   * @return interestDTOs updated.
   */
  @Override
  public List<InterestDTO> markAllNotificationsShown(Integer idMember) {
    String query = "UPDATE donnamis.interests SET send_notification = ? "
        + "WHERE id_member = ? AND send_notification = true "
        + "RETURNING id_object, id_member, availability_date, status,"
        + " send_notification, version, be_called ";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setBoolean(1, false);
      preparedStatement.setInt(2, idMember);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestsDTOSList(resultSet);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get notification count.
   *
   * @param idMember of the member.
   * @return count of notification
   */
  @Override
  public Integer getNotificationCount(Integer idMember) {
    String query = "SELECT count(DISTINCT i.*) "
        + "FROM donnamis.interests i , donnamis.objects o "
        + "WHERE (i.id_object = o.id_object AND o.id_offeror = ? AND i.status = 'published' "
        + "AND i.send_notification = true) "
        + "   OR (i.id_member = ? AND i.send_notification = true AND i.status != 'published')";
    Integer notificationCount = null;
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idMember);
      preparedStatement.setInt(2, idMember);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (resultSet.next()) {
        notificationCount = resultSet.getInt(1);
      }
      resultSet.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }

    return notificationCount;
  }


  /**
   * Update the notification field to know if we have to send one. /!\ There is no version update
   * because of the non-sensibility of the send_notification field /!\
   *
   * @param interestDTO with the notification attribute.
   * @return the interest updated.
   */
  @Override
  public InterestDTO updateNotification(InterestDTO interestDTO) {
    String query = "UPDATE donnamis.interests SET send_notification = ? "
        + "WHERE id_object= ? AND id_member = ? RETURNING id_object, id_member,"
        + " availability_date, status,send_notification, version, be_called ";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setBoolean(1, interestDTO.getIsNotificated());
      preparedStatement.setInt(2, interestDTO.getIdObject());
      preparedStatement.setInt(3, interestDTO.getIdMember());
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestDTO(resultSet);
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
  @Override
  public InterestDTO updateStatus(InterestDTO interestDTO) {

    String query = "UPDATE donnamis.interests SET status = ?, version = ? "
        + "WHERE id_object = ? AND id_member = ? RETURNING id_object, id_member, "
        + "availability_date, status, send_notification, version, be_called";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setString(1, interestDTO.getStatus());
      preparedStatement.setInt(2, interestDTO.getVersion() + 1);
      preparedStatement.setInt(3, interestDTO.getIdObject());
      preparedStatement.setInt(4, interestDTO.getIdMember());
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getInterestDTO(resultSet);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Update all statuses of the member's interests.
   *
   * @param idMember   update all interests of this member.
   * @param statusFrom actual status of the interests
   * @param statusTo   status updated
   */
  @Override
  public void updateAllInterestsStatus(int idMember, String statusFrom, String statusTo) {
    String query = " UPDATE donnamis.interests SET status= ?, version= version+1 "
        + "WHERE id_member = ? AND status= ? "
        + "RETURNING id_object, id_member, availability_date, status, "
        + " send_notification, version, be_called ";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setString(1, statusTo);
      preparedStatement.setInt(2, idMember);
      preparedStatement.setString(3, statusFrom);
      preparedStatement.executeQuery();
    } catch (SQLException e) {
      throw new FatalException(e);
    }


  }


}
