package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.ucc.OfferUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.ihm.filters.Admin;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.JsonViews;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/offers")
public class OfferResource {

  @Inject
  private OfferUCC offerUcc;


  /**
   * Get last offer of an object.
   *
   * @param idObject to search.
   * @return last offer.
   */
  @GET
  @Path("/last/{idObject}")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public OfferDTO getLastOffer(@PathParam("idObject") Integer idObject) {
    Logger.getLogger("Log").log(Level.INFO, "OfferResource getLastOffer");
    OfferDTO offerDTO = offerUcc.getLastOffer(idObject);
    offerDTO = JsonViews.filterPublicJsonView(offerDTO, OfferDTO.class);
    return offerDTO;
  }

  /**
   * Get all offers.
   *
   * @param searchPattern the search pattern (empty -> all) according to their type, description
   * @param offeror       if you want your offers
   * @param request       information of the member
   * @return list of offers
   */
  @GET
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public List<OfferDTO> getOffers(
      @DefaultValue("") @QueryParam("search-pattern") String searchPattern,
      @DefaultValue("") @QueryParam("self") String offeror,
      @DefaultValue("") @QueryParam("type") String type,
      @DefaultValue("") @QueryParam("status") String objectStatus,
      @DefaultValue("") @QueryParam("date") String dateText,
      @Context ContainerRequest request
  ) {
    Logger.getLogger("Log").log(Level.INFO, "OfferResource getOffers");
    int idOfferor = 0;
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    if (offeror.equals("true")) {
      idOfferor = memberDTO.getMemberId();
    } else if (memberDTO.getRole().equals("administrator")) {
      try {
        idOfferor = Integer.parseInt(offeror);
      } catch (Exception ignored) { /* ignore this exception */ }
    }
    List<OfferDTO> offerDTOList =
        offerUcc.getOffers(searchPattern, idOfferor, type, objectStatus, dateText);
    return JsonViews.filterPublicJsonViewAsList(offerDTOList, OfferDTO.class);
  }

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @GET
  @Path("/lasts")
  @Produces(MediaType.APPLICATION_JSON)
  public List<OfferDTO> getLastOffers() {
    Logger.getLogger("Log").log(Level.INFO, "OfferResource getLastOffers");
    List<OfferDTO> offerDTOList = offerUcc.getLastOffers();
    return JsonViews.filterPublicJsonViewAsList(offerDTOList, OfferDTO.class);
  }

  /**
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer
   */
  @GET
  @Path("/getById/{idOffer}")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public OfferDTO getOfferById(@PathParam("idOffer") int idOffer) {
    Logger.getLogger("Log").log(Level.INFO, "OfferResource getOfferById");
    OfferDTO offerDTO = offerUcc.getOfferById(idOffer);
    return JsonViews.filterPublicJsonView(offerDTO, OfferDTO.class);
  }

