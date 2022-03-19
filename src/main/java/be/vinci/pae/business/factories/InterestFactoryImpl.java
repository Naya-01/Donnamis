package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.InterestImpl;
import be.vinci.pae.business.domain.dto.InterestDTO;

public class InterestFactoryImpl implements InterestFactory {

  /**
   * This function is used for the injection, it returns an implementation interest.
   *
   * @return interest implementation.
   */
  @Override
  public InterestDTO getInterestDTO() {
    return new InterestImpl();
  }
}
