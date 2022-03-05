package be.vinci.pae.business.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UnauthorizedException extends WebApplicationException {

  public UnauthorizedException() {
    super(Response.Status.UNAUTHORIZED);
  }

  public UnauthorizedException(String message) {
    super(Response.status(Response.Status.UNAUTHORIZED)
        .entity(message)
        .type("text/plain")
        .build());
  }

}
