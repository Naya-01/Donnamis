package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.ucc.OfferUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.ihm.filters.Authorize;
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
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/offers")
public class OfferResource {

  @Inject
  private OfferUCC offerUcc;

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
      @Context ContainerRequest request
  ) {
    int idOfferor = 0;
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    if (offeror.equals("true")) {
      idOfferor = memberDTO.getMemberId();
    } else if (memberDTO.getRole().equals("administrator")) {
      try {
        idOfferor = Integer.parseInt(offeror);
      } catch (Exception ignored) { /* ignore this exception */ }
    }
    return offerUcc.getOffers(searchPattern, idOfferor, type, objectStatus);
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
    return offerUcc.getLastOffers();
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
    return offerUcc.getOfferById(idOffer);
  }

  /**
   * Add an offer in the db with out without an object.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  @POST
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public OfferDTO addOffer(OfferDTO offerDTO) {
    if (offerDTO.getObject().getType() == null
        || offerDTO.getObject().getType().getIdType() == null
        && offerDTO.getObject().getType().getTypeName() == null && offerDTO.getObject()
        .getType().getTypeName().isEmpty()
        || offerDTO.getObject().getType().getIdType() != null
        && offerDTO.getObject().getType().getTypeName() != null && offerDTO.getObject()
        .getType().getTypeName().isEmpty()) {
      throw new WebApplicationException("Type need more informations", Status.BAD_REQUEST);
    }
    if (offerDTO.getObject().getIdObject() == null && (offerDTO.getObject().getType() == null
        || offerDTO.getObject().getDescription() == null || offerDTO.getObject().getDescription()
        .isEmpty() || offerDTO.getObject().getStatus() == null || offerDTO.getObject().getStatus()
        .isEmpty() || offerDTO.getObject().getIdOfferor() == null)) {
      throw new WebApplicationException("Bad json object sent", Response.Status.BAD_REQUEST);
    }
    return offerUcc.addOffer(offerDTO);
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
    MemberDTO memberRequest = (MemberDTO) request.getProperty("user");

    if (offerDTO.getIdOffer() == 0) {
      throw new BadRequestException("Aucun id de l'offre");
    }

    OfferDTO initialOfferDTO = offerUcc.getOfferById(offerDTO.getIdOffer());

    if (initialOfferDTO.getObject().getIdOfferor() != memberRequest.getMemberId()) {
      throw new UnauthorizedException("Vous n'avez pas créé cet offre.");
    }
    return offerUcc.updateOffer(offerDTO);
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
    MemberDTO memberRequest = (MemberDTO) request.getProperty("user");
    if (!memberRequest.getRole().equals("administrator")
        && memberRequest.getMemberId() != idReceiver) {
      throw new UnauthorizedException("Vous ne pouvez pas voir ces offres");
    }
    return offerUcc.getGivenOffers(idReceiver);
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

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");

    if (offerDTO.getIdOffer() == null) {
      throw new BadRequestException("Veuillez indiquer un id dans la ressource offer");
    }

    offerDTO = offerUcc.getOfferById(offerDTO.getIdOffer());

    if (!ownerDTO.getMemberId().equals(offerDTO.getObject().getIdOfferor())) {
      throw new ForbiddenException("Cet objet ne vous appartient pas");
    }

    return offerUcc.cancelObject(offerDTO);
  }
}
