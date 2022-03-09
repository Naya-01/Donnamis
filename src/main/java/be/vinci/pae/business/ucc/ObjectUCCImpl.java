package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.ObjectDAO;
import jakarta.inject.Inject;

public class ObjectUCCImpl implements ObjectUCC {

  @Inject
  private ObjectDAO objectDAO;

  /**
   * Find an object with his id.
   *
   * @param id : id of the object.
   * @return objectDTO having this id.
   */
  @Override
  public ObjectDTO getObject(int id) {
    ObjectDTO objectDTO = objectDAO.getOne(id);

    if (objectDTO == null) {
      throw new NotFoundException("Objet non trouvé");
    }
    return objectDTO;
  }

}
