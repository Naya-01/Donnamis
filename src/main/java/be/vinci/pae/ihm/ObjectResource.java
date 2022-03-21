package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.ucc.ObjectUCC;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.ihm.manager.Image;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

@Singleton
@Path("/object")
public class ObjectResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

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
  public ObjectNode getObject(@PathParam("id") int id) {
    ObjectDTO objectDTO = objectUCC.getObject(id);

    return jsonMapper.createObjectNode()
        .putPOJO("object", objectDTO);
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
      @FormDataParam("file") FormDataBodyPart fileMime) {

    MemberDTO memberDTO = (MemberDTO) request.getProperty("user");
    ObjectDTO objectDTO = objectUCC.getObject(id);

    if (objectDTO.getIdOfferor() != memberDTO.getMemberId()) {
      throw new WebApplicationException("Cette objet ne vous appartient pas", Status.UNAUTHORIZED);
    }
    String internalPath = imageManager.writeImageOnDisk(file, fileMime, "objects\\");

    if (imageManager.writeImageOnDisk(file, fileMime, "objects\\") == null) {
      throw new WebApplicationException("Le type du fichier est incorrect."
          + "\nVeuillez soumettre une image", Response.Status.BAD_REQUEST);
    }

    ObjectDTO newDTO = objectUCC.updateObjectPicture(internalPath, memberDTO.getMemberId());

    return newDTO;
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
  public ObjectNode getAllObjectMember(@PathParam("id") int idMember) {
    List<ObjectDTO> objectDTOList = objectUCC.getAllObjectMember(idMember);

    return jsonMapper.createObjectNode()
        .putPOJO("objectList", objectDTOList);
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
    return objectUCC.updateOne(objectDTO);
  }
}
