package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.TypeDAO;
import jakarta.inject.Inject;
import java.util.List;

public class TypeUCCImpl implements TypeUCC {

  @Inject
  private TypeDAO typeDAO;

  /**
   * Get all default types available.
   *
   * @return a list of default types
   */
  @Override
  public List<TypeDTO> getAllDefaultTypes() {
    return typeDAO.getAllDefault();
  }
}
