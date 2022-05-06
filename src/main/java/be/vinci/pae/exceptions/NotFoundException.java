package be.vinci.pae.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {

  /**
   * Make a NotFoundException.
   */
  public NotFoundException() {
    super(Response.Status.NOT_FOUND);
  }

  /**
   * Make a NotFoundException with the custom message.
   *
   * @param message custom error message
   */
  public NotFoundException(String message) {
    super(message, Response.Status.NOT_FOUND);
  }

}
