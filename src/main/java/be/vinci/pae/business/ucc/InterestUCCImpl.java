package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
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
    InterestDTO interestDTO;
    try {
      dalService.startTransaction();
      interestDTO = interestDAO.getOne(idObject, idMember);
      if (interestDTO == null) {
        throw new NotFoundException("Interest not found");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
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
    try {
      dalService.startTransaction();
      if (interestDAO.getOne(item.getIdObject(), item.getIdMember()) != null) {
        //change name exception
        throw new NotFoundException("An Interest for this Object and Member already exists");
      }
      // if there is no interest
      if (interestDAO.getAll(item.getIdObject()).isEmpty()) {
        ObjectDTO objectDTO = objectDAO.getOne(item.getIdObject());
        if (objectDTO == null) {
          throw new NotFoundException("Object not found");
        }
        objectDTO.setStatus("interested");
        objectDAO.updateOne(objectDTO);
      }
      interestDAO.addOne(item);
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
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
    List<InterestDTO> interestDTOList;
    try {
      dalService.startTransaction();
      ObjectDTO objectDTO = objectDAO.getOne(idObject);
      if (objectDTO == null) {
        throw new NotFoundException("Object not found");
      }
      interestDTOList = interestDAO.getAll(idObject);
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return interestDTOList;
  }

}
