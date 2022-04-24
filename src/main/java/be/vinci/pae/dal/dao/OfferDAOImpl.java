package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.BadRequestException;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
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
  private ObjectDAO objectDAO;

  /**
   * Get all offers.
   *
   * @param searchPattern the search pattern (empty -> all) according to their type, description
   * @param idMember      the member id if you want only your offers (0 -> all)
   * @param type          the type of object that we want
   * @return list of offers
   */
  @Override
  public List<OfferDTO> getAll(String searchPattern, int idMember, String type,
      String objectStatus) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, "
        + "       ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "       ty.is_default, of.status "
        + "FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty, donnamis.members mb "
        + "WHERE ob.id_object = of.id_object AND mb.id_member = ob.id_offeror "
        + "AND ty.id_type = ob.id_type AND of.date = (SELECT max(of2.date) "
        + "FROM donnamis.offers of2 WHERE of2.id_object = of.id_object ORDER BY of.date DESC)";

    if (searchPattern != null && !searchPattern.isEmpty()) {
      // Search /!\ nom de l'offreur, type
      query += "AND (LOWER(mb.username) LIKE ? OR LOWER(of.time_slot) LIKE ?"
          + " OR LOWER(ob.description) LIKE ?) ";
    }
    if (type != null && !type.isEmpty()) {
      query += "AND ty.type_name = ? ";
    }
    if (idMember != 0) {
      query += "AND ob.id_offeror = ? ";
    }
    if (objectStatus != null && !objectStatus.isEmpty()) {
      if (objectStatus.equals("available")) {
        query += "AND (LOWER(of.status) LIKE 'available' OR LOWER(of.status) LIKE 'interested') ";
      } else {
        query += "AND LOWER(of.status) LIKE ?";
      }
    }

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      int argCounter = 1;
      if (searchPattern != null && !searchPattern.isEmpty()) {
        for (argCounter = 1; argCounter <= 3; argCounter++) {
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
      if (objectStatus != null && !objectStatus.isEmpty() && !objectStatus.equals("available")) {
        preparedStatement.setString(argCounter, objectStatus);
      }
      return getOffersWithPreparedStatement(preparedStatement);
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
        + "ty.is_default, of.status FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty"
        + " WHERE of.id_object = ob.id_object AND ty.id_type = ob.id_type "
        + "AND of.date = (SELECT max(of2.date) FROM donnamis.offers of2 "
        + "WHERE of2.id_object = of.id_object "
        + "ORDER BY of.date DESC) "
        + "ORDER BY of.date "
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
        + "    ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "    ty.is_default, of.status "
        + "FROM donnamis.types ty , donnamis.objects ob, donnamis.offers of "
        + "WHERE ty.id_type = ob.id_type AND of.id_object = ob.id_object  "
        + "AND of.id_object = (SELECT id_object FROM donnamis.offers "
        + "    WHERE id_offer = ? AND date >= of.date) ORDER BY of.date DESC LIMIT 2";
    return getOfferDTOWithOldDate(idOffer, query);
  }

  /**
   * Get the offer with the id of its object.
   *
   * @param idObject the id of the object
   * @return an offer that match with the idObject or null
   */
  @Override
  public OfferDTO getOneByObject(int idObject) {
    String query = "SELECT of.id_offer, of.date, of.time_slot, of.id_object, "
        + "    ty.id_type, ob.description, ob.status, ob.image, ob.id_offeror, ty.type_name, "
        + "    ty.is_default, of.status "
        + "FROM donnamis.types ty , donnamis.objects ob, donnamis.offers of "
        + "WHERE ty.id_type = ob.id_type AND of.id_object = ob.id_object  "
        + "AND of.id_object = ? ORDER BY of.date DESC LIMIT 2";
    return getOfferDTOWithOldDate(idObject, query);
  }

  private OfferDTO getOfferDTOWithOldDate(int idObject, String query) {
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      List<OfferDTO> offerDTOList = getOffersWithResultSet(resultSet);
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
        + "     ty.is_default, of.status "
        + "FROM donnamis.offers of, donnamis.objects ob, donnamis.types ty "
        + "WHERE ob.id_object = of.id_object  "
        + "  AND ty.id_type = ob.id_type  "
        + "  AND of.id_object= ? "
        + "  AND of.date = "
        + "(SELECT max(of2.date) FROM donnamis.offers of2\n"
        + "WHERE of2.id_object = of.id_object\n"
        + "ORDER BY of.date DESC) ;";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idObject);
      return this.getOfferWithPreparedStatement(preparedStatement);
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
    String query = "INSERT INTO donnamis.offers (date, time_slot, id_object, status) "
        + "VALUES (NOW(), ?, ?, ?) "
        + "RETURNING id_offer, date, time_slot, id_object, status";

    try {
      PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query);
      preparedStatement.setString(1, offerDTO.getTimeSlot());
      preparedStatement.setInt(2, offerDTO.getObject().getIdObject());
      preparedStatement.setString(3, offerDTO.getStatus());
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      offerDTO.setIdOffer(resultSet.getInt(1));
      offerDTO.setDate(resultSet.getDate(2).toLocalDate());
      offerDTO.setTimeSlot(resultSet.getString(3));
      offerDTO.getObject().setIdObject(resultSet.getInt(4));
      offerDTO.setStatus(resultSet.getString(5));

      preparedStatement.close();
      resultSet.close();
      return offerDTO;
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
    String query = "UPDATE donnamis.offers SET time_slot = ?, status = ?";
    ObjectDTO realObject = getOne(offerDTO.getIdOffer()).getObject();
    ObjectDTO objectDTO = null;
    if (offerDTO.getObject() != null) {
      offerDTO.getObject().setIdObject(realObject.getIdObject());
      objectDTO = objectDAO.updateOne(offerDTO.getObject());
    }

    if (offerDTO.getTimeSlot() != null && !offerDTO.getTimeSlot().isEmpty()) {
      query += " WHERE id_offer = ? RETURNING id_offer, date, time_slot, id_object, status";
    } else {
      if (objectDTO != null) {
        offerDTO.setObject(objectDTO);
        return offerDTO;
      }
      throw new BadRequestException("Vous ne modifiez rien");
    }

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setString(1, offerDTO.getTimeSlot());
      preparedStatement.setString(2, offerDTO.getStatus());
      preparedStatement.setInt(3, offerDTO.getIdOffer());
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();

      if (!resultSet.next()) {
        return null;
      }

      OfferDTO offerDTOUpdated = offerFactory.getOfferDTO();
      offerDTOUpdated.setIdOffer(resultSet.getInt(1));
      offerDTOUpdated.setDate(resultSet.getDate(2).toLocalDate());
      offerDTOUpdated.setTimeSlot(resultSet.getString(3));
      offerDTOUpdated.setStatus(resultSet.getString(5));
      if (objectDTO != null) {
        offerDTOUpdated.setObject(objectDTO);
        offerDTOUpdated.getObject().setIdObject(resultSet.getInt(4));
      }
      resultSet.close();
      return offerDTOUpdated;
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
        + "of.status, MAX(of.date) as \"date_premiere_offre\" "
        + "FROM donnamis.objects ob, donnamis.types ty, donnamis.offers of, donnamis.interests it "
        + "WHERE ob.id_object = of.id_object AND ob.id_type = ty.id_type "
        + "AND it.id_object = ob.id_object AND it.status = 'received' AND it.id_member = ? "
        + "AND of.status = 'given' "
        + "GROUP BY of.id_offer, of.date, of.time_slot, of.id_object, ty.id_type, ob.description, "
        + "ob.status, ob.image, ob.id_offeror, ty.type_name, ty.is_default "
        + "ORDER BY date_premiere_offre DESC";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, idReceiver);
      return getOffersWithPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a map of data about a member (nb of received object, nb of not colected objects,
   * nb of given objects and nb of total offers).
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
   * Get a list of offers according to the query.
   *
   * @param query a query that match with the pattern : SELECT of.id_offer, of.date, of.time_slot,
   *              of.id_object, id_type, description, status, image, id_offeror FROM donnamis.offers
   *              of, donnamis.objects ob
   * @return a list of six offerDTO
   */
  private List<OfferDTO> getOffersWithQuery(String query) {
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      return getOffersWithPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a list of offers with a prepared statement.
   *
   * @param preparedStatement a prepared statement that match with the pattern : SELECT of.id_offer,
   *                          of.date, of.time_slot, of.id_object, id_type, description, status,
   *                          image, id_offeror FROM donnamis.offers of, donnamis.objects ob
   * @return a list of OfferDTO
   */
  private List<OfferDTO> getOffersWithPreparedStatement(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      return getOffersWithResultSet(preparedStatement.getResultSet());
    } catch (SQLException e) {
      throw new FatalException();
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
        listOfferDTO.add(getOfferWithTypeAndObject(resultSet.getInt(1),
            resultSet.getDate(2).toLocalDate(), resultSet.getString(3),
            resultSet.getString(12), resultSet.getInt(4),
            resultSet.getString(6), resultSet.getString(7),
            resultSet.getString(8), resultSet.getInt(5),
            resultSet.getString(10), resultSet.getBoolean(11)));
      }
      resultSet.close();
      return listOfferDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private OfferDTO getOfferWithPreparedStatement(PreparedStatement preparedStatement) {
    try (ResultSet resultSet = preparedStatement.executeQuery()) {
      if (resultSet.next()) {
        return null;
      }
      return getOfferWithTypeAndObject(resultSet.getInt(1),
          resultSet.getDate(2).toLocalDate(), resultSet.getString(3),
          resultSet.getString(12), resultSet.getInt(4),
          resultSet.getString(6), resultSet.getString(7),
          resultSet.getString(8), resultSet.getInt(5),
          resultSet.getString(10), resultSet.getBoolean(11));

    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private OfferDTO getOfferWithTypeAndObject(int idOffer, LocalDate date, String timeSlot,
      String statusOffer, int idObject, String descriptionObject, String statusObject,
      String imageObject, int idType, String typeName, boolean isTypeDefault) {

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setIdOffer(idOffer);
    offerDTO.setDate(date);
    offerDTO.setTimeSlot(timeSlot);
    offerDTO.setStatus(statusOffer);

    TypeDTO typeDTO = typeFactory.getTypeDTO();
    typeDTO.setId(idType);
    typeDTO.setTypeName(typeName);
    typeDTO.setIsDefault(isTypeDefault);

    ObjectDTO objectDTO = objectDAO.getObject(idObject, descriptionObject, statusObject,
        imageObject, idOffer);
    objectDTO.setType(typeDTO);
    offerDTO.setObject(objectDTO);

    return offerDTO;
  }
}