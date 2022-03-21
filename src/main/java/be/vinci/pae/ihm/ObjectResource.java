package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.ucc.ObjectUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

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

  /**
   * Find all object of a member.
   *
   * @param idMember : id member that we want to get all his object.
   * @return object list of this member.
   */
  @GET
  @Path("/member/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode getAllObjectMember(@PathParam("id") int idMember) {
    List<ObjectDTO> objectDTOList = objectUCC.getAllObjectMember(idMember);

    return jsonMapper.createObjectNode()
        .putPOJO("objectList", objectDTOList);
  }

  /**
   * Update an object with new information in the ObjectDTO.
   *
   * @param objectDTO : object that we want to update.
   * @return : object update
   */
  @PUT
  @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    return objectUCC.updateOne(objectDTO);
  }
}
