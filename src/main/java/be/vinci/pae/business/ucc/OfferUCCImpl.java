package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private OfferDAO offerDAO;
  @Inject
  private ObjectDAO objectDAO;

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

  /**
   * Add an offer in the db with out without an object.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  @Override
  public OfferDTO addOffer(OfferDTO offerDTO) {
    if (offerDTO.getObject().getIdObject() == null) {
      objectDAO.addOne(offerDTO.getObject());
      if (offerDTO.getObject().getIdObject() == null) {
        throw new WebApplicationException("Problème lors de la création d'un objet",
            Response.Status.BAD_REQUEST);
      }
    }
    OfferDTO offer = offerDAO.addOne(offerDTO);
    if (offer.getIdOffer() == 0) {
      throw new WebApplicationException("Problème lors de la création d'une offre",
          Response.Status.BAD_REQUEST);
    }
    return offer;
  }
}
