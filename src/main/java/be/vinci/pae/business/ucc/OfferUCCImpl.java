package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.OfferDAO;
import jakarta.inject.Inject;
import java.util.List;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private OfferDAO offerDAO;

  /**
   * Get all the offers that matche with a search pattern.
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a list of all offerDTO that match with the search pattern
   */
  @Override
  public List<OfferDTO> getAllPosts(String searchPattern) {
    List<OfferDTO> offers = offerDAO.getAll(searchPattern);
    if (offers.isEmpty()) {
      throw new NotFoundException("Aucune offres");
    }
    return offers;
  }

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @Override
  public List<OfferDTO> getLastOffers() {
    List<OfferDTO> offers = offerDAO.getAllLast();
    if (offers.isEmpty()) {
      throw new NotFoundException("Aucune offres");
    }
    return offers;
  }
}
