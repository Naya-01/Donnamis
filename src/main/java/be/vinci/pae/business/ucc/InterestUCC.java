package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import java.util.List;

public interface InterestUCC {

  /**
   * Find an interest, by the id of the interested member and the id of the object.
   *
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return interestDTO having the idObject and idMember.
   */
  InterestDTO getInterest(InterestDTO interestDTO);

  /**
   * Add one interest.
   *
   * @param item : the interest informations (id of the object and id of the member).
   * @return item.
   */
  InterestDTO addOne(InterestDTO item);

  /**
   * Assign the object to a member.
   *
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return objectDTO updated.
   */
  InterestDTO assignObject(InterestDTO interestDTO);

  /**
   * Get a list of interest, by an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  List<InterestDTO> getInterestedCount(int idObject);

  /**
   * Give an Object.
   *
   * @param interestDTO : the interest information (id of the object)
   * @return an object
   */
  InterestDTO giveObject(InterestDTO interestDTO);
}
