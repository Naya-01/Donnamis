package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.business.ucc.RatingUCC;
import be.vinci.pae.ihm.filters.Authorize;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
}
