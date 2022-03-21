package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class FatalException extends WebApplicationException {

  public FatalException() {
    super(Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * Make an InternalServerErrorException with the custom message.
   *
   * @param message custom error message
   */
  public FatalException(String message) {
    super(message, Response.Status.INTERNAL_SERVER_ERROR);
  }
}
