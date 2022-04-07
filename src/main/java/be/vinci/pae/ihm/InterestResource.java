package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.InterestUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/interest")
public class InterestResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private InterestUCC interestUCC;

  /**
   * Get an interest, by the id of the interested member and the id of the object.
   *
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return a json of the interest.
   */
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO getOne(InterestDTO interestDTO) {

    if (interestDTO == null && interestDTO.getIdMember() == null && interestDTO.getObject() == null
        && interestDTO.getObject().getIdObject() == null) {
      throw new WebApplicationException("L'identifiant de l'objet et/ou du membre est/sont "
          + "incorrect(s) et/ou manquant(s)", Response.Status.BAD_REQUEST);
    }

    return interestUCC.getInterest(interestDTO);
  }

  /**
   * Add one interest.
   *
   * @param interest : interestDTO object without the Member.
   * @param request  : request received from Authorize annotation (filter).
   * @return a json of the interest with the Member who is interested in.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO addOne(InterestDTO interest, @Context ContainerRequest request) {
    if (interest == null || interest.getAvailabilityDate() == null) {
      throw new WebApplicationException("Lacks of mandatory info", Response.Status.BAD_REQUEST);
    }
    if (interest.getObject().getIdObject() < 1) {
      throw new WebApplicationException("Non existent id object", Response.Status.BAD_REQUEST);
    }
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    interest.setIdMember(authenticatedUser.getMemberId());
    interest.setStatus("published");
    return interestUCC.addOne(interest);
  }

  /**
   * Get the count of interested people of an object.
   *
   * @param idObject the object we want to retrieve the interest count
   * @param request  request received from Authorize annotation (filter).
   * @return jsonNode with count of interests and a boolean if the user is one of the interested
   */
  @GET
  @Path("/count/{idObject}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public JsonNode getInterestedCount(@PathParam("idObject") int idObject,
      @Context ContainerRequest request) {
    List<InterestDTO> interestDTOList = interestUCC.getInterestedCount(idObject);
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    return jsonMapper.createObjectNode()
        .put("count", interestDTOList.size())
        .put("isUserInterested", interestDTOList.stream()
            .anyMatch(i -> i.getIdMember() == authenticatedUser.getMemberId()));
  }

  /**
   * Assign an object to a member interested.
   *
   * @param request     data of the object's owner.
   * @param interestDTO : the interest informations (id of the object and id of the member).
   * @return object updated.
   */
  @POST
  @Path("/assignObject")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO assignObject(@Context ContainerRequest request, InterestDTO interestDTO) {

    MemberDTO ownerDTO = (MemberDTO) request.getProperty("user");
    if (interestDTO == null && interestDTO.getObject() == null
        && interestDTO.getObject().getIdObject() == null) {
      throw new BadRequestException("Veuillez indiquer un id dans l'objet de la ressource interet");
    }
    interestDTO = interestUCC.getInterest(interestDTO);
    if (!ownerDTO.getMemberId().equals(interestDTO.getObject().getIdOfferor())) {
      throw new UnauthorizedException("Cet objet ne vous appartient pas");
    }

    return interestUCC.assignObject(interestDTO);
  }
}
