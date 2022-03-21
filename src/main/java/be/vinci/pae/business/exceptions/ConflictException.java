package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ConflictException extends WebApplicationException {

  public ConflictException() {
    super(Response.Status.CONFLICT);
  }

  /**
   * Make an ConflictException with the custom message.
   *
   * @param message custom error message
   */
  public ConflictException(String message) {
    super(message, Response.Status.CONFLICT);
  }

}
