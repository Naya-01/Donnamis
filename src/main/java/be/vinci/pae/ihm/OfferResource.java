package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.ucc.OfferUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Singleton
@Path("/offers")
public class OfferResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private OfferUCC offerUcc;


  /**
   * Get all the offers that matche with a search pattern
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a json object of all offerDTO that match with the search pattern
   */
  @GET
  @Path("/all")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(@DefaultValue("") @QueryParam("search-pattern") String searchPattern) {
    List<OfferDTO> offerDTOList = offerUcc.getAllPosts(searchPattern);
    if (offerDTOList.isEmpty()) {
      throw new WebApplicationException("Aucune offre", Response.Status.NOT_FOUND);
    }
    ObjectNode objectNode = jsonMapper.createObjectNode();
    for (OfferDTO offerDTO : offerDTOList) {
      objectNode.putPOJO(String.valueOf(offerDTO.getIdOffer()), offerDTO);
    }
    return objectNode;
  }
}
