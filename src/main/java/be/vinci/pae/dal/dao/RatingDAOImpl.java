package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.business.factories.RatingFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RatingDAOImpl implements RatingDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private RatingFactory ratingFactory;

  /**
   * Get a rating we want to retrieve by his object id.
   *
   * @param id : the id of the object of the rating we want to retrieve
   * @return the rating
   */
  @Override
  public RatingDTO getOne(int id) {
    String query = "SELECT id_object, id_member, comment, rating "
        + "FROM donnamis.ratings WHERE id_object = ?";
    RatingDTO ratingDTO = ratingFactory.getRatingDTO();
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      ratingDTO.setIdObject(resultSet.getInt(1));
      ratingDTO.setIdMember(resultSet.getInt(2));
      ratingDTO.setComment(resultSet.getString(3));
      ratingDTO.setRating(resultSet.getInt(4));
      resultSet.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return ratingDTO;
  }
}
