package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.TypeDTO;
import java.util.List;

public interface TypeUCC {

  /**
   * Find a type by his id.
   *
   * @param id : id of the type.
   * @return typeDTO having this id.
   */
  TypeDTO getType(int id);

  /**
   * Find a type by his type name.
   *
   * @param typeName : name of the type.
   * @return typeDTO having this name.
   */
  TypeDTO getType(String typeName);


  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  List<TypeDTO> getAllDefaultTypes();
}
