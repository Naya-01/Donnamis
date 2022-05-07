package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.OfferImpl;
import be.vinci.pae.business.domain.dto.OfferDTO;

public class OfferFactoryImpl implements OfferFactory {

  /**
   * This function is used for the injection, it returns an implementation offer.
   *
   * @return offer implementation
   */
  @Override
  public OfferDTO getOfferDTO() {
    return new OfferImpl() {
    };
  }
}
