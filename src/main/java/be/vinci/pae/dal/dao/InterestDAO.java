package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.InterestDTO;
import java.util.List;

public interface InterestDAO {

  /**
   * Get a list of notificated interest in an id object.
   *
   * @param idMember the member we want to retrieve notifications
   * @return a list of interest, by an id member
   */
  List<InterestDTO> getAllNotifications(int idMember);

  /**
   * Mark all notifications shown. /!\ There is no version update because of the non-sensibility of
   * the send_notification field /!\
   *
   * @param idMember to mark all his notifications showns.
   * @return interestDTOs updated.
   */
  List<InterestDTO> markAllNotificationsShown(Integer idMember);

  /**
   * Get notification count.
   *
   * @param idMember of the member.
   * @return count of notification
   */
  Integer getNotificationCount(Integer idMember);

  /**
   * Get an interest we want to retrieve by the id of the interested member and the id of the
   * object.
   *
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return the interest.
   */
  InterestDTO getOne(int idObject, int idMember);

  /**
   * Update the notification field to know if we have to send one.
   *
   * @param interestDTO with the notification attribute.
   * @return the interest updated.
   */
  InterestDTO updateNotification(InterestDTO interestDTO);

  /**
   * Get an assign interest.
   *
   * @param idObject the object id of the interest we want to retrieve.
   * @return the interest.
   */
  InterestDTO getAssignedInterest(int idObject);

  /**
   * Add one interest in the DB.
   *
   * @param interestDTO : interestDTO object.
   * @return interest.
   */
  InterestDTO addOne(InterestDTO interestDTO);

  /**
   * Get the number of all interests.
   *
   * @param idObject the object we want to retrieve the interests
   * @return the number of all interests
   */
  Integer getAllPublishedCount(Integer idObject);

  /**
   * Get a list of "published" interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  List<InterestDTO> getAllPublished(int idObject);

  /**
   * Get a count of interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a count of interest, by an id object
   */
  int getAllCount(int idObject);

  /**
   * Update the status of an interest.
   *
   * @param interestDTO the object that we want to edit the status.
   * @return interest
   */
  InterestDTO updateStatus(InterestDTO interestDTO);

  /**
   * Update all statuses of the member's interests.
   *
   * @param idMember   update all interests of this member.
   * @param statusFrom actual status of the interests
   * @param statusTo   status updated
   */
  void updateAllInterestsStatus(int idMember, String statusFrom, String statusTo);
}
