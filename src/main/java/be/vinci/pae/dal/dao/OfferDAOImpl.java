package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OfferDAOImpl implements OfferDAO {

  @Inject
  private DALService dalService;
  @Inject
  private OfferFactory offerFactory;

  @Override
  public List<OfferDTO> getAll(String searchPattern) {
    String query = "SELECT id_offer, date, time_slot, id_object FROM donnamis.offers ";

    if (searchPattern != null && !searchPattern.isEmpty()) {
      // Search
    }

    try (PreparedStatement preparedStatement = dalService.getPreparedStatement(query)) {
      preparedStatement.executeQuery();

      ResultSet resultSet = preparedStatement.getResultSet();
      List<OfferDTO> listOfferDTO = new ArrayList<>();
      while(resultSet.next()) {
        OfferDTO offerDTO = offerFactory.getOfferDTO();
        offerDTO.setIdOffer(resultSet.getInt(1));
        offerDTO.setDate(resultSet.getDate(2).toLocalDate());
        offerDTO.setTimeSlot(resultSet.getString(3));
        listOfferDTO.add(offerDTO);
      }

      return listOfferDTO;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

}
