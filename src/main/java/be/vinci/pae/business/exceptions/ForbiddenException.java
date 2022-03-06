package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ForbiddenException extends WebApplicationException {

  public ForbiddenException() {
    super(Response.Status.FORBIDDEN);
  }

  /**
   * Make an ForbiddenException with the custom message.
   *
   * @param message custom error message
   */
  public ForbiddenException(String message) {
    super(Response.status(Response.Status.FORBIDDEN)
        .entity(message)
        .type("text/plain")
        .build());
  }

}
