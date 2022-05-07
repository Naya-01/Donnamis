package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.RatingImpl;
import be.vinci.pae.business.domain.dto.RatingDTO;

public class RatingFactoryImpl implements RatingFactory {

  /**
   * This function is used for the injection, it returns an implementation rating.
   *
   * @return rating implementation
   */
  @Override
  public RatingDTO getRatingDTO() {
    return new RatingImpl();
  }

}
