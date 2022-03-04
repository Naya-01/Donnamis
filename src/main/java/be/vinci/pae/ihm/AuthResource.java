package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.ihm.manager.Token;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
    String access_token = tokenManager.withoutRememberMe(memberDTO);
    String refresh_token = null;
    if (json.get("rememberMe").asBoolean()) {
      refresh_token = tokenManager.withRememberMe(memberDTO);
    }

    return jsonMapper.createObjectNode()
        .put("access_token", access_token)
        .put("refresh_token", refresh_token);
  }
}