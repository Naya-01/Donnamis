package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberDAOImpl implements MemberDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private MemberFactory memberFactory;
  @Inject
  private AddressDAO addressDAO;

  @Inject
  private AbstractDAO abstractDAO;

  /**
   * Get a member we want to retrieve by his username.
   *
   * @param username : the username of the member we want to retrieve
   * @return the member
   */
  @Override
  public <T> MemberDTO getOne(String username) {
    String condition = "username = ?";
    List<Object> values = new ArrayList<>();
    values.add(username);
    ArrayList<Class<T>> types = new ArrayList<>();
    types.add((Class<T>) MemberDTO.class);
    try (PreparedStatement preparedStatement = abstractDAO.getOne(condition, values, types)) {
      MemberDTO memberDTO = getMemberByPreparedStatement(preparedStatement);
      if (memberDTO != null) {
        memberDTO.setAddress(addressDAO.getAddressByMemberId(memberDTO.getMemberId()));
      }
      return memberDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a member we want to retrieve by his id.
   *
   * @param id : the id of the member we want to retrieve
   * @return the member
   */
  public <T> MemberDTO getOne(Integer id) {
    String condition = "id_member = ?";
    List<Object> values = new ArrayList<>();
    values.add(id);
    ArrayList<Class<T>> types = new ArrayList<>();
    types.add((Class<T>) MemberDTO.class);

    try (PreparedStatement preparedStatement = abstractDAO.getOne(condition, values, types)) {
      MemberDTO memberDTO = getMemberByPreparedStatement(preparedStatement);
      if (memberDTO != null) {
        memberDTO.setAddress(addressDAO.getAddressByMemberId(id));
      }
      return memberDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Add a member in the DB and make a memberDTO.
   *
   * @param member : member we want to add in the DB
   * @return the member added.
   */
  @Override
  public <T> MemberDTO createOneMember(MemberDTO member) {
    Map<String, Object> setters = new HashMap<>();
    setters.put("username", member.getUsername());
    setters.put("lastname", member.getLastname());
    setters.put("firstname", member.getFirstname());
    setters.put("status", member.getStatus());
    setters.put("role", member.getRole());
    setters.put("phone_number", member.getPhone());
    setters.put("password", member.getPassword());
    setters.put("refusal_reason", member.getReasonRefusal());
    List<Class<T>> types = new ArrayList<>();
    types.add((Class<T>) MemberDTO.class);
    try (PreparedStatement preparedStatement = abstractDAO.insertOne(setters, types)) {
      return getMemberByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Search a member with status and search on firstname, lastname and username.
   *
   * @param search the search pattern (if empty -> all)
   * @param status the status : waiting -> pending and denied members, pending -> pending members,
   *               denied -> denied members, valid -> valid members, empty ("") -> all members,
   *               other than these -> all members
   * @return a list of MemberDTO
   */
  @Override
  public <T> List<MemberDTO> getAll(String search, String status) {
    List<Class<T>> types = new ArrayList<>();
    types.add((Class<T>) MemberDTO.class);
    types.add((Class<T>) AddressDTO.class);

    List<Object> values = new ArrayList<>();

    String condition = "addresses.id_member = members.id_member ";

    if (status != null && status.equals("waiting")) {
      condition += "AND members.status != 'valid' ";
    } else if (status != null && status.equals("pending")) {
      condition += "AND members.status = 'pending' ";
    } else if (status != null && status.equals("denied")) {
      condition += "AND members.status = 'denied' ";
    } else if (status != null && status.equals("valid")) {
      condition += "AND members.status = 'valid' ";
    }
    if (search != null && !search.isEmpty()) {
      condition += "AND (lower(members.firstname) LIKE ? OR lower(members.lastname) LIKE ? "
          + "OR lower(members.username) LIKE ?)";
      for (int i = 1; i <= 3; i++) {
        values.add("%" + search.toLowerCase() + "%");
      }
    }

    try (PreparedStatement preparedStatement = abstractDAO.getAll(condition, values, types)) {
      return getMemberListByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Update any attribute of a member.
   *
   * @param memberDTO a memberDTO
   * @return the modified member
   */
  @Override
  public <T> MemberDTO updateOne(MemberDTO memberDTO) {

    Map<String, Object> toUpdate = new HashMap<>();
    List<Class<T>> types = new ArrayList<>();
    types.add((Class<T>) MemberDTO.class);

    List<Object> conditionValues = new ArrayList<>();
    conditionValues.add(memberDTO.getMemberId());

    String condition = "id_member = ? RETURNING id_member,username, lastname, firstname, status, "
        + "role, phone_number, password, refusal_reason, image";

    if (memberDTO.getUsername() != null && !memberDTO.getUsername().isBlank()) {
      toUpdate.put("username", memberDTO.getUsername());
    }
    if (memberDTO.getLastname() != null && !memberDTO.getLastname().isBlank()) {
      toUpdate.put("lastname", memberDTO.getLastname());
    }
    if (memberDTO.getFirstname() != null && !memberDTO.getFirstname().isBlank()) {
      toUpdate.put("firstname", memberDTO.getFirstname());
    }
    if (memberDTO.getStatus() != null && !memberDTO.getStatus().isBlank()) {
      toUpdate.put("status", memberDTO.getStatus());
    }
    if (memberDTO.getRole() != null && !memberDTO.getRole().isBlank()) {
      toUpdate.put("role", memberDTO.getRole());
    }

    toUpdate.put("phone_number", memberDTO.getPhone());

    if (memberDTO.getReasonRefusal() != null && !memberDTO.getReasonRefusal().isBlank()) {
      toUpdate.put("refusal_reason", memberDTO.getReasonRefusal());
    }
    if (memberDTO.getPassword() != null && !memberDTO.getPassword().isBlank()) {
      Member member = (Member) memberDTO;
      toUpdate.put("password", member.hashPassword(member.getPassword()));
    }

    try (PreparedStatement preparedStatement = abstractDAO.updateOne(toUpdate, condition,
        conditionValues, types)) {
      MemberDTO modifiedMember = getMemberByPreparedStatement(preparedStatement);
      return modifiedMember;
    } catch (SQLException e) {
      throw new FatalException(e);
    }

  }

  /**
   * Update the profil picture of the member.
   *
   * @param path of the picture
   * @param id   of the member
   * @return memberDTO updated
   */
  @Override
  public MemberDTO updateProfilPicture(String path, int id) {
    String query = "UPDATE donnamis.members SET image=? WHERE id_member=?";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, path);
      preparedStatement.setInt(2, id);
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOne(id);
  }

  /**
   * Make a memberDTO with the result set.
   *
   * @param preparedStatement : a prepared statement to execute the query with these attributes :
   *                          m.id_member, m.username, m.lastname, m.firstname, m.status, m.role,
   *                          m.phone_number, m.password, m.refusal_reason, m.image, a.id_member,
   *                          a.unit_number, a.building_number, a.street, a.postcode, a.commune
   * @return a list of member.
   */
  private List<MemberDTO> getMemberListByPreparedStatement(PreparedStatement preparedStatement) {
    List<MemberDTO> memberDTOList = new ArrayList<>();
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      while (resultSet.next()) {
        MemberDTO memberDTO = getMemberByResultSet(resultSet);

        AddressDTO addressDTO = addressDAO.getAddress(resultSet.getInt(11),
            resultSet.getString(12), resultSet.getString(13),
            resultSet.getString(14), resultSet.getString(15),
            resultSet.getString(16));
        memberDTO.setAddress(addressDTO);

        memberDTOList.add(memberDTO);
      }
      resultSet.close();
      return memberDTOList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private MemberDTO getMember(int memberId, String username, String lastName, String firstname,
      String status, String role, String phone, String password, String reasonRefusal,
      String image) {
    if (image != null) {
      image = Config.getProperty("ImagePath") + image;
    }
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(memberId);
    memberDTO.setUsername(username);
    memberDTO.setLastname(lastName);
    memberDTO.setFirstname(firstname);
    memberDTO.setStatus(status);
    memberDTO.setRole(role);
    memberDTO.setPhone(phone);
    memberDTO.setPassword(password);
    memberDTO.setReasonRefusal(reasonRefusal);
    memberDTO.setImage(image);
    return memberDTO;
  }

  /**
   * Make a memberDTO with the result set.
   *
   * @param preparedStatement : a prepared statement to execute the query with these attributes :
   *                          id_member, username, lastname, firstname, status, role, phone_number,
   *                          password, refusal_reason, image
   * @return a memberDTO
   */
  private MemberDTO getMemberByPreparedStatement(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      MemberDTO memberDTO = getMemberByResultSet(resultSet);
      resultSet.close();
      return memberDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get member with a result set.
   *
   * @param resultSet a resultSet to execute the query with these attributes : id_member, username,
   *                  lastname, firstname, status, role, phone_number, password, refusal_reason,
   *                  image
   * @return a memberDTO
   */
  private MemberDTO getMemberByResultSet(ResultSet resultSet) {
    try {
      return getMember(resultSet.getInt(1), resultSet.getString(2),
          resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
          resultSet.getString(6), resultSet.getString(7), resultSet.getString(8),
          resultSet.getString(9), resultSet.getString(10));
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}