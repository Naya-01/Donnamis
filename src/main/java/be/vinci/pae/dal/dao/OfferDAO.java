package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.OfferDTO;
import java.util.List;

public interface OfferDAO {

  /**
   * Get all offers that match with the search pattern.
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a list of offerDTO
   */
  List<OfferDTO> getAll(String searchPattern);

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  List<OfferDTO> getAllLast();

  /**
   * Add an offer in the db.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  OfferDTO addOne(OfferDTO offerDTO);
}
