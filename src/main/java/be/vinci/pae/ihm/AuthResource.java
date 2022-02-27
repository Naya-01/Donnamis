package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Singleton
@Path("/auth")
public class AuthResource {

  private MemberUCC memberUCC = new MemberUCC();
  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("/login")
  public ObjectNode login(JsonNode json) {
    if (!json.hasNonNull("pseudo") || !json.hasNonNull("password")) {
      throw new WebApplicationException("pseudo or password required", Response.Status.BAD_REQUEST);
    }
    String pseudo = json.get("pseudo").asText();
    String password = json.get("password").asText();

    MemberDTO memberDTO = memberUCC.login(pseudo, password);
    if (memberDTO == null) {
      throw new WebApplicationException("pseudo or password incorrect",
          Response.Status.UNAUTHORIZED);
    }

    String token;
    try {
      token = JWT.create().withIssuer("auth0")
          .withClaim("user", memberDTO.getMemberId()).sign(this.jwtAlgorithm);
      return jsonMapper.createObjectNode().put("token", token);
    } catch (Exception e) {
      System.out.println("Unable to create token");
      return null;
    }
  }
}