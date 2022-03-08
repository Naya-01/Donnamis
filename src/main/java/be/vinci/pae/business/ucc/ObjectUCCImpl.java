package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.ObjectDAO;
import jakarta.inject.Inject;

public class ObjectUCCImpl implements ObjectUCC {

  @Inject
  private ObjectDAO objectDAO;

  @Override
  public ObjectDTO getObject(int id) {
    ObjectDTO objectDTO = objectDAO.getOne(id);

    if (objectDTO == null) {
      throw new NotFoundException("Objet non trouvé");
    }
    return objectDTO;
  }

}
