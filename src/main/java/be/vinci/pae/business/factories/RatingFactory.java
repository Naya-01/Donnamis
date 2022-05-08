package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.RatingDTO;

public interface RatingFactory {

  /**
   * This function is used for the injection, it returns an implementation rating.
   *
   * @return rating implementation
   */
  RatingDTO getRatingDTO();

}
