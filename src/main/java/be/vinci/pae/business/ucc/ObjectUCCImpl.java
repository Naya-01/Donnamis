package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class ObjectUCCImpl implements ObjectUCC {

  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private DALService dalService;

  /**
   * Get the picture of an object.
   *
   * @param id of the object
   * @return picture as file
   */
  public BufferedImage getPicture(int id) {
    ObjectDTO objectDTO;
    BufferedImage picture;
    try {
      dalService.startTransaction();
      objectDTO = objectDAO.getOne(id);

      if (objectDTO == null) {
        throw new NotFoundException("Objet non trouvé");
      }

      try {
        File file = new File(objectDTO.getImage());
        picture = ImageIO.read(file);
      } catch (IOException e) {
        throw new NotFoundException("Image inexistante sur le disque");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return picture;
  }


  /**
   * Find an object with his id.
   *
   * @param id : id of the object.
   * @return objectDTO having this id.
   */
  @Override
  public ObjectDTO getObject(int id) {
    ObjectDTO objectDTO;
    try {
      dalService.startTransaction();
      objectDTO = objectDAO.getOne(id);
      if (objectDTO == null) {
        throw new NotFoundException("Objet non trouvé");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
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
    List<ObjectDTO> objectDTOList;
    try {
      dalService.startTransaction();
      objectDTOList = objectDAO.getAllObjectOfMember(idMember);

      if (objectDTOList.isEmpty()) {
        throw new NotFoundException("Aucun objet pour ce membre");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return objectDTOList;
  }

  /**
   * Update an object.
   *
   * @param objectDTO : object that we want to update.
   * @return object updated
   */
  @Override
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    ObjectDTO object;
    try {
      dalService.startTransaction();
      object = objectDAO.getOne(objectDTO.getIdObject());
      if (object == null) {
        throw new NotFoundException("Objet non trouvé");
      }
      if (!object.getVersion().equals(objectDTO.getVersion())) {
        throw new ForbiddenException("Vous n'avez pas la dernière version de l'objet.");
      }
      object = objectDAO.updateOne(objectDTO);
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return object;
  }

  /**
   * Update the object picture.
   *
   * @param internalPath location of the picture.
   * @param id           of the object.
   * @param version      of the object
   * @param memberId     owner of the object.
   * @return Object modified.
   */
  @Override
  public ObjectDTO updateObjectPicture(String internalPath, int id, Integer memberId,
      int version) {
    ObjectDTO objectDTO;
    try {
      dalService.startTransaction();
      objectDTO = objectDAO.getOne(id);
      if (objectDTO == null) {
        throw new NotFoundException("Objet non trouvé");
      }

      if (!objectDTO.getIdOfferor().equals(memberId)) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      if (!objectDTO.getVersion().equals(version)) {
        throw new ForbiddenException("Vous n'avez pas la dernière version de l'objet.");
      }

      File f = new File(Config.getProperty("ImagePath") + objectDTO.getImage());
      if (f.exists()) {
        f.delete();
      }

      objectDTO = objectDAO.updateObjectPicture(internalPath, id);
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return objectDTO;
  }


}
