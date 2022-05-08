package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.business.ucc.RatingUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.JsonViews;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/ratings")
public class RatingResource {

  @Inject
  private RatingUCC ratingUCC;

  /**
   * GET all default types.
   *
   * @param idObject the id of the object
   * @return a rating
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public RatingDTO getOne(@PathParam("id") int idObject) {
    Logger.getLogger("Log").log(Level.INFO, "RatingResource getOne");
    RatingDTO ratingDTO = ratingUCC.getOne(idObject);
    ratingDTO.setMemberRater(JsonViews
        .filterPublicJsonView(ratingDTO.getMemberRater(), MemberDTO.class));
    ratingDTO = JsonViews.filterPublicJsonView(ratingDTO, RatingDTO.class);
    return ratingDTO;
  }

  /**
   * Add a Rating for an object.
   *
   * @param ratingDTO object that contain rating informations
   * @return rating added
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public RatingDTO addRating(@Context ContainerRequest request, RatingDTO ratingDTO) {
    Logger.getLogger("Log").log(Level.INFO, "RatingResource addRating");

    if (ratingDTO == null || ratingDTO.getIdObject() == null || ratingDTO.getRating() == null
        || ratingDTO.getComment() == null || ratingDTO.getComment().isBlank()) {
      throw new BadRequestException("La note a besoin de plus d'informations");
    }
    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    ratingDTO.setIdMember(ownerDTO.getMemberId());
    RatingDTO rating = ratingUCC.addRating(ratingDTO);

    rating.setMemberRater(JsonViews
        .filterPublicJsonView(rating.getMemberRater(), MemberDTO.class));
    rating = JsonViews.filterPublicJsonView(rating, RatingDTO.class);
    return rating;
  }
}
