package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.InterestDAO;
import jakarta.inject.Inject;
import java.util.List;

public class InterestUCCImpl implements InterestUCC {

  @Inject
  private InterestDAO interestDAO;


  /**
   * Find an interest, by the id of the interested member and the id of the object.
   *
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return interestDTO having the idObject and idMember.
   */
  @Override
  public InterestDTO getInterest(int idObject, int idMember) {
    InterestDTO interestDTO = interestDAO.getOne(idObject, idMember);
    if (interestDTO == null) {
      throw new NotFoundException("Interest not found");
    }
    return interestDTO;
  }

  /**
   * Add one interest.
   *
   * @param item : interestDTO object.
   * @return item.
   */
  @Override
  public InterestDTO addOne(InterestDTO item) {
    if (interestDAO.getOne(item.getIdObject(), item.getIdMember()) != null) {
      //change name exception
      throw new NotFoundException("An Interest for this Object and Member already exists");
    }
    interestDAO.addOne(item);
    if (this.getInterest(item.getIdObject(), item.getIdMember()) == null) {
      //change name exception
      throw new NotFoundException("Interest not added");
    }
    return item;
  }

  /**
   * Get a list of interest by an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest by an id object
   */
  @Override
  public List<InterestDTO> getInterestedCount(int idObject) {
    return interestDAO.getAll(idObject);
  }

}
