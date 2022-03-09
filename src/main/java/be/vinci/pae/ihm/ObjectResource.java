package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.ucc.ObjectUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Singleton
@Path("/object")
public class ObjectResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private ObjectUCC objectUCC;

  /**
   * GET an object by his id.
   *
   * @param id : the object id
   * @return a json of the object
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode getObject(@PathParam("id") int id) {
    ObjectDTO objectDTO = objectUCC.getObject(id);

    return jsonMapper.createObjectNode()
        .putPOJO("object", objectDTO);
  }
}
