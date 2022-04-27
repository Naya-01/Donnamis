package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import jakarta.inject.Inject;
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
  private MemberDAO memberDAO;
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
   * Update the time slot of an offer or an errorcode.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  @Override
  public OfferDTO updateOffer(OfferDTO offerDTO) {
    try {
      dalService.startTransaction();
      // TODO verifier version objet

      OfferDTO offer = offerDAO.updateOne(offerDTO);
      if (offer == null) {
        throw new NotFoundException("Aucune offre");
      }

      if (offerDTO.getObject() != null) {
        ObjectDTO objectFromDB = offerDAO.getOne(offerDTO.getIdOffer()).getObject();
        offerDTO.getObject().setIdObject(objectFromDB.getIdObject());
        offer.setObject(objectDAO.updateOne(offerDTO.getObject()));
      }

      dalService.commitTransaction();
      return offer;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
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
    try {
      dalService.startTransaction();
      List<OfferDTO> offerDTO = offerDAO.getAll(search, idMember, type, objectStatus);
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

      offerDTO = offerDAO.getOne(offerDTO.getIdOffer());

      if (!ownerDTO.getMemberId().equals(offerDTO.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      if (offerDTO.getStatus().equals("given") || offerDTO.getStatus().equals("cancelled")) {
        throw new ForbiddenException("Impossible d'annuler l'offre");
      }

      offerDTO.setStatus("cancelled");
      offerDTO.getObject().setStatus("cancelled");

      OfferDTO updatedOffer = offerDAO.updateOne(offerDTO);
      if (offerDTO.getObject() != null) {
        offerDTO.getObject().setIdObject(offerDTO.getObject().getIdObject());
        updatedOffer.setObject(objectDAO.updateOne(offerDTO.getObject()));
      }

      InterestDTO interestDTO = interestDAO.getAssignedInterest(
          updatedOffer.getObject().getIdObject());
      if (interestDTO != null) {
        //Send notification
        interestDTO.setIsNotificated(true);
        interestDAO.updateNotification(interestDTO);

        interestDTO.setStatus("published");
        interestDAO.updateStatus(interestDTO);
        interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
        interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

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

      offerDTO = offerDAO.getOne(offerDTO.getIdOffer());

      if (!ownerDTO.getMemberId().equals(offerDTO.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      InterestDTO interestDTO = interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject());
      if (interestDTO == null) {
        throw new NotFoundException("aucun membre n'a été assigner");
      }

      offerDTO = offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject());

      if (!offerDTO.getStatus().equals("assigned")) {
        throw new ForbiddenException(
            "aucune offre attribuée n'existe pour que l'objet puisse être non collecté");
      }

      //Send notification
      interestDTO.setIsNotificated(true);
      interestDAO.updateNotification(interestDTO);

      interestDTO.setStatus("not_collected");
      interestDAO.updateStatus(interestDTO);
      interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
      interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

      offerDTO.setStatus("not_collected");
      offerDTO.getObject().setStatus("not_collected");

      offerDTO = offerDAO.updateOne(offerDTO);
      offerDTO.setObject(objectDAO.updateOne(offerDTO.getObject()));

      dalService.commitTransaction();
      return offerDTO;
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

      offerDTO = offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject());

      if (offerDTO == null) {
        throw new NotFoundException("cet object n'existe pas");
      }

      if (!ownerDTO.getMemberId().equals(offerDTO.getObject().getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      InterestDTO interestDTO = interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject());
      if (interestDTO == null) {
        throw new NotFoundException("aucun membre n'a été assigner");
      }

      offerDTO = offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject());
      System.out.println(offerDTO);
      if (!offerDTO.getStatus().equals("assigned")) {
        throw new ForbiddenException(
            "aucune offre attribuée n'existe pour que l'objet puisse être donné");
      }

      //Send notification
      interestDTO.setIsNotificated(true);
      interestDAO.updateNotification(interestDTO);

      interestDTO.setStatus("received");
      interestDAO.updateStatus(interestDTO);

      interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
      interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

      offerDTO.getObject().setStatus("given");
      offerDTO.setStatus("given");

      ObjectDTO objectDTO = objectDAO.updateOne(offerDTO.getObject());

      offerDTO = offerDAO.updateOne(offerDTO);

      offerDTO.setObject(objectDTO);

      dalService.commitTransaction();
      return offerDTO;
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
