package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.TypeDTO;
import java.util.List;

public interface TypeDAO {

  /**
   * Get all default types available
   *
   * @return a list of default types
   */
  List<TypeDTO> getAllDefault();
}
