package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.InterestDTO;

public interface InterestFactory {

  /**
   * This function is used for the injection, it returns an implementation interest.
   *
   * @return type implementation.
   */
  InterestDTO getInterestDTO();
}
