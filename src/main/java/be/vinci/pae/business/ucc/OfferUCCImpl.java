package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.util.List;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private OfferDAO offerDAO;
  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private TypeDAO typeDAO;
  @Inject
  private DALService dalService;

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @Override
  public List<OfferDTO> getLastOffers() {
    List<OfferDTO> offers;
    try {
      dalService.startTransaction();
      offers = offerDAO.getAllLast();
      if (offers.isEmpty()) {
        dalService.rollBackTransaction();
        throw new NotFoundException("Aucune offres");
      }
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
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
    OfferDTO offerDTO;
    try {
      dalService.startTransaction();
      offerDTO = offerDAO.getOne(idOffer);
      if (offerDTO == null) {
        dalService.rollBackTransaction();
        throw new NotFoundException("Aucune offres");
      }
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
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
    OfferDTO offer;
    try {
      dalService.startTransaction();
      setCorrectType(offerDTO);

      if (offerDTO.getObject().getIdObject() == 0) {
        objectDAO.addOne(offerDTO.getObject());
        if (offerDTO.getObject().getIdObject() == 0) {
          throw new BadRequestException("Problème lors de la création d'un objet");
        }
      }
      offer = offerDAO.addOne(offerDTO);
      if (offer.getIdOffer() == 0) {
        throw new BadRequestException("Problème lors de la création d'une offre");
      }
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
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
    OfferDTO offer;
    try {
      dalService.startTransaction();
      setCorrectType(offerDTO);

      offer = offerDAO.updateOne(offerDTO);
      if (offer == null) {
        throw new BadRequestException("Problème lors de la mise à jour du time slot");
      }

      ObjectDTO objectDTO = objectDAO.updateOne(offerDTO.getObject());
      if (objectDTO == null) {
        throw new BadRequestException("Problème lors de la mise à jour de l'objet");
      }
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
    return offer;
  }

  /**
   * Verify the type and set it.
   *
   * @param offerDTO the offer that has an object that has a type.
   */
  private void setCorrectType(OfferDTO offerDTO) {
    TypeDTO typeDTO;
      if (offerDTO.getObject().getType().getTypeName() != null && !offerDTO.getObject().getType()
          .getTypeName().isEmpty()) {
        typeDTO = typeDAO.getOne(offerDTO.getObject().getType().getTypeName());
        if (typeDTO == null) {
          typeDTO = typeDAO.addOne(offerDTO.getObject().getType().getTypeName());
          if (typeDTO == null) {
            throw new BadRequestException("Problème lors de la création du type");
          }
        }
      } else {
        typeDTO = typeDAO.getOne(offerDTO.getObject().getType().getIdType());
      }
      offerDTO.getObject().setType(typeDTO);
  }

  /**
   * Get all offers.
   *
   * @param search   the search pattern (empty -> all) according to their type, description
   * @param idMember the member id if you want only your offers (0 -> all)
   * @return list of offers
   */
  @Override
  public List<OfferDTO> getOffers(String search, int idMember) {
    List<OfferDTO> offerDTO = null;
    try {
      dalService.startTransaction();
      offerDTO = offerDAO.getAll(search, idMember);
      if (offerDTO == null) {
        dalService.rollBackTransaction();
        throw new NotFoundException("Aucune offre");
      }
    } catch (NotFoundException e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
    return offerDTO;
  }
}
