package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.util.List;

public class TypeUCCImpl implements TypeUCC {

  @Inject
  private TypeDAO typeDAO;
  @Inject
  private DALService dalService;

  /**
   * Find a type by his id.
   *
   * @param id : id of the type.
   * @return typeDTO having this id.
   */
  @Override
  public TypeDTO getType(int id) {
    dalService.startTransaction();
    TypeDTO typeDTO = typeDAO.getOne(id);
    if (typeDTO == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Type not found");
    }
    dalService.commitTransaction();
    return typeDTO;
  }

  /**
   * Find a type by his type name.
   *
   * @param typeName : name of the type.
   * @return typeDTO having this name.
   */
  @Override
  public TypeDTO getType(String typeName) {
    dalService.startTransaction();
    TypeDTO typeDTO = typeDAO.getOne(typeName);
    if (typeDTO == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Type not found");
    }
    dalService.commitTransaction();
    return typeDTO;
  }

  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  @Override
  public List<TypeDTO> getAllDefaultTypes() {
    dalService.startTransaction();
    List<TypeDTO> typeDTO = typeDAO.getAllDefaultTypes();
    if (typeDTO.isEmpty()) {
      dalService.rollBackTransaction();
      throw new NotFoundException("No default types found");
    }
    dalService.commitTransaction();
    return typeDTO;
  }
}
