package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.AddressUCC;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.JsonViews;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/address")
public class AddressResource {

  @Inject
  private AddressUCC addressUCC;

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  @PUT
  @Authorize
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public AddressDTO updateOne(AddressDTO addressDTO, @Context ContainerRequest containerRequest) {
    Logger.getLogger("Log").log(Level.INFO, "AddressResource updateOne");
    MemberDTO memberDTO = (MemberDTO) containerRequest.getProperty("user");
    if (memberDTO.getMemberId() != addressDTO.getIdMember()) {
      throw new UnauthorizedException("Not your address");
    }
    return JsonViews.filterPublicJsonView(addressUCC.updateOne(addressDTO), AddressDTO.class);
  }
}
