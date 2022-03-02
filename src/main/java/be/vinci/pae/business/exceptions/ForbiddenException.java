package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ForbiddenException extends WebApplicationException {

  public ForbiddenException() {
    super(Response.Status.FORBIDDEN);
  }

  public ForbiddenException(String message) {
    super(message, Response.Status.FORBIDDEN);
  }

}
