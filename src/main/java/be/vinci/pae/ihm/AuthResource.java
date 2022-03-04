package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.ihm.manager.Token;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/auth")
public class AuthResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private MemberUCC memberUCC;
  @Inject
  private Token tokenManager;

  /**
   * Log in a quidam by a pseudo and a password.
   *
   * @param json a json object that contains pseudo and password
   * @return a json object that contains the token or a http error
   */
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(JsonNode json) {

    if (!json.hasNonNull("pseudo") || !json.hasNonNull("password")) {
      throw new WebApplicationException("pseudo or password required", Response.Status.BAD_REQUEST);
    }
    String pseudo = json.get("pseudo").asText();
    String password = json.get("password").asText();
    MemberDTO memberDTO = memberUCC.login(pseudo, password);
    if (memberDTO == null) {
      throw new WebApplicationException("pseudo or password incorrect", Response.Status.NOT_FOUND);
    }
    String accessToken = tokenManager.withoutRememberMe(memberDTO);
    String refreshToken = null;
    if (json.get("rememberMe").asBoolean()) {
      refreshToken = tokenManager.withRememberMe(memberDTO);
    }

    return jsonMapper.createObjectNode()
        .put("access_token", accessToken)
        .put("refresh_token", refreshToken);
  }

  /**
   * Refresh the access token for authenticated member.
   *
   * @param request: to get information request
   * @return a json object that contains the new access token
   */
  @GET
  @Path("/refreshToken")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode refreshToken(@Context ContainerRequest request) {
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    String accessToken = tokenManager.withoutRememberMe(memberDTO);
    return jsonMapper.createObjectNode().put("access_token", accessToken);
  }
}