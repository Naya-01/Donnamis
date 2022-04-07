package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
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
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return interestDTO having the idObject and idMember.
   */
  @Override
  public InterestDTO getInterest(InterestDTO interestDTO) {
    try {
      dalService.startTransaction();
      interestDTO = interestDAO.getOne(interestDTO);
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
      if (interestDAO.getOne(item) != null) {
        //change name exception
        throw new NotFoundException("An Interest for this Object and Member already exists");
      }
      // if there is no interest
      if (interestDAO.getAll(item.getObject().getIdObject()).isEmpty()) {
        ObjectDTO objectDTO = objectDAO.getOne(item.getObject().getIdObject());
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
   * Assign the object to a member.
   *
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return objectDTO updated.
   */
  @Override
  public InterestDTO assignObject(InterestDTO interestDTO) {
    try {
      dalService.startTransaction();
      if (!interestDTO.getObject().getStatus().equals("interested")) {
        throw new ForbiddenException("L'objet n'est pas en mesure d'être assigné");
      }
      interestDTO = interestDAO.getOne(interestDTO);
      if (interestDTO == null) {
        throw new NotFoundException("Le membre ne présente pas d'intérêt");
      }
      // update object to assigned
      interestDTO.getObject().setStatus("assigned");
      objectDAO.updateOne(interestDTO.getObject());
      // update interest to assigned
      interestDTO.setStatus("assigned");
      interestDAO.updateStatus(interestDTO);

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }

    return interestDTO;
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

  /**
   * Give an Object.
   *
   * @param interestDTO : the interest information (id of the object)
   * @return an object
   */
  @Override
  public InterestDTO giveObject(InterestDTO interestDTO) {
    try {
      dalService.startTransaction();

      InterestDTO tmp = interestDAO.getGiveInterest(interestDTO.getObject().getIdObject());
      if (tmp == null) {
        throw new NotFoundException("aucun membre n'a été assigner");
      }
      interestDTO.setIdMember(tmp.getIdMember());
      interestDTO = interestDAO.getOne(interestDTO);

      if (!interestDTO.getObject().getStatus().equals("assigned")) {
        throw new ForbiddenException("aucun objet n'est assigné pour le donner");
      }

      if (!interestDTO.getStatus().equals("assigned")) {
        throw new ForbiddenException("l'intérêt n'est pas assigné");
      }

      interestDTO.setStatus("received");
      interestDTO.getObject().setStatus("given");

      objectDAO.updateOne(interestDTO.getObject());
      interestDTO = interestDAO.updateStatus(interestDTO);

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }

    return interestDTO;
  }

}
