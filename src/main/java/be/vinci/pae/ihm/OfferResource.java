package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.ucc.OfferUCC;
import be.vinci.pae.ihm.filters.Authorize;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
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
   * Get all the offers that matche with a search pattern.
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a list of all offerDTO that match with the search pattern
   */
  @GET
  @Path("/all")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public List<OfferDTO> getOffers(
      @DefaultValue("") @QueryParam("search-pattern") String searchPattern) {
    return offerUcc.getAllPosts(searchPattern);
  }

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @GET
  @Path("/lasts")
  @Authorize
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
    if (offerDTO.getTimeSlot() == null || offerDTO.getTimeSlot().isEmpty()
        || offerDTO.getObject() == null) {
      throw new WebApplicationException("Timeslot or object incorrect",
          Response.Status.BAD_REQUEST);
    }
    if (offerDTO.getObject().getIdObject() == 0 && (offerDTO.getObject().getType() == null
        || offerDTO.getObject().getType().getIdType() <= 0
        || offerDTO.getObject().getDescription() == null || offerDTO.getObject().getDescription()
        .isEmpty() || offerDTO.getObject().getStatus() == null || offerDTO.getObject().getStatus()
        .isEmpty() || offerDTO.getObject().getIdOfferor() == 0)) {
      throw new WebApplicationException("Bad json object sent",
          Response.Status.BAD_REQUEST);
    }
    return offerUcc.addOffer(offerDTO);
  }

  /**
   * Update the time slot of an offer.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot
   */
  @POST
  @Authorize
  @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public OfferDTO updateOffer(OfferDTO offerDTO, @Context ContainerRequest request) {
    if (offerDTO.getIdOffer() <= 0 || offerDTO.getTimeSlot() == null || offerDTO.getTimeSlot()
        .isEmpty()) {
      throw new WebApplicationException("Offer need more informations", Status.BAD_REQUEST);
    }

    if (offerDTO.getObject() == null || offerDTO.getObject().getDescription() == null ||
        offerDTO.getObject().getDescription().isEmpty() || offerDTO.getObject().getStatus() == null
        || offerDTO.getObject().getStatus().isEmpty()) {
      throw new WebApplicationException("Object need more informations", Status.BAD_REQUEST);
    }

    if (offerDTO.getObject().getType() == null ||
        ((offerDTO.getObject().getType().getIdType() == 0
            && offerDTO.getObject().getType().getTypeName() == null && offerDTO.getObject()
            .getType().getTypeName().isEmpty())
            ||
            (offerDTO.getObject().getType().getIdType() != 0
                && offerDTO.getObject().getType().getTypeName() != null && offerDTO.getObject()
                .getType()
                .getTypeName().isEmpty()))) {
      throw new WebApplicationException("Type need more informations", Status.BAD_REQUEST);
    }

    return offerUcc.updateOffer(offerDTO);
  }
}
