package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class NotFoundException extends WebApplicationException {

  public NotFoundException() {
    super(Response.Status.NOT_FOUND);
  }

  public NotFoundException(String message) {
    super(Response.status(Response.Status.UNAUTHORIZED)
        .entity(message)
        .type("text/plain")
        .build());
  }

}
