package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.TypeDTO;
import java.util.List;

public interface TypeDAO {

  /**
   * Get a type we want to retrieve by his type name.
   *
   * @param typeName : the typeName of the type we want to retrieve
   * @return the type
   */
  TypeDTO getOne(String typeName);

  /**
   * Get a type we want to retrieve by his id.
   *
   * @param typeId : the id of the type we want to retrieve
   * @return the type
   */
  TypeDTO getOne(int typeId);

  /**
   * Get all types that are default.
   *
   * @return a list with all types
   */
  List<TypeDTO> getAllDefaultTypes();

  /**
   * Insert a new type in the db.
   *
   * @param typeName the name of the type
   * @return a typeDTO with all the informations of the new type added
   */
  TypeDTO addOne(String typeName);
}
