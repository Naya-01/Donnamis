package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import java.util.List;

public interface InterestUCC {

  /**
   * Find an interest, by the id of the interested member and the id of the object.
   *
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return interestDTO having the idObject and idMember.
   */
  InterestDTO getInterest(int idObject, int idMember);

  /**
   * Add one interest.
   *
   * @param item : the interest informations (id of the object and id of the member).
   * @return item.
   */
  InterestDTO addOne(InterestDTO item);

  /**
   * Assign the offer to a member.
   *
   * @param owner       of the object
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return objectDTO updated.
   */
  InterestDTO assignOffer(InterestDTO interestDTO, MemberDTO owner);

  /**
   * Get a list of interest, by an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  Integer getInterestedCount(Integer idObject);

  /**
   * Get notification count.
   *
   * @param member of the member.
   * @return count of notification
   */
  Integer getNotificationCount(MemberDTO member);

  /**
   * Get the number of all interests.
   *
   * @param idObject the object we want to retrieve the interests
   * @param offeror  the owner of the object
   * @return the number of all interests
   */
  List<InterestDTO> getAllInterests(int idObject, MemberDTO offeror);

  /**
   * Check if a member is interested by an object.
   *
   * @param member   the id of the member
   * @param idObject the id of the object
   * @return true if he's interested false if he's not
   */
  boolean isUserInterested(MemberDTO member, int idObject);

  /**
   * Get a list of notificated interest in an id object.
   *
   * @param member the member we want to retrieve notifications
   * @return a list of interest, by an id member
   */
  List<InterestDTO> getNotifications(MemberDTO member);


  /**
   * Mark a notification shown.
   *
   * @param member   of the member
   * @param idObject to mark as shown.
   * @return interestDTO updated.
   */
  InterestDTO markNotificationShown(int idObject, MemberDTO member);

  /**
   * Mark all notifications shown.
   *
   * @param member to mark all his notifications showns.
   * @return interestDTOs updated.
   */
  List<InterestDTO> markAllNotificationsShown(MemberDTO member);

}
