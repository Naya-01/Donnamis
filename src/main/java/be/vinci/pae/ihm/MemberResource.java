package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.ihm.filters.Admin;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.ihm.manager.Image;
import be.vinci.pae.utils.JsonViews;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/member")
public class MemberResource {

  @Inject
  private MemberUCC memberUCC;
  @Inject
  private Image imageManager;

  /**
   * Set a picture for the member.
   *
   * @param request  information of the member
   * @param file     data
   * @param fileMime mime with the picture type
   * @return member updated
   */
  @POST
  @Path("/setPicture")
  @Authorize
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public MemberDTO setPicture(@Context ContainerRequest request,
      @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataBodyPart fileMime,
      @QueryParam("version") int version) {

    Logger.getLogger("Log").log(Level.INFO, "MemberResource setPicture");
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    if (!imageManager.isAuthorized(fileMime)) {
      throw new BadRequestException("Le type du fichier est incorrect."
          + "\nVeuillez soumettre une image");
    }

    String internalPath = imageManager.getInternalPath("profils\\", memberDTO.getMemberId(),
        fileMime);
    imageManager.writeImageOnDisk(file, fileMime, "profils\\",
        memberDTO.getMemberId());
    return memberUCC.updateProfilPicture(internalPath, memberDTO.getMemberId(), version);
  }

  /**
   * Get the image of a member.
   *
   * @param id the id of the member
   * @return an image
   */
  @GET
  @Path("/getPicture/{id}")
  @Produces({"image/png", "image/jpg", "image/jpeg"})
  public Response getPicture(@PathParam("id") int id) {
    Logger.getLogger("Log").log(Level.INFO, "MemberResource getPicture");

    MemberDTO memberDTO = memberUCC.getMember(id);

    if (memberDTO.getImage() == null) {
      throw new NotFoundException("Ce membre ne possède pas de photo de profil");
    }

    return Response.ok(memberUCC.getPicture(memberDTO.getMemberId())).build();
  }

  /**
   * Get a user by his token.
   *
   * @param request to get information request
   * @return return the linked user to his token
   */
  @GET
  @Path("/getMemberByToken")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public MemberDTO getMemberByToken(@Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "MemberResource getMemberByToken");
    return JsonViews.filterPublicJsonView((MemberDTO) request.getProperty("user"), MemberDTO.class);
  }

  /**
   * Get a user by his id.
   *
   * @param id the id of the member we want to get
   * @return return the linked user to his id
   */
  @GET
  @Path("/id/{id}")
  @Authorize
  @Produces(MediaType.APPLICATION_JSON)
  public MemberDTO getMemberById(@PathParam("id") int id) {
    Logger.getLogger("Log").log(Level.INFO, "MemberResource getMemberById");

    return JsonViews.filterPublicJsonView(memberUCC.getMember(id), MemberDTO.class);
  }

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members, pending -> pending members,
   *               denied -> denied members, valid -> valid members, empty -> all members
   * @return a list of MemberDTO
   */
  @GET
  @Path("/search")
  @Produces(MediaType.APPLICATION_JSON)
  @Admin
  public List<MemberDTO> searchMembers(@DefaultValue("") @QueryParam("search") String search,
      @DefaultValue("") @QueryParam("status") String status) {
    Logger.getLogger("Log").log(Level.INFO, "MemberResource searchMembers");

    return JsonViews.filterPublicJsonViewAsList(memberUCC.searchMembers(search, status),
        MemberDTO.class);
  }

  /**
   * Update any attribute of a member. You need to be an administrator to change other member,
   * status or role.
   *
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  @PUT
  @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public MemberDTO updateMember(MemberDTO memberDTO, @Context ContainerRequest request) {
    Logger.getLogger("Log").log(Level.INFO, "MemberResource updateMember");

    MemberDTO requestMember = (MemberDTO) request.getProperty("user");
    if (!requestMember.getRole().equals("administrator")
        && (!memberDTO.getMemberId().equals(requestMember.getMemberId())
        || memberDTO.getRole() != null || memberDTO.getStatus() != null
        || memberDTO.getReasonRefusal() != null)) {
      throw new UnauthorizedException();
    }
    return JsonViews.filterPublicJsonView(memberUCC.updateMember(memberDTO), MemberDTO.class);
  }

  /**
   * Update a member status and update its assigned interests into a prevented status.
   *
   * @param memberDTO member who has a prevented
   * @return the filter member updated with a 'prevented' status
   */
  @PUT
  @Path("/toPrevented")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Admin
  public MemberDTO preventMember(MemberDTO memberDTO) {
    Logger.getLogger("Log").log(Level.INFO, "MemberResource preventMember");

    if (memberDTO.getMemberId() == null) {
      throw new BadRequestException("Identifiant du membre manquant !");
    }
    if (memberDTO.getVersion() == null) {
      throw new BadRequestException("Attribut 'version' manquant ! ");
    }

    return JsonViews.filterPublicJsonView(memberUCC.preventMember(memberDTO), MemberDTO.class);
  }

}