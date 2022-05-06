package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.dto.OfferDTO;

public interface OfferFactory {

  /**
   * This function is used for the injection, it returns an implementation offer.
   *
   * @return offer implementation
   */
  OfferDTO getOfferDTO();
}
