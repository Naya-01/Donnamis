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
   * @param item : interestDTO object.
   * @return item.
   */
  InterestDTO addOne(InterestDTO item);

  /**
   * Get a list of "published" interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  List<InterestDTO> getAllPublished(int idObject);

  /**
   * Get a list of interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  List<InterestDTO> getAll(int idObject);

  /**
   * Update the status of an interest.
   *
   * @param interestDTO the object that we want to edit the status.
   * @return interest
   */
  InterestDTO updateStatus(InterestDTO interestDTO);
}
