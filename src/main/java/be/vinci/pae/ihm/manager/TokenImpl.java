package be.vinci.pae.ihm.manager;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.exceptions.TokenDecodingException;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TokenImpl implements Token {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).withIssuer("auth0")
      .build();

  @Inject
  private MemberUCC memberUCC;

  /**
   * Make a token with an expiration date.
   *
   * @param memberDTO Member that'll receive the token
   * @param date      the expiration date token
   * @return a token
   */
  private String getToken(MemberDTO memberDTO, Date date) {
    String token = null;
    try {
      token = JWT.create().withIssuer("auth0")
          .withClaim("user", memberDTO.getMemberId())
          .withExpiresAt(date)
          .sign(this.jwtAlgorithm);

    } catch (Exception e) {
      throw new TokenDecodingException("Impossibilité de créer un token");
    }
    return token;
  }

  /**
   * Make a token with a long expire date.
   *
   * @param memberDTO : member want to be authenticated
   * @return a token
   */
  @Override
  public String withRememberMe(MemberDTO memberDTO) {
    Date date = Date.from(Instant.now().plus(90, ChronoUnit.DAYS));
    return getToken(memberDTO, date);
  }

  /**
   * Make a token with a short expire date.
   *
   * @param memberDTO : member want to be authenticated
   * @return a token
   */
  @Override
  public String withoutRememberMe(MemberDTO memberDTO) {
    Date date = Date.from(Instant.now().plus(2, ChronoUnit.HOURS));
    return getToken(memberDTO, date);
  }

  /**
   * Verify a token given.
   *
   * @param token a string of the token
   * @return the member bound to the given token or null
   */
  @Override
  public MemberDTO verifyToken(String token) {
    if (token != null) {
      try {
        DecodedJWT decodedToken = this.jwtVerifier.verify(token);
        return memberUCC.getMember(decodedToken.getClaim("user").asInt());
      } catch (Exception e) {
        throw new TokenDecodingException(e);
      }
    }
    return null;
  }
}
