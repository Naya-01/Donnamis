package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.RatingImpl;
import be.vinci.pae.business.domain.dto.RatingDTO;

public class RatingFactoryImpl implements RatingFactory {

  @Override
  public RatingDTO getRatingDTO() {
    return new RatingImpl();
  }

}
