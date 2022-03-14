package be.vinci.pae.ihm.filters;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Singleton
@Provider
@Admin
public class AdminRequestFilter implements ContainerRequestFilter {

  @Override
  public void filter(ContainerRequestContext containerRequestContext) {
    MemberDTO requester = (MemberDTO) containerRequestContext.getProperty("user");
    if(!requester.getRole().equals("administrator")){
      containerRequestContext.abortWith(Response.status(Status.UNAUTHORIZED)
          .entity("Vous n'êtes pas administrateur pour accéder à cette ressource").build());
    }
  }
}
