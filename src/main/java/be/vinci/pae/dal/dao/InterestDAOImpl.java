package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestDAOImpl implements InterestDAO {

  @Inject
  private InterestFactory interestFactory;
  @Inject
  private AbstractDAO abstractDAO;

  /**
   * Get an interest we want to retrieve by the id of the interested member and the id of the
   * object.
   *
   * @param idObject : the object id of the interest we want to retrieve.
   * @param idMember :  the member id of the interest we want to retrieve.
   * @return the interest.
   */
  @Override
  public InterestDTO getOne(int idObject, int idMember) {
    String condition = "id_object = ? AND id_member = ?";
    List<Object> values = new ArrayList<>();
    values.add(idObject);
    values.add(idMember);
    ArrayList<Class> types = new ArrayList<>();
    types.add(InterestDTO.class);

    try (PreparedStatement preparedStatement = abstractDAO.getOne(condition, values, types)) {
      return getInterestDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Make an interestDTO with the result set.
   *
   * @param preparedStatement : a prepared statement to execute the query.
   * @return the interest.
   */
  private InterestDTO getInterestDTO(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      // Create the interestDTO if we have a result
      InterestDTO interestDTO = interestFactory.getInterestDTO();
      interestDTO.setIdObject(resultSet.getInt("id_object"));
      interestDTO.setIdMember(resultSet.getInt("id_member"));
      interestDTO.setAvailabilityDate(resultSet.getDate("availability_date").toLocalDate());
      interestDTO.setStatus(resultSet.getString("status"));
      resultSet.close();

      return interestDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Add one interest in the DB.
   *
   * @param item : interestDTO object.
   * @return item.
   */
  @Override
  public InterestDTO addOne(InterestDTO item) {
    Map<String, Object> setters = new HashMap<>();
    setters.put("id_object", item.getIdObject());
    setters.put("id_member", item.getIdMember());
    setters.put("availability_date", item.getAvailabilityDate());
    setters.put("status", item.getStatus());
    List<Class> types = new ArrayList<>();
    types.add(InterestDTO.class);

    try (PreparedStatement preparedStatement = abstractDAO.insertOne(setters, types)) {
      preparedStatement.execute();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return item;
  }

  /**
   * Get a list of interest in an id object.
   *
   * @param idObject the object we want to retrieve the interests
   * @return a list of interest, by an id object
   */
  @Override
  public List<InterestDTO> getAll(int idObject) {

    List<Class> types = new ArrayList<>();
    types.add(InterestDTO.class);
    List<Object> values = new ArrayList<>();
    values.add(idObject);
    values.add("cancelled");
    String condition = "id_object = ? AND status != ?";

    try (PreparedStatement preparedStatement = abstractDAO.getAll(condition, values, types)) {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();

      List<InterestDTO> interestDTOList = new ArrayList<>();
      while (resultSet.next()) {
        InterestDTO interestDTO = interestFactory.getInterestDTO();
        interestDTO.setIdObject(resultSet.getInt("id_object"));
        interestDTO.setIdMember(resultSet.getInt("id_member"));
        interestDTO.setAvailabilityDate(resultSet.getDate("availability_date").toLocalDate());
        interestDTO.setStatus(resultSet.getString("status"));

        interestDTOList.add(interestDTO);
      }
      resultSet.close();
      return interestDTOList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}
