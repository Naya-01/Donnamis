package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.ucc.MemberUCC;
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
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
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
      @FormDataParam("file") FormDataBodyPart fileMime) {

    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");

    String internalPath = imageManager.writeImageOnDisk(file, fileMime, "profils\\");

    if (internalPath == null) {
      throw new WebApplicationException("Le type du fichier est incorrect."
          + "\nVeuillez soumettre une image", Response.Status.BAD_REQUEST);
    }
    return memberUCC.updateProfilPicture(internalPath, memberDTO.getMemberId());
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
    MemberDTO requestMember = (MemberDTO) request.getProperty("user");
    if (!requestMember.getRole().equals("administrator")
        && (memberDTO.getMemberId() != requestMember.getMemberId() || memberDTO.getRole() != null
        || memberDTO.getStatus() != null || memberDTO.getReasonRefusal() != null)) {
      throw new UnauthorizedException();
    }
    return JsonViews.filterPublicJsonView(memberUCC.updateMember(memberDTO), MemberDTO.class);
  }
}