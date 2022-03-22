package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.util.List;

public class InterestUCCImpl implements InterestUCC {

  @Inject
  private InterestDAO interestDAO;
  @Inject
  private DALService dalService;
  @Inject
  private ObjectDAO objectDAO;

  /**
   * Find an interest, by the id of the interested member and the id of the object.
   *
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return interestDTO having the idObject and idMember.
   */
  @Override
  public InterestDTO getInterest(int idObject, int idMember) {
    dalService.startTransaction();
    InterestDTO interestDTO = interestDAO.getOne(idObject, idMember);
    if (interestDTO == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Interest not found");
    }
    dalService.commitTransaction();
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
    try {
      dalService.startTransaction();
      if (interestDAO.getOne(item.getIdObject(), item.getIdMember()) != null) {
        //change name exception
        throw new NotFoundException("An Interest for this Object and Member already exists");
      }
      interestDAO.addOne(item);
      if (interestDAO.getOne(item.getIdObject(), item.getIdMember()) == null) {
        //change name exception
        throw new NotFoundException("Interest not added");
      }
    } catch (NotFoundException e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
    return item;
  }

  /**
   * Get a list of interest, by an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  @Override
  public List<InterestDTO> getInterestedCount(int idObject) {
    dalService.startTransaction();
    ObjectDTO objectDTO = objectDAO.getOne(idObject);
    if (objectDTO == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Object not found");
    }
    List<InterestDTO> allInterests = interestDAO.getAll(idObject);
    dalService.commitTransaction();
    return allInterests;
  }

}
