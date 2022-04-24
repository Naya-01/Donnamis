package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.RatingDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;

public class RatingUCCImpl implements RatingUCC {

  @Inject
  private RatingDAO ratingDAO;
  @Inject
  private MemberDAO memberDAO;
  @Inject
  private InterestDAO interestDAO;
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
      ratingDTO.setMemberRater(memberDAO.getOne(ratingDTO.getIdMember()));
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return ratingDTO;
  }

  /**
   * Add a rating.
   *
   * @param ratingDTO : the rating to add.
   * @return ratingDTO that has been added.
   */
  @Override
  public RatingDTO addRating(RatingDTO ratingDTO) {
    RatingDTO rating;
    try {
      dalService.startTransaction();
      if (ratingDAO.getOne(ratingDTO.getIdObject()) != null) { // if there is already a rating
        throw new ForbiddenException("Une note existe déjà pour cet objet");
      }
      InterestDTO interestDTO =
          interestDAO.getOne(ratingDTO.getIdObject(), ratingDTO.getIdMember());
      if (interestDTO == null || !interestDTO.getStatus().equals("received")) {
        throw new ForbiddenException("Ce membre n'a pas reçu cet objet.");
      }
      rating = ratingDAO.addOne(ratingDTO);
      rating.setMemberRater(memberDAO.getOne(rating.getIdMember()));
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return rating;
  }
}
