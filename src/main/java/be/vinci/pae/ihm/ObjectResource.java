package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.ucc.ObjectUCC;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.ihm.manager.Image;
import be.vinci.pae.utils.JsonViews;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
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
@Path("/object")
public class ObjectResource {

  @Inject
  private ObjectUCC objectUCC;

  @Inject
  private Image imageManager;

  /**
   * GET an object by his id.
   *
   * @param id : the object id
   * @return a json of the object
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectDTO getObject(@PathParam("id") int id) {
    Logger.getLogger("Log").log(Level.INFO, "ObjectResource getObject");
    ObjectDTO objectDTO = objectUCC.getObject(id);
    objectDTO = JsonViews.filterPublicJsonView(objectDTO, ObjectDTO.class);
    return objectDTO;
  }

  /**
   * Set a picture for the object.
   *
   * @param id       of the object
   * @param request  with member data
   * @param file     data
   * @param fileMime mime with the type of file
   * @return object updated
   */
  @POST
  @Path("/setPicture/{id}")
  @Authorize
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectDTO setPicture(@PathParam("id") int id,
      @Context ContainerRequest request,
      @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataBodyPart fileMime,
      @QueryParam("version") int version) {

    Logger.getLogger("Log").log(Level.INFO, "ObjectResource setPicture");
    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");

    if (!imageManager.isAuthorized(fileMime)) {
      throw new ForbiddenException("Le type du fichier est incorrect."
          + "\nVeuillez soumettre une image");
    }

    String internalPath = imageManager.getInternalPath("objects\\", id, fileMime);

    ObjectDTO object = objectUCC.updateObjectPicture(
        internalPath, id, memberDTO.getMemberId(), version);

    imageManager.writeImageOnDisk(file, fileMime, "objects\\", id);

    return JsonViews.filterPublicJsonView(object, ObjectDTO.class);
  }

  /**
   * Get the image of an object.
   *
   * @param id the id of the object
   * @return an image
   */
  @GET
  @Path("/getPicture/{id}")
  @Produces({"image/png", "image/jpg", "image/jpeg"})
  public Response getPicture(@PathParam("id") int id) {
    Logger.getLogger("Log").log(Level.INFO, "ObjectResource getPicture");
    ObjectDTO objectDTO = objectUCC.getObject(id);

    if (objectDTO.getImage() == null) {
      throw new NotFoundException("Cet objet ne possède pas d'image");
    }

    return Response.ok(objectUCC.getPicture(objectDTO.getIdObject())).build();
  }

  /**
   * Find all object of a member.
   *
   * @param idMember : id member that we want to get all his object.
   * @return object list of this member.
   */
  @GET
  @Path("/member/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<ObjectDTO> getAllObjectMember(@PathParam("id") int idMember) {
    Logger.getLogger("Log").log(Level.INFO, "ObjectResource getAllObjectMember");
    List<ObjectDTO> objectDTOList = objectUCC.getAllObjectMember(idMember);
    objectDTOList = JsonViews.filterPublicJsonViewAsList(objectDTOList, ObjectDTO.class);
    return objectDTOList;
  }

  /**
   * Update an object with new information in the ObjectDTO.
   *
   * @param objectDTO : object that we want to update.
   * @return : object update
   */
  @PUT
  @Path("/update")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectDTO updateOne(ObjectDTO objectDTO) {
    Logger.getLogger("Log").log(Level.INFO, "ObjectResource updateOne");
    ObjectDTO object = objectUCC.updateOne(objectDTO);
    object = JsonViews.filterPublicJsonView(object, ObjectDTO.class);
    return object;
  }


}
