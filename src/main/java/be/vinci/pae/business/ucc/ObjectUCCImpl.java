package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.io.File;
import java.util.List;

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

  /**
   * Find all object of a member.
   *
   * @param idMember : id member that we want to get all his object.
   * @return object list of this member.
   */
  @Override
  public List<ObjectDTO> getAllObjectMember(int idMember) {
    List<ObjectDTO> objectDTOList = objectDAO.getAllObjectOfMember(idMember);

    if (objectDTOList.isEmpty()) {
      throw new NotFoundException("Aucun objet pour ce membre");
    }
    return objectDTOList;
  }

  /**
   * Create an object.
   *
   * @param objectDTO : object that we want to create.
   * @return return the added object
   */
  public ObjectDTO addOne(ObjectDTO objectDTO) {

    return objectDAO.addOne(objectDTO);
  }

  /**
   * Update an object.
   *
   * @param objectDTO : object that we want to update.
   * @return object updated
   */
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    return objectDAO.updateOne(objectDTO);
  }

  /**
   * Update the object picture.
   *
   * @param internalPath location of the picture.
   * @param id           of the object.
   * @return Object modified.
   */
  @Override
  public ObjectDTO updateObjectPicture(String internalPath, int id) {
    ObjectDTO objectDTO = objectDAO.getOne(id);
    if (objectDTO == null) {
      throw new NotFoundException("Member not found");
    }

    if (objectDTO.getImage() != null) {
      File f = new File(Config.getProperty("ImagePath") + objectDTO.getImage());
      if (f.exists()) {
        f.delete();
      }

    }

    objectDTO = objectDAO.updateObjectPicture(internalPath, id);

    return objectDTO;
  }
}
