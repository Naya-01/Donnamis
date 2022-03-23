package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;
import java.util.List;

public class ObjectUCCImpl implements ObjectUCC {

  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private DALService dalService;

  /**
   * Find an object with his id.
   *
   * @param id : id of the object.
   * @return objectDTO having this id.
   */
  @Override
  public ObjectDTO getObject(int id) {
    dalService.startTransaction();
    ObjectDTO objectDTO = objectDAO.getOne(id);

    if (objectDTO == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Objet non trouvé");
    }
    dalService.commitTransaction();
    return objectDTO;
  }

  /**
   * Find all object of a member.
   *
   * @param idMember : id member that we want to get all his object.
   * @return object list of this member.
   */
  @Override
  public List<ObjectDTO> getAllObjectMember(int idMember) {
    dalService.startTransaction();
    List<ObjectDTO> objectDTOList = objectDAO.getAllObjectOfMember(idMember);

    if (objectDTOList.isEmpty()) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Aucun objet pour ce membre");
    }
    dalService.commitTransaction();
    return objectDTOList;
  }

  /**
   * Update an object.
   *
   * @param objectDTO : object that we want to update.
   * @return object updated
   */
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    dalService.startTransaction();
    ObjectDTO object = objectDAO.getOne(objectDTO.getIdObject());
    if (object == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Object not found");
    }
    object = objectDAO.updateOne(objectDTO);
    dalService.commitTransaction();
    return object;
  }
}
