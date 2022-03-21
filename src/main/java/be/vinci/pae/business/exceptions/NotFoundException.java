package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {

  public NotFoundException() {
    super(Response.Status.NOT_FOUND);
  }

  /**
   * Make an NotFoundException with the custom message.
   *
   * @param message custom error message
   */
  public NotFoundException(String message) {
    super(message, Response.Status.NOT_FOUND);
  }

}
