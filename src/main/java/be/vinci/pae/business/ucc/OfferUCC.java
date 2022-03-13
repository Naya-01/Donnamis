package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.OfferDTO;
import java.util.List;

public interface OfferUCC {

  /**
   * Get all the offers that matche with a search pattern.
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a list of all offerDTO that match with the search pattern
   */
  List<OfferDTO> getAllPosts(String searchPattern);

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  List<OfferDTO> getLastOffers();

}
