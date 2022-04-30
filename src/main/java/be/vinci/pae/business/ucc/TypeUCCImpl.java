package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
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
    TypeDTO typeDTO;
    try {
      dalService.startTransaction();
      typeDTO = typeDAO.getOne(id);
      if (typeDTO == null) {
        throw new NotFoundException("Type non trouvé");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
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
    try {
      dalService.startTransaction();
      TypeDTO typeDTO = typeDAO.getOne(typeName);
      if (typeDTO == null) {
        throw new NotFoundException("Type non trouvé");
      }
      dalService.commitTransaction();
      return typeDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  @Override
  public List<TypeDTO> getAllDefaultTypes() {
    List<TypeDTO> typeDTO;
    try {
      dalService.startTransaction();
      typeDTO = typeDAO.getAllDefaultTypes();
      if (typeDTO.isEmpty()) {
        throw new NotFoundException("Pas de types par défaut trouvé");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return typeDTO;
  }
}
