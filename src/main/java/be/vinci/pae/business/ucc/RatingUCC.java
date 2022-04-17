package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.RatingDTO;

public interface RatingUCC {

  /**
   * Find a rating by the id of the object.
   *
   * @param id : id of the object.
   * @return ratingDTO having as object id the id in param.
   */
  RatingDTO getOne(int id);

  /**
   * Add a rating.
   *
   * @param ratingDTO : the rating to add.
   * @return ratingDTO that has been added.
   */
  RatingDTO addRating(RatingDTO ratingDTO);

}
