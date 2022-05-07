package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ConflictException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;

public class InterestUCCImpl implements InterestUCC {

  private static final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private InterestDAO interestDAO;
  @Inject
  private OfferDAO offerDAO;
  @Inject
  private DALService dalService;
  @Inject
  private ObjectDAO objectDAO;
  @Inject
  private MemberDAO memberDAO;

  /**
   * Find an interest, by the id of the interested member and the id of the object.
   *
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return interestDTO having the idObject and idMember.
   */
  @Override
  public InterestDTO getInterest(int idObject, int idMember) {
    try {
      dalService.startTransaction();
      InterestDTO interestDTO = interestDAO.getOne(idObject, idMember);
      if (interestDTO == null) {
        throw new NotFoundException("Intérêt non trouvé");
      }
      interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
      interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

      dalService.commitTransaction();
      return interestDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Add one interest.
   *
   * @param interest          : interestDTO object.
   * @param authenticatedUser : member that wants to add an interest.
   * @return interest added.
   */
  @Override
  public InterestDTO addOne(InterestDTO interest, MemberDTO authenticatedUser) {
    InterestDTO interestDTO;
    try {
      dalService.startTransaction();
      interest.setIdMember(authenticatedUser.getMemberId());
      interest.setStatus("published");
      if (interestDAO.getOne(interest.getIdObject(), interest.getIdMember()) != null) {
        //change name exception
        throw new ConflictException("Un intérêt pour cet objet et ce membre existe déjà !");
      }
      ObjectDTO objectDTO = objectDAO.getOne(interest.getIdObject());
      if (objectDTO == null) {
        throw new NotFoundException("Objet non trouvé !");
      }
      if (!objectDTO.getStatus().equals("interested") && !objectDTO.getStatus()
          .equals("available")) {
        throw new ForbiddenException("L'objet doit être disponible pour marquer son intérêt.");
      }
      OfferDTO offerDTO = offerDAO.getLastObjectOffer(objectDTO.getIdObject());
      if (offerDTO == null) {
        throw new NotFoundException("Offre non trouvée !");
      }
      if (!offerDTO.getStatus().equals("interested") && !offerDTO.getStatus().equals("available")) {
        throw new ForbiddenException("L'objet doit être disponible pour marquer son intérêt.");
      }

      // if there is no interest
      if (interestDAO.getAllCount(interest.getIdObject()) == 0) {
        if (!objectDTO.getVersion().equals(interest.getObject().getVersion())) {
          throw new ForbiddenException("Les versions ne correspondent pas");
        }
        if (!offerDTO.getVersion().equals(interest.getOffer().getVersion())) {
          throw new ForbiddenException("Les versions ne correspondent pas");
        }
        objectDTO.setStatus("interested");
        objectDAO.updateOne(objectDTO);
        offerDTO.setStatus("interested");
        offerDAO.updateOne(offerDTO);
      }
      interest.setIsNotificated(true);
      interest.setNotificationDate(LocalDate.now());
      interestDTO = interestDAO.addOne(interest);
      interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
      interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return interestDTO;
  }

  /**
   * Assign the offer to a member.
   *
   * @param owner       the object's owner
   * @param interestDTO : the interest information (id of the object and id of the member).
   * @return objectDTO updated.
   */
  @Override
  public InterestDTO assignOffer(InterestDTO interestDTO, MemberDTO owner) {
    try {
      dalService.startTransaction();
      InterestDTO interestDTOFromDB =
          interestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember());

      if (interestDTOFromDB == null) {
        throw new NotFoundException("Cet interet n'existe pas");
      }

      interestDTOFromDB.setObject(objectDAO.getOne(interestDTOFromDB.getIdObject()));
      interestDTOFromDB.setMember(memberDAO.getOne(interestDTOFromDB.getIdMember()));

      if (!(owner.getMemberId().equals(interestDTOFromDB.getObject().getIdOfferor()))) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      OfferDTO offerDTO = offerDAO.getLastObjectOffer(interestDTOFromDB.getIdObject());

      if (!offerDTO.getVersion().equals(interestDTO.getOffer().getVersion())) {
        throw new ForbiddenException("Les versions ne correspondent pas");
      }

      if (!offerDTO.getObject().getVersion().equals(interestDTO.getObject().getVersion())) {
        throw new ForbiddenException("Les versions ne correspondent pas");
      }

      if (interestDTOFromDB.getMember().getStatus().equals("prevented")
          || !offerDTO.getStatus().equals("interested")
          && !offerDTO.getStatus().equals("not_collected")) {
        throw new ForbiddenException("L'offre n'est pas en mesure d'être assigné");
      }

      if (interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()) != null) {
        throw new ForbiddenException("L'offre est déjà assignée à un membre");
      }

      if (!interestDTOFromDB.getStatus().equals("published")) {
        throw new ForbiddenException("Le membre n'est pas éligible à l'assignement");
      }

      if (!interestDTO.getVersion().equals(interestDTOFromDB.getVersion())) {
        throw new ForbiddenException("Vous ne possédez pas une version à jour de l'intérêt.");
      }

      // update offer and object to assigned
      offerDTO.getObject().setStatus("assigned");
      objectDAO.updateOne(offerDTO.getObject());
      offerDTO.setStatus("assigned");
      offerDAO.updateOne(offerDTO);

      // update interest to assigned
      interestDTOFromDB.setStatus("assigned");
      interestDAO.updateStatus(interestDTOFromDB);

      // Send Notification
      interestDTOFromDB.setIsNotificated(true);
      interestDTOFromDB.setNotificationDate(LocalDate.now());
      interestDAO.updateNotification(interestDTOFromDB);
      interestDTOFromDB.setObject(objectDAO.getOne(interestDTOFromDB.getIdObject()));
      interestDTOFromDB.setMember(memberDAO.getOne(interestDTOFromDB.getIdMember()));

      dalService.commitTransaction();
      return interestDTOFromDB;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get notification count.
   *
   * @param member of the member.
   * @return count of notification
   */
  @Override
  public Integer getNotificationCount(MemberDTO member) {
    Integer interests;
    try {
      dalService.startTransaction();
      interests = interestDAO.getNotificationCount(member.getMemberId());
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return interests;
  }

  /**
   * Get a list of interest, by an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @param offeror  the owner of the object
   * @return a list of interest, by an id object
   */
  @Override
  public List<InterestDTO> getAllInterests(int idObject, MemberDTO offeror) {
    List<InterestDTO> interestDTOList;
    try {
      dalService.startTransaction();
      ObjectDTO objectDTO = objectDAO.getOne(idObject);
      if (objectDTO == null) {
        throw new NotFoundException("Objet non trouvé !");
      }

      if (!offeror.getMemberId().equals(objectDTO.getIdOfferor())) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      interestDTOList = interestDAO.getAllPublished(idObject);

      if (interestDTOList == null) {
        throw new NotFoundException("Aucun intérêt trouvé");
      }

      for (InterestDTO interestDTO : interestDTOList) {
        interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
        interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));
      }

      dalService.commitTransaction();
      return interestDTOList;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Get the count of interested people of an object.
   *
   * @param idObject  the object we want to retrieve the interest count.
   * @param memberDTO to check if he is in the interested people.
   * @return jsonNode with count of interests and a boolean if the user is one of the interested
   */
  @Override
  public JsonNode getInterestedCount(Integer idObject, MemberDTO memberDTO) {
    int count;
    Boolean userInterested;
    try {
      dalService.startTransaction();
      ObjectDTO objectDTO = objectDAO.getOne(idObject);
      if (objectDTO == null) {
        throw new NotFoundException("Objet non trouvé !");
      }
      count = interestDAO.getAllPublishedCount(idObject);

      InterestDTO interestDTO = interestDAO.getOne(idObject, memberDTO.getMemberId());
      userInterested = interestDTO != null;

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return jsonMapper.createObjectNode()
        .put("count", count)
        .put("isUserInterested", userInterested);
  }


  /**
   * Get a list of notificated interest in an id object.
   *
   * @param member the member we want to retrieve notifications
   * @return a list of interest, by an id member
   */
  @Override
  public List<InterestDTO> getNotifications(MemberDTO member) {
    List<InterestDTO> interestDTOList;
    try {
      dalService.startTransaction();
      interestDTOList = interestDAO.getAllNotifications(member.getMemberId());
      if (interestDTOList == null) {
        throw new NotFoundException("Aucunes notifications n'est disponible");
      }
      for (InterestDTO interestDTO : interestDTOList) {
        interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
        interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));
      }
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return interestDTOList;
  }

  /**
   * Mark a notification shown.
   *
   * @param idMember of the member's interest
   * @param member   owner of the object or the one to update notification.
   * @param idObject to mark as shown.
   * @return interestDTO updated.
   */
  @Override
  public InterestDTO markNotificationShown(Integer idObject, MemberDTO member, Integer idMember) {
    InterestDTO interestDTO;
    try {
      dalService.startTransaction();

      ObjectDTO objectDTO = objectDAO.getOne(idObject);
      if (objectDTO == null) {
        throw new NotFoundException("L'objet n'existe pas");
      }

      interestDTO = interestDAO.getOne(idObject, idMember);

      if (interestDTO == null) {
        throw new NotFoundException("La notification n'existe pas");
      }

      //It's not the same user interest as the requester.
      if (!idMember.equals(member.getMemberId()) && !member.getMemberId()
          .equals(objectDTO.getIdOfferor())) {
        throw new ForbiddenException(
            "Cette objet ne vous appartient pas, vous ne pouvez pas modifier la notification.");
      }

      if (!interestDTO.getIsNotificated()) {
        throw new ForbiddenException("La notification a déjà été marquée comme lue");
      }

      // Send Notification
      interestDTO.setIsNotificated(false);
      interestDTO.setNotificationDate(LocalDate.now());
      interestDAO.updateNotification(interestDTO);
      interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
      interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

      dalService.commitTransaction();
      return interestDTO;
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
  }

  /**
   * Mark all notifications shown. /!\ There is no version update because of the non-sensibility of
   * the send_notification field /!\
   *
   * @param member to mark all his notifications showns.
   * @return interestDTOs updated.
   */
  @Override
  public List<InterestDTO> markAllNotificationsShown(MemberDTO member) {
    List<InterestDTO> interestDTOList;
    try {
      dalService.startTransaction();
      interestDTOList = interestDAO.markAllNotificationsShown(member.getMemberId());
      if (interestDTOList == null) {
        throw new NotFoundException("Aucunes notifications n'a été trouvé");
      }
      for (InterestDTO interestDTO : interestDTOList) {
        interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
        interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));
      }

      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }

    return interestDTOList;
  }
}
