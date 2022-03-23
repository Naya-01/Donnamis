package be.vinci.pae.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class TokenDecodingException extends WebApplicationException {

  public TokenDecodingException() {
    super(Response.Status.UNAUTHORIZED);
  }

  /**
   * Make an TokenDecodingException with the custom message.
   *
   * @param message custom error message
   */
  public TokenDecodingException(String message) {
    super(message, Response.Status.UNAUTHORIZED);
  }

  public TokenDecodingException(Throwable cause) {
    super(cause.getMessage(), Response.Status.UNAUTHORIZED);
  }
}
