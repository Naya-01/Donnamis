package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.ucc.TypeUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Singleton
@Path("/types")
public class TypeResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private TypeUCC typeUCC;


  /**
   * Get all default types available.
   *
   * @return a list of default types
   */
  @GET
  @Path("/allDefault")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode getDefaultTypes() {
    List<TypeDTO> offerDTOList = typeUCC.getAllDefaultTypes();
    ObjectNode objectNode = jsonMapper.createObjectNode();
    for (TypeDTO typeDTO : offerDTOList) {
      objectNode.putPOJO(String.valueOf(typeDTO.getIdType()), typeDTO);
    }
    return objectNode;
  }

}
