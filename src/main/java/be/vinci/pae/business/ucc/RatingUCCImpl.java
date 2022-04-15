package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.RatingDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;

public class RatingUCCImpl implements RatingUCC {

  @Inject
  private RatingDAO ratingDAO;
  @Inject
  private DALService dalService;

  /**
   * Find a rating by the id of the object.
   *
   * @param id : id of the object.
   * @return ratingDTO having as object id the id in param.
   */
  @Override
  public RatingDTO getOne(int id) {
    RatingDTO ratingDTO;
    try {
      dalService.startTransaction();
      ratingDTO = ratingDAO.getOne(id);
      if (ratingDTO == null) {
        throw new NotFoundException("Note non trouvé");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return ratingDTO;
  }
}
