package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private OfferDAO offerDAO;
  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private TypeDAO typeDAO;

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
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer or an error if offer not found
   */
  @Override
  public OfferDTO getOfferById(int idOffer) {
    OfferDTO offerDTO = offerDAO.getOne(idOffer);
    if (offerDTO == null) {
      throw new NotFoundException("Aucune offres");
    }
    return offerDTO;
  }

  /**
   * Add an offer in the db with out without an object.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  @Override
  public OfferDTO addOffer(OfferDTO offerDTO) {
    TypeDTO typeDTO;
    if (offerDTO.getObject().getType().getTypeName() != null && !offerDTO.getObject().getType()
        .getTypeName().isEmpty()) {
      typeDTO = typeDAO.getOne(offerDTO.getObject().getType().getTypeName());
      if (typeDTO == null) {
        typeDTO = typeDAO.addOne(offerDTO.getObject().getType().getTypeName());
        if (typeDTO == null) {
          throw new WebApplicationException("Problème lors de la création du type",
              Response.Status.BAD_REQUEST);
        }
      }
    } else {
      typeDTO = typeDAO.getOne(offerDTO.getObject().getType().getIdType());
    }
    offerDTO.getObject().setType(typeDTO);

    if (offerDTO.getObject().getIdObject() == 0) {
      objectDAO.addOne(offerDTO.getObject());
      if (offerDTO.getObject().getIdObject() == 0) {
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

  /**
   * Update the time slot of an offer or an errorcode.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  @Override
  public OfferDTO updateOffer(OfferDTO offerDTO) {
    TypeDTO typeDTO;
    if (offerDTO.getObject().getType().getTypeName() != null && !offerDTO.getObject().getType()
        .getTypeName().isEmpty()) {
      typeDTO = typeDAO.getOne(offerDTO.getObject().getType().getTypeName());
      if (typeDTO == null) {
        typeDTO = typeDAO.addOne(offerDTO.getObject().getType().getTypeName());
        if (typeDTO == null) {
          throw new WebApplicationException("Problème lors de la création du type",
              Response.Status.BAD_REQUEST);
        }
      }
    } else {
      typeDTO = typeDAO.getOne(offerDTO.getObject().getType().getIdType());
    }
    offerDTO.getObject().setType(typeDTO);

    OfferDTO offer = offerDAO.updateOne(offerDTO);
    if (offer == null) {
      throw new WebApplicationException("Problème lors de la mise à jour du time slot",
          Response.Status.BAD_REQUEST);
    }

    ObjectDTO objectDTO = objectDAO.updateOne(offerDTO.getObject());
    if (objectDTO == null) {
      throw new WebApplicationException("Problème lors de la mise à jour de l'objet",
          Response.Status.BAD_REQUEST);
    }

    return offer;
  }
}
