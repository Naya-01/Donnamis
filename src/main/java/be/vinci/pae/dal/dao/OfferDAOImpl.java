package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.services.DALBackendService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferDAOImpl implements OfferDAO {

  @Inject
  private DALBackendService dalBackendService;

  @Inject
  private OfferFactory offerFactory;
  @Inject
  private ObjectFactory objectFactory;
  @Inject
  private TypeFactory typeFactory;

  /**
   * Get all offers that match with the search pattern.
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getAll(String searchPattern) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object,"
        + "ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "ty.is_default FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty "
        + "WHERE ob.id_object = of.id_object AND ty.id_type = ob.id_type ";

    if (searchPattern != null && !searchPattern.isEmpty()) {
      // Search /!\ nom de l'offreur, type
      query +=
          "AND (ob.status LIKE '%" + searchPattern + "%' OR o.time_slot LIKE '%" + searchPattern
              + "%')";
    }
    return getOffersWithQuery(query);
  }

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @Override
  public List<OfferDTO> getAllLast() {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, "
        + "ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "ty.is_default FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty "
        + "WHERE of.id_object = ob.id_object AND ty.id_type = ob.id_type ORDER BY of.date "
        + "DESC LIMIT 6";

    return getOffersWithQuery(query);
  }

  /**
   * Get the offer with a specific id.
   *
   * @param idOffer the id of the offer
   * @return an offer that match with the idOffer or null
   */
  @Override
  public OfferDTO getOne(int idOffer) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, "
        + "ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "ty.is_default FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty "
        + "WHERE of.id_object = ob.id_object AND of.id_offer = ? AND ty.id_type = ob.id_type";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idOffer);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();

      List<OfferDTO> offerDTOList = getOffersWithResultSet(resultSet);
      if (offerDTOList == null || offerDTOList.size() != 1) {
        return null;
      }
      return offerDTOList.get(0);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Add an offer in the db.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  @Override
  public OfferDTO addOne(OfferDTO offerDTO) {
    String query = "INSERT INTO donnamis.offers (date, time_slot, id_object) "
        + "VALUES (NOW(), ?, ?) "
        + "RETURNING id_offer, date, time_slot, id_object";

    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      preparedStatement.setString(1, offerDTO.getTimeSlot());
      preparedStatement.setInt(2, offerDTO.getObject().getIdObject());

      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      offerDTO.setIdOffer(resultSet.getInt(1));
      offerDTO.setDate(resultSet.getDate(2).toLocalDate());
      offerDTO.setTimeSlot(resultSet.getString(3));
      offerDTO.getObject().setIdObject(resultSet.getInt(4));
      return offerDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Update the time slot of an offer.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot or null
   */
  @Override
  public OfferDTO updateOne(OfferDTO offerDTO) {
    String query = "UPDATE donnamis.offers SET time_slot = ? "
        + "WHERE id_offer = ? RETURNING id_offer, date, time_slot, id_object";

    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      preparedStatement.setString(1, offerDTO.getTimeSlot());
      preparedStatement.setInt(2, offerDTO.getIdOffer());
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }

      offerDTO.setIdOffer(resultSet.getInt(1));
      offerDTO.setDate(resultSet.getDate(2).toLocalDate());
      offerDTO.setTimeSlot(resultSet.getString(3));
      offerDTO.getObject().setIdObject(resultSet.getInt(4));
      return offerDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get a list of offers according to the query.
   *
   * @param query a query that match with the pattern : SELECT of.id_offer, of.date, of.time_slot,
   *              of.id_object, id_type, description, status, image, id_offeror FROM donnamis.offers
   *              of, donnamis.objects ob
   * @return a list of six offerDTO
   */
  private List<OfferDTO> getOffersWithQuery(String query) {
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      return getOffersWithResultSet(resultSet);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Get a list of offers according to the resultSet.
   *
   * @param resultSet a resultSet created with this kind of query : SELECT of.id_offer, of.date,
   *                  of.time_slot, of.id_object, id_type, description, status, image, id_offeror
   *                  FROM donnamis.offers of, donnamis.objects ob
   * @return a list of offers
   */
  private List<OfferDTO> getOffersWithResultSet(ResultSet resultSet) {
    try {
      List<OfferDTO> listOfferDTO = new ArrayList<>();
      while (resultSet.next()) {

        OfferDTO offerDTO = offerFactory.getOfferDTO();
        offerDTO.setIdOffer(resultSet.getInt(1));
        offerDTO.setDate(resultSet.getDate(2).toLocalDate());
        offerDTO.setTimeSlot(resultSet.getString(3));

        ObjectDTO objectDTO = objectFactory.getObjectDTO();
        objectDTO.setIdObject(resultSet.getInt(4));

        TypeDTO typeDTO = typeFactory.getTypeDTO();
        typeDTO.setId(resultSet.getInt(5));
        typeDTO.setTypeName(resultSet.getString(10));
        typeDTO.setIsDefault(resultSet.getBoolean(11));

        objectDTO.setType(typeDTO);

        objectDTO.setDescription(resultSet.getString(6));
        objectDTO.setStatus(resultSet.getString(7));
        objectDTO.setImage(resultSet.getBytes(8));
        objectDTO.setIdOfferor(resultSet.getInt(9));

        offerDTO.setObject(objectDTO);

        listOfferDTO.add(offerDTO);
      }
      return listOfferDTO;
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    return null;
  }
}