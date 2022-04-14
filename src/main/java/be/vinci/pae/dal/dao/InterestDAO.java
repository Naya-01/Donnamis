package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.InterestDTO;
import java.util.List;

public interface InterestDAO {

  /**
   * Get an interest we want to retrieve by the id of the interested member and the id of the
   * object.
   *
   * @param idObject : the object id of the interest we want to retrieve.
   * @param idMember :  the member id of the interest we want to retrieve.
   * @return the interest.
   */
  InterestDTO getOne(int idObject, int idMember);

  /**
   * Add one interest in the DB.
   *
   * @param item : interestDTO object.
   * @return item.
   */
  InterestDTO addOne(InterestDTO item);

  /**
   * Get a list of interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  List<InterestDTO> getAll(int idObject);
}
