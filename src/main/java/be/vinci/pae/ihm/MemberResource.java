package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.ihm.filters.Admin;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.JsonViews;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/member")
public class MemberResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private MemberUCC memberUCC;


  @POST
  @Path("/upload")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response uploadFile(@FormDataParam("file") InputStream file, @FormDataParam("file")
      FormDataContentDisposition fileDisposition) {

    try {
      String fileName = System.getenv("OneDrive") + "\\img\\"
          + UUID.randomUUID() + fileDisposition.getFileName();
      Files.copy(file, Paths.get(fileName));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Response.ok().build();
  }

  /**
   * Get a user by his token.
   *
   * @param request to get information request
   * @return return the linked user to his token
   */
  @POST
  @Path("/getMemberByToken")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode getMemberByToken(@Context ContainerRequest request) {
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    return jsonMapper.createObjectNode()
        .putPOJO("user", JsonViews.filterPublicJsonView(memberDTO, MemberDTO.class));
  }

  /**
   * Promote a member to admin status with his id.
   *
   * @param json to get id of the member to promote
   */
  @POST
  @Path("/promoteAdministrator")
  @Authorize
  @Admin
  public void promoteAdministrator(JsonNode json) {
    if (!json.hasNonNull("id")) {
      throw new WebApplicationException("id du membre introuvable",
          Response.Status.BAD_REQUEST);
    }
    int id = json.get("id").asInt();
    memberUCC.promoteAdministrator(id);
    throw new WebApplicationException("Le membre est désormais administrateur", Response.Status.OK);
  }

  /**
   * Confirm the registration of the member.
   *
   * @param json to get id of the member to promote
   */
  @POST
  @Path("/confirmRegistration")
  @Authorize
  @Admin
  public void confirmRegistration(JsonNode json) {
    if (!json.hasNonNull("id")) {
      throw new WebApplicationException("id du membre introuvable",
          Response.Status.BAD_REQUEST);
    }
    int id = json.get("id").asInt();
    memberUCC.confirmRegistration(id);
    throw new WebApplicationException("Le membre est désormais validé", Response.Status.OK);
  }

  /**
   * Decline the registration of the member.
   *
   * @param json to get id of the member to promote
   */
  @POST
  @Path("/declineRegistration")
  @Authorize
  @Admin
  public void declineRegistration(JsonNode json) {
    if (!json.hasNonNull("id") || !json.hasNonNull("reason")) {
      throw new WebApplicationException("id du membre introuvable ou raison de refus introuvable",
          Status.BAD_REQUEST);
    }
    int id = json.get("id").asInt();
    String reason = json.get("reason").asText();
    if (reason.length() == 0) {
      throw new WebApplicationException("Raison non spécifié",
          Status.BAD_REQUEST);
    }

    memberUCC.declineRegistration(id, reason);
    throw new WebApplicationException("Le membre est désormais validé", Status.OK);
  }

  /**
   * Get all subscription requests according to their status. Need admin rights
   *
   * @param status the status subscription members
   * @return a list of memberDTO
   */
  @GET
  @Authorize
  @Admin
  @Path("/subscriptions/{status}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<MemberDTO> getAllInscriptionRequest(@PathParam("status") String status) {
    return memberUCC.getInscriptionRequest(status);
  }


}
