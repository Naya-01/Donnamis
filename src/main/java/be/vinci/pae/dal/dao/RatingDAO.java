package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.RatingDTO;

public interface RatingDAO {
  /**
   * Get a rating we want to retrieve by his object id.
   *
   * @param id : the id of the object of the rating we want to retrieve
   * @return the rating
   */
  RatingDTO getOne(int id);

  /**
   * Add a rating.
   *
   * @param ratingDTO : the rating to add.
   * @return ratingDTO that has been added.
   */
  RatingDTO addOne(RatingDTO ratingDTO);
}
