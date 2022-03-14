package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.ucc.OfferUCC;
import be.vinci.pae.ihm.filters.Authorize;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

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
}
