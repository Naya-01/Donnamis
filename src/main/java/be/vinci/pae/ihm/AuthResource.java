package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.ihm.manager.Token;
import be.vinci.pae.utils.Filters;
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
  private final Filters<MemberImpl> filters = new Filters<>(MemberImpl.class);

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
      throw new WebApplicationException("username or password required",
          Response.Status.BAD_REQUEST);
    }
    String username = json.get("username").asText();
    String password = json.get("password").asText();
    MemberDTO memberDTO = memberUCC.login(username, password);
    if (memberDTO == null) {
      throw new WebApplicationException("username or password incorrect",
          Response.Status.NOT_FOUND);
    }
    String token;
    if (json.get("rememberMe").asBoolean()) {
      token = tokenManager.withRememberMe(memberDTO);
    } else {
      token = tokenManager.withoutRememberMe(memberDTO);
    }

    return jsonMapper.createObjectNode().put("token", token)
        .putPOJO("user", filters.filterPublicJsonView(memberDTO));
  }
}