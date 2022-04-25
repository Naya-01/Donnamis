package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import com.fasterxml.jackson.databind.JsonNode;
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
   * Get a list of notificated interest in an id object.
   *
   * @param member the member we want to retrieve notifications
   * @return a list of interest, by an id member
   */
  List<InterestDTO> getNotifications(MemberDTO member);

  /**
   * Get the count of interested people of an object.
   *
   * @param idObject  the object we want to retrieve the interest count.
   * @param memberDTO to check if he is in the interested people.
   * @return jsonNode with count of interests and a boolean if the user is one of the interested
   */
  JsonNode getInterestedCount(Integer idObject, MemberDTO memberDTO);


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
