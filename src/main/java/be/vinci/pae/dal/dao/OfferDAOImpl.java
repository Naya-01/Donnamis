package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OfferDAOImpl implements OfferDAO {

  @Inject
  private DALBackendService dalBackendService;

  @Inject
  private OfferFactory offerFactory;
  @Inject
  private TypeFactory typeFactory;
  @Inject
  private ObjectFactory objectFactory;

  /**
   * Get all offers.
   *
   * @param searchPattern the search pattern (empty -> all) according to their type, description,
   *                      username and lastname
   * @param idMember      the member id if you want only your offers (0 -> all)
   * @param type          the type of object that we want
   * @param objectStatus  the status of object that we want
   * @param dateText      the max date late
   * @return list of offers
   */
  @Override
  public List<OfferDTO> getAll(String searchPattern, int idMember, String type,
      String objectStatus, String dateText) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, "
        + "       ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "       ty.is_default, of.status, of.version, ob.version "
        + "FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty, donnamis.members mb "
        + "WHERE ob.id_object = of.id_object AND mb.id_member = ob.id_offeror "
        + "AND ty.id_type = ob.id_type AND of.date = (SELECT max(of2.date) "
        + "FROM donnamis.offers of2 WHERE of2.id_object = of.id_object ORDER BY of.date DESC)";

    if (searchPattern != null && !searchPattern.isEmpty()) {
      query += "AND (LOWER(mb.username) LIKE ? OR LOWER(of.time_slot) LIKE ?"
          + " OR LOWER(ob.description) LIKE ? OR LOWER(mb.lastname) LIKE ?) ";
    }
    if (type != null && !type.isEmpty()) {
      query += "AND ty.type_name = ? ";
    }
    if (idMember != 0) {
      query += "AND ob.id_offeror = ? ";
    }
    if (objectStatus != null && !objectStatus.isEmpty()) {
      query += "AND LOWER(of.status) LIKE ? ";
    }
    if (dateText != null && !dateText.isBlank()) {
      query += "AND of.date >= ? ";
    }
    query += " ORDER BY of.date DESC";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      int argCounter = 1;
      if (searchPattern != null && !searchPattern.isEmpty()) {
        for (argCounter = 1; argCounter <= 4; argCounter++) {
          preparedStatement.setString(argCounter, "%" + searchPattern.toLowerCase() + "%");
        }
      }
      if (type != null && !type.isEmpty()) {
        preparedStatement.setString(argCounter, type);
        argCounter++;
      }
      if (idMember != 0) {
        preparedStatement.setInt(argCounter, idMember);
        argCounter++;
      }
      if (objectStatus != null && !objectStatus.isEmpty()) {
        preparedStatement.setString(argCounter, objectStatus);
        argCounter++;
      }
      if (dateText != null && !dateText.isBlank()) {
        preparedStatement.setDate(argCounter, Date.valueOf(dateText));
      }
      return getOffersWithResultSet(preparedStatement.executeQuery());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
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
        + "ty.is_default, of.status, of.version, ob.version FROM donnamis.offers of, "
        + "donnamis.objects ob, donnamis.types ty WHERE of.id_object = ob.id_object "
        + "AND ty.id_type = ob.id_type AND of.date = (SELECT max(of2.date) "
        + "FROM donnamis.offers of2 WHERE of2.id_object = of.id_object ORDER BY of.date DESC) "
        + "AND of.status != 'not_collected' "
        + "AND of.status != 'cancelled' "
        + "ORDER BY of.date DESC LIMIT 6";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      return getOffersWithResultSet(preparedStatement.executeQuery());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
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
        + "    ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "    ty.is_default, of.status, of.version, ob.version "
        + "FROM donnamis.types ty , donnamis.objects ob, donnamis.offers of "
        + "WHERE ty.id_type = ob.id_type AND of.id_object = ob.id_object  "
        + "AND of.id_object = (SELECT id_object FROM donnamis.offers "
        + "    WHERE id_offer = ? AND date >= of.date) ORDER BY of.date DESC LIMIT 2";
    return getOfferDTOWithOldDate(idOffer, query);
  }

  /**
   * Construct an offer with its old date on base of a query.
   *
   * @param idObject the id of the object
   * @param query the SQL query to execute
   * @return the constructed offerDTO
   */
  private OfferDTO getOfferDTOWithOldDate(int idObject, String query) {
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      List<OfferDTO> offerDTOList = getOffersWithResultSet(preparedStatement.executeQuery());
      if (offerDTOList.isEmpty()) {
        return null;
      }
      if (offerDTOList.size() == 2) {
        LocalDate oldDate = offerDTOList.get(1).getDate();
        offerDTOList.get(0).setOldDate(oldDate);
      }
      return offerDTOList.get(0);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get last offer of an object.
   *
   * @param idObject the id of the object
   * @return an offer
   */
  @Override
  public OfferDTO getLastObjectOffer(int idObject) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object,"
        + "     ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "     ty.is_default, of.status, of.version, ob.version "
        + "FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty "
        + "WHERE ob.id_object = of.id_object  "
        + "  AND ty.id_type = ob.id_type  "
        + "  AND of.id_object= ? "
        + "  AND of.date = "
        + "(SELECT max(of2.date) FROM donnamis.offers of2 "
        + "WHERE of2.id_object = of.id_object "
        + "ORDER BY of.date DESC) ;";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (!resultSet.next()) {
          return null;
        }
        return getOfferWithResultSet(resultSet);
      } catch (SQLException e) {
        throw new FatalException(e);
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Add an offer in the db.
   *
   * @param offerDTO an offer we want to add in the db
   * @return the offerDTO added
   */
  @Override
  public OfferDTO addOne(OfferDTO offerDTO) {
    String query = "INSERT INTO donnamis.offers (date, time_slot, id_object, status, version) "
        + "VALUES (NOW(), ?, ?, ?, 1) "
        + "RETURNING id_offer, date, time_slot, id_object, status, version";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, offerDTO.getTimeSlot());
      preparedStatement.setInt(2, offerDTO.getObject().getIdObject());
      preparedStatement.setString(3, offerDTO.getStatus());

      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (!resultSet.next()) {
          return null;
        }

        offerDTO.setIdOffer(resultSet.getInt(1));
        offerDTO.setDate(resultSet.getDate(2).toLocalDate());
        offerDTO.setTimeSlot(resultSet.getString(3));
        offerDTO.getObject().setIdObject(resultSet.getInt(4));
        offerDTO.setStatus(resultSet.getString(5));
        offerDTO.setVersion(resultSet.getInt(6));

        return offerDTO;
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Update the time slot of an offer.
   *
   * @param offerDTO an offerDTO that contains the new time slot and the id of the offer
   * @return an offerDTO with the id and the new time slot or null
   */
  @Override
  public OfferDTO updateOne(OfferDTO offerDTO) {
    String query = "UPDATE donnamis.offers SET time_slot = ?, status = ?, version = version + 1";

    if (offerDTO.getTimeSlot() != null && !offerDTO.getTimeSlot().isEmpty()) {
      query += " WHERE id_offer = ? RETURNING id_offer, date, time_slot, id_object, status";
    } else {
      throw new BadRequestException("Vous ne modifiez rien");
    }

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, offerDTO.getTimeSlot());
      preparedStatement.setString(2, offerDTO.getStatus());
      preparedStatement.setInt(3, offerDTO.getIdOffer());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        if (!resultSet.next()) {
          return null;
        }
        OfferDTO offerDTOUpdated = offerFactory.getOfferDTO();
        offerDTOUpdated.setIdOffer(resultSet.getInt(1));
        offerDTOUpdated.setDate(resultSet.getDate(2).toLocalDate());
        offerDTOUpdated.setTimeSlot(resultSet.getString(3));
        offerDTOUpdated.setStatus(resultSet.getString(5));
        offerDTOUpdated.setObject(offerDTO.getObject());
        return offerDTOUpdated;
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getAllGivenOffers(int idReceiver) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, ty.id_type, "
        + "ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, ty.is_default, "
        + "of.status, of.version, ob.version, MAX(of.date) as \"date_premiere_offre\" "
        + "FROM donnamis.objects ob, donnamis.types ty, donnamis.offers of, donnamis.interests it "
        + "WHERE ob.id_object = of.id_object AND ob.id_type = ty.id_type "
        + "AND it.id_object = ob.id_object AND it.status = 'received' AND it.id_member = ? "
        + "AND of.status = 'given' "
        + "GROUP BY of.id_offer, of.date, of.time_slot, of.id_object, ty.id_type, ob.description, "
        + "of.version, ob.version, ob.status, ob.image, ob.id_offeror, ty.type_name, ty.is_default "
        + "ORDER BY date_premiere_offre DESC";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idReceiver);
      return getOffersWithResultSet(preparedStatement.executeQuery());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get all offers received by a member.
   *
   * @param idReceiver the id of the receiver
   * @param searchPattern the search pattern (empty -> all) according to their type, description
   * @return a list of offerDTO
   */
  @Override
  public List<OfferDTO> getAllGivenAndAssignedOffers(int idReceiver, String searchPattern) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, ty.id_type, "
        + "ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, ty.is_default, "
        + "of.status, of.version, ob.version, MAX(of.date) as \"date_premiere_offre\" "
        + "FROM donnamis.objects ob, donnamis.types ty, donnamis.offers of, donnamis.interests it "
        + "WHERE ob.id_object = of.id_object AND ob.id_type = ty.id_type "
        + "AND it.id_object = ob.id_object AND (it.status = 'received' or it.status = 'assigned')"
        + " AND it.id_member = ? AND (of.status = 'given' OR of.status = 'assigned') ";

    if (searchPattern != null && !searchPattern.isBlank()) {
      query += "AND (LOWER(of.time_slot) LIKE ? OR LOWER(ob.description) LIKE ?) ";
    }
    query += "GROUP BY of.id_offer, of.date, of.time_slot, of.id_object, ty.id_type, "
        + "ob.description, of.version, ob.version, ob.status, ob.image, ob.id_offeror, "
        + "ty.type_name, ty.is_default ORDER BY date_premiere_offre DESC";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idReceiver);
      if (searchPattern != null && !searchPattern.isBlank()) {
        for (int i = 2; i <= 3; i++) {
          preparedStatement.setString(i, "%" + searchPattern.toLowerCase() + "%");
        }
      }
      return getOffersWithResultSet(preparedStatement.executeQuery());
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Get a map of data about a member (nb of received object, nb of not colected objects, nb of
   * given objects and nb of total offers).
   *
   * @param idReceiver the id of the member
   * @return a map with all th datas.
   */
  @Override
  public Map<String, Integer> getOffersCount(int idReceiver) {
    String query = "SELECT count(case i.status when 'received' then 1 end) as nbReceived, "
        + "count(case i.status when 'not_collected' then 1 end) as nbNotCollected, "
        + "(SELECT count(o.*) FROM donnamis.objects o WHERE o.id_offeror = ? "
        + "AND o.status = 'given') as nbGiven, (SELECT count(o2.*) FROM donnamis.objects o2, "
        + "donnamis.offers of2 WHERE o2.id_offeror = ? AND of2.id_object = o2.id_object "
        + "AND of2.date = (SELECT MAX(of21.date) FROM donnamis.offers of21 "
        + "WHERE of21.id_object = of2.id_object)) as nbOffers FROM donnamis.interests i "
        + "WHERE i.id_member = ?";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      for (int i = 1; i <= 3; i++) {
        preparedStatement.setInt(i, idReceiver);
      }
      preparedStatement.execute();
      try (ResultSet resultSet = preparedStatement.getResultSet()) {
        if (!resultSet.next()) {
          return null;
        }
        return Map.of(
            "nbReceived", resultSet.getInt(1),
            "nbNotCollected", resultSet.getInt(2),
            "nbGiven", resultSet.getInt(3),
            "nbOffers", resultSet.getInt(4)
        );
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
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
        listOfferDTO.add(getOfferWithResultSet(resultSet));
      }
      resultSet.close();
      return listOfferDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Construct an offer on base of the resultset.
   *
   * @param resultSet that contains all information for the offers
   * @return the constructed offerDTO
   */
  private OfferDTO getOfferWithResultSet(ResultSet resultSet) {
    try {
      OfferDTO offerDTO = offerFactory.getOfferDTO();
      offerDTO.setIdOffer(resultSet.getInt(1));
      offerDTO.setDate(resultSet.getDate(2).toLocalDate());
      offerDTO.setTimeSlot(resultSet.getString(3));
      offerDTO.setStatus(resultSet.getString(12));
      offerDTO.setVersion(resultSet.getInt(13));
      offerDTO.setIdObject(resultSet.getInt(4));

      TypeDTO typeDTO = typeFactory.getTypeDTO();
      typeDTO.setIdType(resultSet.getInt(5));
      typeDTO.setTypeName(resultSet.getString(10));
      typeDTO.setIsDefault(resultSet.getBoolean(11));

      ObjectDTO objectDTO = objectFactory.getObjectDTO();
      objectDTO.setIdObject(resultSet.getInt(4));
      objectDTO.setDescription(resultSet.getString(6));
      objectDTO.setStatus(resultSet.getString(7));
      objectDTO.setImage(resultSet.getString(8));
      objectDTO.setIdOfferor(resultSet.getInt(9));
      objectDTO.setVersion(resultSet.getInt(14));
      objectDTO.setType(typeDTO);

      offerDTO.setObject(objectDTO);
      return offerDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}