package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private DALService dalService;
  @Inject
  private OfferDAO offerDAO;
  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private InterestDAO interestDAO;
  @Inject
  private TypeDAO typeDAO;

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @Override
  public List<OfferDTO> getLastOffers() {
    try {
      dalService.startTransaction();
      List<OfferDTO> offers = offerDAO.getAllLast();
      if (offers.isEmpty()) {
        throw new NotFoundException("Aucune offres");
      }
      dalService.commitTransaction();
      return offers;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer or an error if offer not found
   */
  @Override
  public OfferDTO getOfferById(int idOffer) {
    try {
      dalService.startTransaction();
      OfferDTO offerDTO = offerDAO.getOne(idOffer);
      if (offerDTO == null) {
        throw new NotFoundException("Aucune offres");
      }
      dalService.commitTransaction();
      return offerDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Add an offer in the db without an object.
   *
   * @param offerDTO an offer we want to add in the db
   * @param ownerDTO member object
   * @return the offerDTO added
   */
  @Override
  public OfferDTO addOffer(OfferDTO offerDTO, MemberDTO ownerDTO) {
    try {
      dalService.startTransaction();

      offerDTO = offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject());

      if (offerDTO == null) {
        throw new NotFoundException("cet object n'existe pas");
      }

      if (!ownerDTO.getMemberId().equals(offerDTO.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      if (!offerDTO.getStatus().equals("cancelled") && !offerDTO.getStatus()
          .equals("not_collected")) {
        throw new ForbiddenException("La dernière offre n'est pas encore terminer vous ne pouvez "
            + "en créer de nouveau");
      }

      int nbInterests = interestDAO.getAllCount(offerDTO.getObject().getIdObject());

      if (nbInterests < 1) {
        offerDTO.getObject().setStatus("available"); // object
        offerDTO.setStatus("available"); // offer
      } else {
        offerDTO.getObject().setStatus("interested"); // object
        offerDTO.setStatus("interested"); // offer
      }

      offerDTO = offerDAO.addOne(offerDTO);
      objectDAO.updateOne(offerDTO.getObject());

      dalService.commitTransaction();
      return offerDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Update the time slot of an offer or an error code.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  @Override
  public OfferDTO updateOffer(OfferDTO offerDTO) {
    try {
      dalService.startTransaction();

      OfferDTO offerFromDB = offerDAO.getOne(offerDTO.getIdOffer());
      if (offerFromDB == null) {
        throw new NotFoundException("Aucune offre");
      }

      if (!offerFromDB.getVersion().equals(offerDTO.getVersion())) {
        throw new ForbiddenException("Les versions de l'offre ne correspondent pas");
      }

      OfferDTO updatedOffer = offerDAO.updateOne(offerDTO);
      if (offerDTO.getObject() != null) {
        if (!updatedOffer.getObject().getVersion().equals(offerDTO.getObject().getVersion())) {
          throw new ForbiddenException("Les versions de l'objet ne correspondent pas");
        }
        offerDTO.getObject().setIdObject(offerFromDB.getObject().getIdObject());
        updatedOffer.setObject(objectDAO.updateOne(offerDTO.getObject()));
      }

      dalService.commitTransaction();
      return updatedOffer;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }


  /**
   * Get all offers.
   *
   * @param search       the search pattern (empty -> all) according to their type, description
   * @param idMember     the member id if you want only your offers (0 -> all)
   * @param type         the type of object that we want
   * @param objectStatus the status of object that we want
   * @param dateText      the max date late
   * @return list of offers
   */
  @Override
  public List<OfferDTO> getOffers(String search, int idMember, String type, String objectStatus,
      String dateText) {
    try {
      dalService.startTransaction();
      List<OfferDTO> offerDTO = offerDAO.getAll(search, idMember, type, objectStatus, dateText);
      if (offerDTO.isEmpty()) {
        throw new NotFoundException("Aucune offre");
      }
      dalService.commitTransaction();
      return offerDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Return the last offer of an object.
   *
   * @param idObject to search.
   * @return last offer.
   */
  @Override
  public OfferDTO getLastOffer(int idObject) {
    try {
      dalService.startTransaction();
      OfferDTO offerDTO = offerDAO.getLastObjectOffer(idObject);
      if (offerDTO == null) {
        throw new NotFoundException("Aucune offre");
      }
      dalService.commitTransaction();
      return offerDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getGivenOffers(int idReceiver) {
    try {
      dalService.startTransaction();
      List<OfferDTO> givenOffers = offerDAO.getAllGivenOffers(idReceiver);
      if (givenOffers.isEmpty()) {
        throw new NotFoundException("Aucune offre");
      }
      dalService.commitTransaction();
      return givenOffers;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get all offers received by a member.
   *
   * @param receiver the receiver
   * @param searchPattern the search pattern (empty -> all) according to their type, description
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getGivenAndAssignedOffers(MemberDTO receiver, String searchPattern) {
    try {
      dalService.startTransaction();
      List<OfferDTO> givenOffers =
          offerDAO.getAllGivenAndAssignedOffers(receiver.getMemberId(), searchPattern);
      if (givenOffers.isEmpty()) {
        throw new NotFoundException("Aucune offre");
      }
      dalService.commitTransaction();
      return givenOffers;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }


  /**
   * Cancel an Object.
   *
   * @param offerDTO object with his id & set the status to 'cancelled'
   * @param ownerDTO member object
   * @return an object
   */
  @Override
  public OfferDTO cancelOffer(OfferDTO offerDTO, MemberDTO ownerDTO) {
    try {
      dalService.startTransaction();

      // Retrieve offer from the DB
      OfferDTO offerFromDB = offerDAO.getOne(offerDTO.getIdOffer());
      if (offerFromDB == null) {
        throw new NotFoundException("Cette offre n'existe pas");
      }

      // Check if the offeror don't change the offer of anyone else
      if (!ownerDTO.getMemberId().equals(offerFromDB.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      // Check if the offer has the correct status
      if (offerFromDB.getStatus().equals("given") || offerFromDB.getStatus().equals("cancelled")) {
        throw new ForbiddenException("Impossible d'annuler l'offre");
      }

      // Check the version of the offer
      if (!offerFromDB.getVersion().equals(offerDTO.getVersion())) {
        throw new ForbiddenException("Les versions de l'offre ne correspondent pas");
      }

      // Check version of the object
      if (!offerFromDB.getObject().getVersion().equals(offerDTO.getObject().getVersion())) {
        throw new ForbiddenException("Les versions de l'objet ne correspondent pas");
      }

      // Change offerDB status and update
      offerFromDB.setStatus("cancelled");
      OfferDTO updatedOffer = offerDAO.updateOne(offerFromDB);

      // Change object from offerDB status, add version to the object and update
      offerFromDB.getObject().setStatus("cancelled");
      updatedOffer.setObject(objectDAO.updateOne(offerFromDB.getObject()));

      // Retrieve assigned interest for the object
      InterestDTO interestDTO
          = interestDAO.getAssignedInterest(offerFromDB.getObject().getIdObject());

      if (interestDTO != null) {
        // Update notification of the interest
        interestDTO.setIsNotificated(true);
        interestDTO.setNotificationDate(LocalDate.now());
        interestDAO.updateNotification(interestDTO);

        // Update status of the interest
        interestDTO.setStatus("published");
        interestDAO.updateStatus(interestDTO);
      }

      dalService.commitTransaction();
      return updatedOffer;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }


  /**
   * Mark an object to 'not collected'.
   *
   * @param offerDTO object with his id & set the status to 'not collected'
   * @param ownerDTO member object
   * @return an object
   */
  @Override
  public OfferDTO notCollectedOffer(OfferDTO offerDTO, MemberDTO ownerDTO) {
    try {
      dalService.startTransaction();

      // Retrieve offer from the DB
      OfferDTO offerFromDB = offerDAO.getOne(offerDTO.getIdOffer());

      if (offerFromDB == null) {
        throw new NotFoundException("L'offre n'existe pas");
      }

      // Check if the offeror don't change the offer of anyone else
      if (!ownerDTO.getMemberId().equals(offerFromDB.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      // Check version of the offer
      if (!offerFromDB.getVersion().equals(offerDTO.getVersion())) {
        throw new ForbiddenException("Les versions de l'offre ne correspondent pas");
      }

      // Check version of the object
      if (!offerFromDB.getObject().getVersion().equals(offerDTO.getObject().getVersion())) {
        throw new ForbiddenException("Les versions de l'objet ne correspondent pas");
      }

      // Check if the object is assigned
      InterestDTO interestDTO =
          interestDAO.getAssignedInterest(offerFromDB.getObject().getIdObject());
      if (interestDTO == null) {
        throw new NotFoundException("aucun membre n'a été assigner");
      }

      // Retrieve the last offer of an object
      offerFromDB = offerDAO.getLastObjectOffer(offerFromDB.getObject().getIdObject());

      // Check if the offer is assigned
      if (!offerFromDB.getStatus().equals("assigned")) {
        throw new ForbiddenException(
            "aucune offre attribuée n'existe pour que l'objet puisse être non collecté");
      }

      // update the notification of the interest
      interestDTO.setIsNotificated(true);
      interestDTO.setNotificationDate(LocalDate.now());
      interestDAO.updateNotification(interestDTO);

      // Update status of the interest
      interestDTO.setStatus("not_collected");
      interestDAO.updateStatus(interestDTO);

      // Update the status of the offer
      offerFromDB.setStatus("not_collected");
      OfferDTO updatedOffer = offerDAO.updateOne(offerFromDB);

      // Update the status of the object
      offerFromDB.getObject().setStatus("not_collected");
      updatedOffer.setObject(objectDAO.updateOne(offerFromDB.getObject()));

      dalService.commitTransaction();
      return updatedOffer;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }


  /**
   * Give an Object, set the status to 'given'.
   *
   * @param offerDTO : object with his id'
   * @param ownerDTO member object
   * @return an object
   */
  @Override
  public OfferDTO giveOffer(OfferDTO offerDTO, MemberDTO ownerDTO) {
    try {
      dalService.startTransaction();

      // Get offer from db
      OfferDTO offerFromDB = offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject());
      if (offerFromDB == null) {
        throw new NotFoundException("Cette offre n'existe pas");
      }

      // Check version of the offer
      if (!offerFromDB.getVersion().equals(offerDTO.getVersion())) {
        throw new ForbiddenException("Les versions de l'offre ne correspondent pas");
      }

      // Check version of the object
      if (!offerFromDB.getObject().getVersion().equals(offerDTO.getObject().getVersion())) {
        throw new ForbiddenException("Les versions de l'objet ne correspondent pas");
      }

      // Check if the offeror don't change the offer of anyone else
      if (!ownerDTO.getMemberId().equals(offerFromDB.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      // Get the assigned interest
      InterestDTO interestDTO =
          interestDAO.getAssignedInterest(offerFromDB.getObject().getIdObject());
      if (interestDTO == null) {
        throw new NotFoundException("aucun membre n'a été assigner");
      }

      // Get offer from db
      offerFromDB = offerDAO.getLastObjectOffer(offerFromDB.getObject().getIdObject());
      if (!offerFromDB.getStatus().equals("assigned")) {
        throw new ForbiddenException(
            "aucune offre attribuée n'existe pour que l'objet puisse être donné");
      }

      // Update interest notification
      interestDTO.setIsNotificated(true);
      interestDTO.setNotificationDate(LocalDate.now());
      interestDAO.updateNotification(interestDTO);

      // Update interest status
      interestDTO.setStatus("received");
      interestDAO.updateStatus(interestDTO);

      // Update offer
      offerFromDB.setStatus("given");
      OfferDTO updatedOffer = offerDAO.updateOne(offerFromDB);

      // Update object
      offerFromDB.getObject().setStatus("given");
      updatedOffer.setObject(objectDAO.updateOne(offerFromDB.getObject()));

      dalService.commitTransaction();
      return updatedOffer;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get a map of data about a member (nb of received object, nb of not colected objects, nb of
   * given objects and nb of total offers).
   *
   * @param idReceiver the id of the member
   * @return a map with all th datas.
   */
  @Override
  public Map<String, Integer> getOffersCount(int idReceiver) {
    try {
      dalService.startTransaction();
      Map<String, Integer> map = offerDAO.getOffersCount(idReceiver);
      if (map == null) {
        throw new NotFoundException("Aucune donnée pour ce membre");
      }
      dalService.commitTransaction();
      return map;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Make an Object with his offer.
   *
   * @param offerDTO object that contain id object & offerDTO information
   * @return offer
   */
  @Override
  public OfferDTO addObject(OfferDTO offerDTO) {
    try {
      dalService.startTransaction();
      setCorrectType(offerDTO.getObject());
      ObjectDTO objectDTO = objectDAO.addOne(offerDTO.getObject());
      offerDTO.setObject(objectDTO);
      offerDTO.setStatus("available");
      OfferDTO offer = offerDAO.addOne(offerDTO);
      dalService.commitTransaction();
      return offer;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Verify the type and set it.
   *
   * @param objectDTO the offer that has an object that has a type.
   */
  private void setCorrectType(ObjectDTO objectDTO) {
    TypeDTO typeDTO;
    if (objectDTO.getType().getTypeName() != null && !objectDTO.getType()
        .getTypeName().isBlank()) {
      typeDTO = typeDAO.getOne(objectDTO.getType().getTypeName());

      if (typeDTO == null) {
        typeDTO = typeDAO.addOne(objectDTO.getType().getTypeName());
      }
    } else {
      typeDTO = typeDAO.getOne(objectDTO.getType().getIdType());
    }
    objectDTO.setType(typeDTO);
  }

}
