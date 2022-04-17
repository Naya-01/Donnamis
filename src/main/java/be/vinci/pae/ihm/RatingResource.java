package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.business.ucc.RatingUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.ihm.filters.Authorize;
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
    return ratingUCC.getOne(idObject);
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
    if (ratingDTO == null || ratingDTO.getIdObject() == null || ratingDTO.getRating() == null
        || ratingDTO.getComment() == null || ratingDTO.getComment().isBlank()) {
      throw new BadRequestException("Rating need more informations");
    }
    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    ratingDTO.setIdMember(ownerDTO.getMemberId());
    return ratingUCC.addRating(ratingDTO);
  }
}
