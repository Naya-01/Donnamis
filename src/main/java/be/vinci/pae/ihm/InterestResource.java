package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.InterestUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
   * @param idObject : id object of the interest.
   * @param idMember : id of interested member.
   * @return a json of the interest.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public InterestDTO getOne(@DefaultValue("-1") @QueryParam("idObject") int idObject,
      @DefaultValue("-1") @QueryParam("idMember") int idMember) {

    if (idObject < 1 || idMember < 1) {
      throw new WebApplicationException("L'identifiant de l'objet et/ou du membre est/sont "
          + "incorrect(s) et/ou manquant(s)", Response.Status.BAD_REQUEST);
    }

    return interestUCC.getInterest(idObject, idMember);
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
    if (interest.getIdObject() < 1) {
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
    System.out.println("getinterestedcount");
    List<InterestDTO> interestDTOList = interestUCC.getInterestedCount(idObject);
    MemberDTO authenticatedUser = (MemberDTO) request.getProperty("user");
    return jsonMapper.createObjectNode()
        .put("count", interestDTOList.size())
        .put("isUserInterested", interestDTOList.stream()
            .anyMatch(i -> i.getIdMember() == authenticatedUser.getMemberId()));
  }
}
