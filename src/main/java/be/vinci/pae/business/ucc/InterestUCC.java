package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
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
   * @param item : interestDTO object.
   * @return item.
   */
  InterestDTO addOne(InterestDTO item);

  /**
   * Get a list of interest by an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest by an id object
   */
  List<InterestDTO> getInterestedCount(int idObject);

}
