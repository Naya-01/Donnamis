package be.vinci.pae.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class FatalException extends WebApplicationException {

  /**
   * Make an InternalServerErrorException.
   */
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

  /**
   * Make an InternalServerErrorException with the Throwable.
   *
   * @param e Throwable.
   */
  public FatalException(Throwable e) {
    super(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
  }
}
