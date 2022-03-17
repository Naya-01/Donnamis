package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InternalServerErrorException extends WebApplicationException {

  public InternalServerErrorException() {
    super(Response.Status.INTERNAL_SERVER_ERROR);
  }

  /**
   * Make an InternalServerErrorException with the custom message.
   *
   * @param message custom error message
   */
  public InternalServerErrorException(String message) {
    super(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(message)
        .type("text/plain")
        .build());
  }
}
