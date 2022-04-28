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
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
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
        throw new NotFoundException("Interest not found");
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
   * @param item : interestDTO object.
   * @return item.
   */
  @Override
  public InterestDTO addOne(InterestDTO item) {
    InterestDTO interestDTO;
    try {
      dalService.startTransaction();
      if (interestDAO.getOne(item.getIdObject(), item.getIdMember()) != null) {
        //change name exception
        throw new ForbiddenException("An Interest for this Object and Member already exists");
      }
      // if there is no interest
      if (interestDAO.getAllCount(item.getIdObject()) == 0) {
        ObjectDTO objectDTO = objectDAO.getOne(item.getIdObject());
        if (objectDTO == null) {
          throw new NotFoundException("Object not found");
        }
        // TODO verifier version offre & objet
        objectDTO.setStatus("interested");
        objectDAO.updateOne(objectDTO);
        OfferDTO offerDTO = offerDAO.getOneByObject(objectDTO.getIdObject());
        offerDTO.setStatus("interested");
        offerDAO.updateOne(offerDTO);

      }

      interestDTO = interestDAO.addOne(item);

      // Send Notification
      interestDTO.setIsNotificated(true);
      interestDAO.updateNotification(interestDTO);
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
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return objectDTO updated.
   */
  @Override
  public InterestDTO assignOffer(InterestDTO interestDTO, MemberDTO owner) {
    try {
      dalService.startTransaction();
      interestDTO = interestDAO.getOne(interestDTO.getIdObject(),
          interestDTO.getIdMember());

      if (interestDTO == null) {
        throw new NotFoundException("Cet interet n'existe pas");
      }

      interestDTO.setObject(objectDAO.getOne(interestDTO.getIdObject()));
      interestDTO.setMember(memberDAO.getOne(interestDTO.getIdMember()));

      if (!(owner.getMemberId().equals(interestDTO.getObject().getIdOfferor()))) {
        throw new ForbiddenException("Cet objet ne vous appartient pas");
      }

      OfferDTO offerDTO = offerDAO.getLastObjectOffer(interestDTO.getIdObject());

      if ((!offerDTO.getStatus().equals("interested") || !interestDTO.getObject().getStatus()
          .equals("interested")) && (!offerDTO.getStatus().equals("not_collected")
          || !interestDTO.getObject().getStatus()
          .equals("not_collected"))) {
        throw new ForbiddenException("L'offre n'est pas en mesure d'être assigné");
      }

      if (interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()) != null) {
        throw new ForbiddenException("L'offre est déjà assignée à un membre");
      }

      if (!interestDTO.getStatus().equals("published")) {
        throw new ForbiddenException("Le membre n'est pas éligible à l'assignement");
      }

      Integer interestVersionDB = interestDAO.getOne(interestDTO.getObject().getIdObject(),
          interestDTO.getIdMember()).getVersion();
      if (!interestVersionDB.equals(interestDTO.getVersion())) {
        throw new ForbiddenException("Les versions de l'intérêt ne correspondent pas.");
      }

      // TODO verifier version offre
      // update offer and object to assigned
      offerDTO.getObject().setStatus("assigned");
      objectDAO.updateOne(offerDTO.getObject());
      offerDTO.setStatus("assigned");
      offerDAO.updateOne(offerDTO);

      // update interest to assigned
      interestDTO.setStatus("assigned");
      interestDAO.updateStatus(interestDTO);

      // Send Notification
      interestDTO.setIsNotificated(true);
      interestDAO.updateNotification(interestDTO);
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
        throw new NotFoundException("Object not found");
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
   * Check if a member is interested by an object.
   *
   * @param idMember the id of the member
   * @param idObject the id of the object
   * @return true if he's interested false if he's not
   */
  @Override
  public boolean isUserInterested(int idMember, int idObject) {
    InterestDTO userInterested;
    try {
      dalService.startTransaction();
      userInterested = interestDAO.getOne(idObject, idMember);
      dalService.commitTransaction();
      if (userInterested == null) {
        return false;
      }
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return true;
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
        throw new NotFoundException("Object not found");
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
   * Mark a notification shown. /!\ There is no version update because of the non-sensibility of the
   * send_notification field /!\
   *
   * @param member   of the member
   * @param idObject to mark as shown.
   * @return interestDTO updated.
   */
  @Override
  public InterestDTO markNotificationShown(int idObject, MemberDTO member) {
    InterestDTO interestDTO = null;
    try {
      dalService.startTransaction();

      interestDTO = interestDAO.getOne(idObject, member.getMemberId());
      if (interestDTO == null) {
        throw new NotFoundException("La notification n'existe pas");
      }
      if (!interestDTO.getIsNotificated()) {
        throw new ForbiddenException("La notification a déjà été marquée comme lue");
      }

      // Send Notification
      interestDTO.setIsNotificated(false);
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
