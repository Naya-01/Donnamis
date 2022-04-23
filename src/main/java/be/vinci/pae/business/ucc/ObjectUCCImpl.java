package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
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
  @Inject
  private TypeDAO typeDAO;
  @Inject
  private OfferDAO offerDAO;

  /**
   * Get the picture of an object.
   *
   * @param id of the oject
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
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    ObjectDTO object;
    try {
      dalService.startTransaction();
      object = objectDAO.getOne(objectDTO.getIdObject());
      if (object == null) {
        throw new NotFoundException("Object not found");
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
   * @return Object modified.
   */
  @Override
  public ObjectDTO updateObjectPicture(String internalPath, int id) {
    ObjectDTO objectDTO = null;
    try {
      dalService.startTransaction();
      objectDTO = objectDAO.getOne(id);
      if (objectDTO == null) {
        throw new NotFoundException("Object not found");
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

  @Override
  public OfferDTO addObject(OfferDTO offerDTO) {
    OfferDTO offer;
    try {
      dalService.startTransaction();
      setCorrectType(offerDTO.getObject());
      ObjectDTO objectDTO = objectDAO.addOne(offerDTO.getObject());
      offerDTO.setObject(objectDTO);
      offerDTO.setStatus("available");
      offer = offerDAO.addOne(offerDTO);

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return offer;
  }


  /**
   * Verify the type and set it.
   *
   * @param objectDTO the offer that has an object that has a type.
   */
  private void setCorrectType(ObjectDTO objectDTO) {
    TypeDTO typeDTO;
    if (objectDTO.getType().getTypeName() != null && !objectDTO.getType()
        .getTypeName().isBlank()) {
      typeDTO = typeDAO.getOne(objectDTO.getType().getTypeName());

      if (typeDTO == null) {
        typeDTO = typeDAO.addOne(objectDTO.getType().getTypeName());
      }
    } else {
      typeDTO = typeDAO.getOne(objectDTO.getType().getIdType());
    }
    objectDTO.setType(typeDTO);
  }


}
