package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;
import java.util.List;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private OfferDAO offerDAO;
  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private DALService dalService;
  @Inject
  private InterestDAO interestDAO;

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
        throw new NotFoundException("Aucune offres");
      }
      dalService.commitTransaction();

    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
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
    OfferDTO offerDTO;
    try {
      dalService.startTransaction();
      offerDTO = offerDAO.getOne(idOffer, offerDAO.hasOlderOffer(idOffer));
      if (offerDTO == null) {
        throw new NotFoundException("Aucune offres");
      }
      dalService.commitTransaction();

    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
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
    try {
      dalService.startTransaction();

      OfferDTO offer = offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject());

      if (!offer.getStatus().equals("cancelled") && !offer.getStatus().equals("not_collected")) {
        throw new ForbiddenException("La dernière offre n'est pas encore terminer vous ne pouvez "
            + "en créer de nouveau");
      }

      offerDTO = offerDAO.addOne(offerDTO);

      List<InterestDTO> interestDTOList = interestDAO.getAll(offerDTO.getObject().getIdObject());

      if (interestDTOList.size() < 1) {
        offerDTO.getObject().setStatus("available");
      } else {
        offerDTO.getObject().setStatus("interested");
      }

      objectDAO.updateOne(offerDTO.getObject());

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return offerDTO;
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
      offer = offerDAO.updateOne(offerDTO, offerDAO.hasOlderOffer(offerDTO.getIdOffer()));
      if (offer == null) {
        throw new FatalException("Problème lors de la mise à jour de l'offre");
      }

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return offer;
  }


  /**
   * Get all offers.
   *
   * @param search   the search pattern (empty -> all) according to their type, description
   * @param idMember the member id if you want only your offers (0 -> all)
   * @param type     the type of object that we want
   * @return list of offers
   */
  @Override
  public List<OfferDTO> getOffers(String search, int idMember, String type, String objectStatus) {
    List<OfferDTO> offerDTO = null;
    try {
      dalService.startTransaction();
      offerDTO = offerDAO.getAll(search, idMember, type, objectStatus);
      if (offerDTO.isEmpty()) {
        throw new NotFoundException("Aucune offre");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return offerDTO;
  }

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getGivenOffers(int idReceiver) {
    List<OfferDTO> offerDTO;
    try {
      dalService.startTransaction();
      offerDTO = offerDAO.getAllGivenOffers(idReceiver);
      if (offerDTO.isEmpty()) {
        throw new NotFoundException("Aucune offre");
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return offerDTO;
  }


  /**
   * Cancel an Object.
   *
   * @param offerDTO object with his id & new status to 'cancelled'
   * @return an object
   */
  @Override
  public OfferDTO cancelObject(OfferDTO offerDTO) {
    try {
      dalService.startTransaction();

      if (offerDTO.getStatus().equals("given") || offerDTO.getStatus().equals("cancelled")) {
        throw new ForbiddenException("Impossible d'annuler l'offre");
      }

      offerDTO.setStatus("cancelled");
      offerDTO.getObject().setStatus("cancelled");

      offerDTO = offerDAO.updateOne(offerDTO, offerDAO.hasOlderOffer(offerDTO.getIdOffer()));
      offerDTO.setObject(objectDAO.updateOne(offerDTO.getObject()));
      InterestDTO interestDTO = interestDAO
          .getAssignedInterest(offerDTO.getObject().getIdObject());

      if (interestDTO != null) {
        interestDTO.setStatus("published");
        interestDAO.updateStatus(interestDTO);
      }

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }

    return offerDTO;
  }


  /**
   * Mark an object to 'not collected'.
   *
   * @param offerDTO object with his id & new status to 'not collected'
   * @return an object
   */
  @Override
  public OfferDTO notCollectedObject(OfferDTO offerDTO) {
    try {
      dalService.startTransaction();

      if (!offerDTO.getStatus().equals("assigned")) {
        throw new ForbiddenException("Impossible de marquer en non collecté");
      }

      InterestDTO interestDTO = interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject());

      if (interestDTO == null) {
        throw new ForbiddenException("Aucune offre n'a d'offre attribué");
      }

      interestDTO.setStatus("not_collected");
      interestDAO.updateStatus(interestDTO);

      offerDTO.setStatus("not_collected");
      offerDTO.getObject().setStatus("not_collected");

      offerDTO = offerDAO.updateOne(offerDTO, offerDAO.hasOlderOffer(offerDTO.getIdOffer()));
      offerDTO.setObject(objectDAO.updateOne(offerDTO.getObject()));

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }

    return offerDTO;
  }
}
