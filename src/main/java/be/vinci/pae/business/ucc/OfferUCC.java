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

  /**
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer or an error if offer not found
   */
  OfferDTO getOfferById(int idOffer);

  /**
   * Add an offer in the db with out without an object.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  OfferDTO addOffer(OfferDTO offerDTO);

  /**
   * Update the time slot of an offer or an errorcode.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  OfferDTO updateOffer(OfferDTO offerDTO);
}
