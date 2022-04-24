package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.ucc.InterestUCC;
import be.vinci.pae.business.ucc.ObjectUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.JsonViews;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/interest")
public class InterestResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private InterestUCC interestUCC;
  @Inject
  private ObjectUCC objectUCC;

  /**
   * Get notifications count.
   *
   * @param request data of the member.
   * @return notification count
   */
  @GET
  @Path("/notificationCount")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public Integer getNotificationCount(@Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource getOne");
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    return interestUCC.getNotificationCount(authenticatedUser.getMemberId());
  }

  /**
   * Get an interest, by the id of the interested member and the id of the object.
   *
   * @param idObject : id object of the interest.
   * @return a json of the interest.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO getOwnInterest(@DefaultValue("-1") @QueryParam("idObject") int idObject,
      @Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource getOne");
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    if (idObject < 1) {
      throw new WebApplicationException("L'identifiant de l'objet et/ou du membre est/sont "
          + "incorrect(s) et/ou manquant(s)", Response.Status.BAD_REQUEST);
    }

    InterestDTO interestDTO = interestUCC.getInterest(idObject, authenticatedUser.getMemberId());
    interestDTO.setMember(JsonViews.filterPublicJsonView(interestDTO.getMember(), MemberDTO.class));
    return interestDTO;
  }


  /**
   * Add one interest.
   *
   * @param interest : interestDTO object without the Member.
   * @param request  : request received from Authorize annotation (filter).
   * @return a json of the interest with the Member who is interested in.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO addOne(InterestDTO interest, @Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource addOne");
    if (interest == null || interest.getAvailabilityDate() == null) {
      throw new WebApplicationException("Lacks of mandatory info", Response.Status.BAD_REQUEST);
    }
    if (interest.getObject().getIdObject() < 1) {
      throw new WebApplicationException("Non existent id object", Response.Status.BAD_REQUEST);
    }
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    interest.setIdMember(authenticatedUser.getMemberId());
    interest.setStatus("published");

    InterestDTO interestDTO = interestUCC.addOne(interest);
    interestDTO.setMember(JsonViews.filterPublicJsonView(interestDTO.getMember(), MemberDTO.class));
    return interestDTO;
  }

  /**
   * Get the count of interested people of an object.
   *
   * @param idObject the object we want to retrieve the interest count
   * @param request  request received from Authorize annotation (filter).
   * @return jsonNode with count of interests and a boolean if the user is one of the interested
   */
  @GET
  @Path("/count/{idObject}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public JsonNode getInterestedCount(@PathParam("idObject") int idObject,
      @Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource getInterestedCount");
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    return jsonMapper.createObjectNode()
        .put("count", interestUCC.getInterestedCount(idObject))
        .put("isUserInterested",
            interestUCC.isUserInterested(authenticatedUser.getMemberId(), idObject));
  }

  /**
   * Get all the interests of an object.
   *
   * @param idObject of the object.
   * @param request  information of the owner.
   * @return interestDTO List
   */
  @GET
  @Path("/getAllInterests/{idObject}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<InterestDTO> getAllInterests(@PathParam("idObject") int idObject,
      @Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource getAllInterests");
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    ObjectDTO objectDTO = objectUCC.getObject(idObject);
    if (authenticatedUser.getMemberId() != objectDTO.getIdOfferor()) {
      throw new UnauthorizedException("Cet objet ne vous appartient pas");
    }

    List<InterestDTO> interestDTOList = interestUCC.getAllInterests(idObject);
    for (InterestDTO interestDTO : interestDTOList) {
      interestDTO.setMember(
          JsonViews.filterPublicJsonView(interestDTO.getMember(), MemberDTO.class));
    }
    return interestDTOList;
  }

  /**
   * Get all the notification of a member.
   *
   * @param request information of the member.
   * @return interestDTO List filtered with notifications
   */
  @GET
  @Path("/getAllNotifications")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<InterestDTO> getAllNotifications(@Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource getAllNotifications");
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    List<InterestDTO> interestDTOList = interestUCC.getNotifications(
        authenticatedUser.getMemberId());
    for (InterestDTO interestDTO : interestDTOList) {
      interestDTO.setMember(
          JsonViews.filterPublicJsonView(interestDTO.getMember(), MemberDTO.class));
    }
    return interestDTOList;
  }

  /**
   * Assign an object to a member interested.
   *
   * @param request     data of the object's owner.
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return object updated.
   */
  @POST
  @Path("/assignOffer")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO assignOffer(@Context ContainerRequest request, InterestDTO interestDTO) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource assignOffer");

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    if (interestDTO.getIdMember() == null
        && interestDTO.getObject().getIdObject() == null) {
      throw new BadRequestException("Veuillez indiquer un id dans l'objet de la ressource interet");
    }
    interestDTO = interestUCC
        .getInterest(interestDTO.getObject().getIdObject(), interestDTO.getIdMember());
    if (!ownerDTO.getMemberId().equals(interestDTO.getObject().getIdOfferor())) {
      throw new UnauthorizedException("Cet objet ne vous appartient pas");
    }

    return interestUCC.assignOffer(interestDTO);
  }

  /**
   * Mark a notification as shown.
   *
   * @param request  data of the member.
   * @param idObject of the interest.
   * @return interestDTO updated.
   */
  @PUT
  @Path("/notificationShown/{idObject}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO markNotificationShown(@Context ContainerRequest request,
      @PathParam("idObject") int idObject) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource markNotifcationShown");

    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    InterestDTO interestDTO = interestUCC.getInterest(idObject, memberDTO.getMemberId());
    return interestUCC.markNotificationShown(interestDTO);
  }

  /**
   * Mark all notifications as shown.
   *
   * @param request data of the member.
   * @return interestDTO updated.
   */
  @PUT
  @Path("/allNotificationShown")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public List<InterestDTO> markAllNotificationsShown(@Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "InterestResource markNotifcationShown");

    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    return interestUCC.markAllNotificationsShown(memberDTO.getMemberId());
  }


}
