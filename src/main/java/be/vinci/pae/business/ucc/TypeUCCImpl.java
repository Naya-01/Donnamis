package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.TypeDAO;
import jakarta.inject.Inject;
import java.util.List;

public class TypeUCCImpl implements TypeUCC {

  @Inject
  private TypeDAO typeDAO;

  /**
   * Find a type by his id.
   *
   * @param id : id of the type.
   * @return typeDTO having this id.
   */
  @Override
  public TypeDTO getType(int id) {
    TypeDTO typeDTO = typeDAO.getOne(id);
    if (typeDTO == null) {
      throw new NotFoundException("Type not found");
    }
    return typeDTO;
  }

  /**
   * Find a type by his type name.
   *
   * @param type_name : name of the type.
   * @return typeDTO having this name.
   */
  @Override
  public TypeDTO getType(String type_name) {
    TypeDTO typeDTO = typeDAO.getOne(type_name);
    if (typeDTO == null) {
      throw new NotFoundException("Type not found");
    }
    return typeDTO;
  }

  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  @Override
  public List<TypeDTO> getAllDefaultTypes() {
    List<TypeDTO> typeDTO = typeDAO.getAllDefaultTypes();
    if (typeDTO.isEmpty()) {
      throw new NotFoundException("No default types found");
    }
    return typeDTO;
  }
}
