package be.vinci.pae.ihm.manager;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class TokenImpl implements Token {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));

  private String getToken(MemberDTO memberDTO, Date date) {
    String token = null;
    try {
      token = JWT.create().withIssuer("auth0")
          .withClaim("user", memberDTO.getMemberId())
          .withExpiresAt(date)
          .sign(this.jwtAlgorithm);

    } catch (Exception e) {
      System.out.println("Unable to create token");
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
    Date date = Date.from(Instant.now().plus(3, ChronoUnit.HOURS));
    return getToken(memberDTO, date);
  }
}
