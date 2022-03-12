package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.ucc.AddressUCC;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Singleton
@Path("/address")
public class AddressResource {

  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private AddressUCC addressUCC;

  /**
   * Update an address.
   *
   * @param json a json object that contains new informations for address
   * @return a json object that contains the new address or a http error
   */
  @POST
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode updateOne(JsonNode json) {
    // Verification of all fields in the json except unitNumber
    if (!json.hasNonNull("idAddress")) {
      throw new WebApplicationException("Id de l'adresse requis",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("buildingNumber")) {
      throw new WebApplicationException("Le numéro de l'adresse requis",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("street")) {
      throw new WebApplicationException("Le nom de la rue de l'adresse requis",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("postcode")) {
      throw new WebApplicationException("Le code postal de l'adresse requis",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("commune")) {
      throw new WebApplicationException("La commune de l'adresse requis",
          Response.Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("country")) {
      throw new WebApplicationException("Le pays de l'adresse requis",
          Response.Status.BAD_REQUEST);
    }

    int idAddress = json.get("idAddress").asInt();
    String unitNumber = json.get("unitNumber").asText();
    String buildingNumber = json.get("buildingNumber").asText();
    String street = json.get("street").asText();
    String postcode = json.get("postcode").asText();
    String commune = json.get("commune").asText();
    String country = json.get("country").asText();

    AddressDTO addressDTO = addressUCC.updateOne(idAddress, unitNumber, buildingNumber, street,
        postcode, commune, country);
    if (addressDTO == null) {
      throw new WebApplicationException("Mise à jour de l'adresse impossible",
          Response.Status.NOT_FOUND);
    }
    return jsonMapper.createObjectNode().putPOJO("address", addressDTO);
  }
}
