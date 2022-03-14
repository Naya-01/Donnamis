package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfferDAOImpl implements OfferDAO {

  @Inject
  private DALService dalService;
  @Inject
  private OfferFactory offerFactory;
  @Inject
  private ObjectFactory objectFactory;

  /**
   * Get all offers that match with the search pattern.
   *
   * @param searchPattern the search pattern to find offers according to their type, description
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getAll(String searchPattern) {
    String query =
        "SELECT o.id_offer, o.date, o.time_slot, ob.id_object, ob.id_type, ob.description, "
            + "ob.status, ob.image, ob.id_offeror, t.id_type, t.is_default, t.type_name "
            + "FROM donnamis.offers o, donnamis.objects ob, donnamis.types t "
            + "WHERE o.id_object = ob.id_object AND t.id_type = ob.id_type ";

    if (searchPattern != null && !searchPattern.isEmpty()) {
      // Search /!\ nom de l'offreur, type
      query +=
          "AND (ob.status LIKE '%" + searchPattern + "%' OR o.time_slot LIKE '%" + searchPattern
              + "%')";
    }
    return getOffers(query);
  }

  /**
   * Get the last six offers posted.
   *
   * @return a list of six offerDTO
   */
  @Override
  public List<OfferDTO> getAllLast() {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, "
        + "ob.id_object, id_type, description, status, image, id_offeror "
        + "FROM donnamis.offers of, donnamis.objects ob "
        + "WHERE of.id_object = ob.id_object ORDER BY of.date DESC LIMIT 6";

    return getOffers(query);
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
      PreparedStatement preparedStatement = dalService.getPreparedStatement(query);
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
   * Get a list of offers according to the query.
   *
   * @param query a query that match with the pattern :
   *     SELECT of.id_offer, of.date, of.time_slot, of.id_object,
   *     ob.id_object, id_type, description, status, image, id_offeror
   *     FROM donnamis.offers of, donnamis.objects ob
   *
   * @return a list of six offerDTO
   */
  public List<OfferDTO> getOffers(String query) {
    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      List<OfferDTO> listOfferDTO = new ArrayList<>();
      while (resultSet.next()) {
        OfferDTO offerDTO = offerFactory.getOfferDTO();
        offerDTO.setIdOffer(resultSet.getInt(1));
        offerDTO.setDate(resultSet.getDate(2).toLocalDate());
        offerDTO.setTimeSlot(resultSet.getString(3));

        ObjectDTO objectDTO = objectFactory.getObjectDTO();
        objectDTO.setIdObject(resultSet.getInt(4));
        objectDTO.setIdType(resultSet.getInt(5));
        objectDTO.setDescription(resultSet.getString(6));
        objectDTO.setStatus(resultSet.getString(7));
        objectDTO.setImage(resultSet.getBytes(8));
        objectDTO.setIdOfferor(resultSet.getInt(9));

        offerDTO.setObject(objectDTO);

        listOfferDTO.add(offerDTO);
      }
      return listOfferDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}