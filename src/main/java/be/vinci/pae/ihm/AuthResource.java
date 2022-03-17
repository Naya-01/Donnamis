package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.ihm.manager.Token;
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
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/auth")
public class AuthResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private MemberUCC memberUCC;
  @Inject
  private Token tokenManager;

  /**
   * Log in a quidam by a username and a password.
   *
   * @param json a json object that contains username and password
   * @return a json object that contains the token or a http error
   */
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(JsonNode json) {

    if (!json.hasNonNull("username") || !json.hasNonNull("password")) {
      throw new WebApplicationException("Pseudonyme ou mot de passe requis",
          Response.Status.BAD_REQUEST);
    }
    String username = json.get("username").asText();
    String password = json.get("password").asText();
    MemberDTO memberDTO = memberUCC.login(username, password);
    if (memberDTO == null) {
      throw new WebApplicationException("Pseudonyme ou mot de passe incorrect",
          Response.Status.NOT_FOUND);
    }
    String accessToken = tokenManager.withoutRememberMe(memberDTO);
    String refreshToken;
    if (json.get("rememberMe").asBoolean()) {
      refreshToken = tokenManager.withRememberMe(memberDTO);
    } else {
      refreshToken = accessToken;
    }

    return jsonMapper.createObjectNode()
        .put("access_token", accessToken)
        .put("refresh_token", refreshToken)
        .putPOJO("user", JsonViews.filterPublicJsonView(memberDTO, MemberDTO.class));
  }

  /**
   * Refresh the access token for authenticated member.
   *
   * @param request to get information request
   * @return a json object that contains the new access token
   */
  @GET
  @Path("/refreshToken")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode refreshToken(@Context ContainerRequest request) {
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    String accessToken = tokenManager.withoutRememberMe(memberDTO);
    return jsonMapper.createObjectNode()
        .put("access_token", accessToken)
        .putPOJO("user", JsonViews.filterPublicJsonView(memberDTO, MemberDTO.class));
  }

  /**
   * Get a user by his token.
   *
   * @param request to get information request
   * @return return the linked user to his token
   */
  @POST
  @Path("/getuserbytoken")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode getUserByToken(@Context ContainerRequest request) {
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    return jsonMapper.createObjectNode()
        .putPOJO("user", JsonViews.filterPublicJsonView(memberDTO, MemberDTO.class));
  }

  /**
   * Register a quidam.
   *
   * @param user : all information of the quidam.
   * @return a json object that contains the token.
   */
  @POST
  @Path("register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode register(MemberDTO user) {
    // Get and check credentials
    if (user == null || user.getPassword() == null || user.getPassword().isBlank()
        || user.getUsername() == null || user.getUsername().isBlank()) {
      throw new WebApplicationException("login or password required", Response.Status.BAD_REQUEST);
    }
    // Try to login
    ObjectNode publicUser = memberUCC.register(user);
    if (publicUser == null) {
      throw new WebApplicationException("this resource already exists", Response.Status.CONFLICT);
    }
    return publicUser;

  }

  /**
   * Get a user by his id.
   *
   * @param id the id of the member we want to get
   * @return return the linked user to his id
   */
  @GET
  @Path("/id/{id}")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public MemberDTO getUserById(@PathParam("id") int id) {
    System.out.println("id");
    return memberUCC.getMember(id);
  }

  /**
   * Get all subscription requests according to their status. Need admin rights
   *
   * @param request to get information request
   * @param status  the status subscription members
   * @return a list of memberDTO
   */
  @GET
  @Authorize
  @Path("/subscriptions/{status}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<MemberDTO> getRefusedInscriptionRequest(@Context ContainerRequest request,
      @PathParam("status") String status) {
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    if (!memberDTO.getRole().equals("administrator")) {
      throw new UnauthorizedException("Need admin right");
    }
    return memberUCC.getInscriptionRequest(status);
  }


}
