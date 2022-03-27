package be.vinci.pae.ihm.filters;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.ihm.manager.Token;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Singleton
@Provider
@Authorize
public class AuthorizationRequestFilter implements ContainerRequestFilter {

  @Inject
  private Token tokenManager;

  @Override
  public void filter(ContainerRequestContext requestContext) {
    MemberDTO memberDTO = tokenManager.verifyToken(requestContext.getHeaderString("Authorization"));
    if (memberDTO == null) {
      requestContext.abortWith(Response.status(Status.UNAUTHORIZED)
          .entity("A token is needed to access this resource").build());
    } else {
      requestContext.setProperty("user", memberDTO);
    }
  }
}