  /**
   * Add an offer in the db with out without an object.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  @POST
  @Path("/newOffer")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public OfferDTO addOffer(@Context ContainerRequest request, OfferDTO offerDTO) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource addOffer");
    if (offerDTO.getObject().getIdObject() == null || offerDTO.getTimeSlot() == null
        || offerDTO.getTimeSlot().isBlank()) {
      throw new BadRequestException("Informations manquantes !");
    }

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    OfferDTO offer = offerUcc.addOffer(offerDTO, ownerDTO);

    return JsonViews.filterPublicJsonView(offer, OfferDTO.class);
  }

  /**
   * Update the time slot of an offer.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  @PUT
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public OfferDTO updateOffer(OfferDTO offerDTO, @Context ContainerRequest request) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource updateOffer");
    MemberDTO memberRequest = (MemberDTO) request.getProperty("user");

    if (offerDTO.getIdOffer() == null) {
      throw new BadRequestException("L'offre n'a pas d'identifiant");
    }

    OfferDTO initialOfferDTO = offerUcc.getOfferById(offerDTO.getIdOffer());

    if (!Objects.equals(initialOfferDTO.getObject().getIdOfferor(), memberRequest.getMemberId())) {
      throw new UnauthorizedException("Vous n'êtes pas l'offreur de l'objet");
    }
    OfferDTO offer = offerUcc.updateOffer(offerDTO);
    return JsonViews.filterPublicJsonView(offer, OfferDTO.class);
  }

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @return a list of offerDTO
   */
  @GET
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/givenOffers/{id}")
  public List<OfferDTO> getGivenOffers(@PathParam("id") int idReceiver,
      @Context ContainerRequest request) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource getGivenOffers");
    MemberDTO memberRequest = (MemberDTO) request.getProperty("user");
    if (!memberRequest.getRole().equals("administrator")
        && memberRequest.getMemberId() != idReceiver) {
      throw new UnauthorizedException("Vous ne pouvez pas voir ces offres");
    }
    List<OfferDTO> offerDTOList = offerUcc.getGivenOffers(idReceiver);
    return JsonViews.filterPublicJsonViewAsList(offerDTOList, OfferDTO.class);
  }

  /**
   * Get all offers received by a member.
   *
   * @param request data of the member connected
   * @param search  the search pattern (empty -> all) according to their type, description
   * @return a list of offerDTO
   */
  @GET
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/givenAndAssignedOffers/")
  public List<OfferDTO> getGivenAndAssigned(@Context ContainerRequest request,
      @QueryParam("search") String search) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource getGivenOffers");
    MemberDTO memberRequest = (MemberDTO) request.getProperty("user");
    List<OfferDTO> offerDTOList = offerUcc.getGivenAndAssignedOffers(memberRequest, search);
    return JsonViews.filterPublicJsonViewAsList(offerDTOList, OfferDTO.class);
  }

  /**
   * Cancel an Offer, set the status to 'cancelled'.
   *
   * @param offerDTO offer object with his id
   * @return an object
   */
  @POST
  @Path("/cancelOffer")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public OfferDTO cancelOffer(@Context ContainerRequest request, OfferDTO offerDTO) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource cancelOffer");
    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");

    if (offerDTO.getIdOffer() == null) {
      throw new BadRequestException(
          "Veuillez indiquer un identifiant dans la ressource de l'offre");
    }

    OfferDTO offer = offerUcc.cancelOffer(offerDTO, ownerDTO);
    return JsonViews.filterPublicJsonView(offer, OfferDTO.class);
  }


  /**
   * Mark an offer to 'not collected'.
   *
   * @param offerDTO object with his id
   * @return an object
   */
  @POST
  @Path("/notCollected")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public OfferDTO notCollectedOffer(@Context ContainerRequest request, OfferDTO offerDTO) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource notCollectedOffer");
    if (offerDTO.getIdOffer() == null) {
      throw new BadRequestException(
          "Veuillez indiquer un identifiant dans la ressource de l'offre");
    }

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    OfferDTO offer = offerUcc.notCollectedOffer(offerDTO, ownerDTO);
    return JsonViews.filterPublicJsonView(offer, OfferDTO.class);

  }


  /**
   * Give an Object.
   *
   * @param offerDTO contain object id
   * @return an object
   */
  @POST
  @Path("/give")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public OfferDTO giveOffer(@Context ContainerRequest request, OfferDTO offerDTO) {

    Logger.getLogger("Log").log(Level.INFO, "OfferResource giveOffer");
    if (offerDTO.getObject().getIdObject() == null) {
      throw new BadRequestException("L'identifiant de l'objet null");
    }

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    OfferDTO offer = offerUcc.giveOffer(offerDTO, ownerDTO);
    return JsonViews.filterPublicJsonView(offer, OfferDTO.class);
  }

  /**
   * Get a map of data about a member (nb of received object, nb of not collected objects, nb of
   * given objects and nb of total offers).
   *
   * @param idReceiver the id of the member
   * @return a map with all th data's.
   */
  @GET
  @Path("/countOffers/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Admin
  public Map<String, Integer> getOffersCount(@PathParam("id") int idReceiver) {
    Logger.getLogger("Log").log(Level.INFO, "OfferResource getOffersCount");
    return offerUcc.getOffersCount(idReceiver);
  }

  /**
   * Make an Object with his offer.
   *
   * @param offerDTO object that contain objectDTO & offerDTO information
   * @return offer
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public OfferDTO addFirstOffer(@Context ContainerRequest request, OfferDTO offerDTO) {
    Logger.getLogger("Log").log(Level.INFO, "ObjectResource addObject");
    if (offerDTO.getObject().getType() == null
        || offerDTO.getObject().getType().getIdType() == null
        && offerDTO.getObject().getType().getTypeName() == null && offerDTO.getObject()
        .getType().getTypeName().isEmpty()
        || offerDTO.getObject().getType().getIdType() != null
        && offerDTO.getObject().getType().getTypeName() != null && offerDTO.getObject().getType()
        .getTypeName().isEmpty()) {
      throw new BadRequestException("Veuillez spécifier un type");
    }
    if (offerDTO.getObject().getType() == null
        || offerDTO.getObject().getDescription() == null || offerDTO.getObject().getDescription()
        .isEmpty()) {
      throw new BadRequestException("L'objet de l'offre est incomplet");
    }

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    offerDTO.getObject().setIdOfferor(ownerDTO.getMemberId());
    OfferDTO offer = offerUcc.addObject(offerDTO);
    return JsonViews.filterPublicJsonView(offer, OfferDTO.class);
  }

}
