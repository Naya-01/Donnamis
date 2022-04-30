package be.vinci.pae.business.factories;

import be.vinci.pae.business.domain.OfferImpl;
import be.vinci.pae.business.domain.dto.OfferDTO;

public class OfferFactoryImpl implements OfferFactory {

  @Override
  public OfferDTO getOfferDTO() {
    return new OfferImpl() {
    };
  }
}
