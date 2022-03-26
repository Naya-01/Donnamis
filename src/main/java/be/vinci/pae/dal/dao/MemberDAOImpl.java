package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private MemberFactory memberFactory;
  @Inject
  private AddressDAO addressDAO;

  /**
   * Get a member we want to retrieve by his username.
   *
   * @param username : the username of the member we want to retrieve
   * @return the member
   */
  @Override
  public MemberDTO getOne(String username) {
    String query = "SELECT id_member, username, lastname, firstname, status, role, phone_number, "
        + "password, refusal_reason, image FROM donnamis.members WHERE username = ?";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      preparedStatement.setString(1, username);
      List<MemberDTO> memberDTOList = getMemberList(preparedStatement, false);
      if (memberDTOList.size() != 1) {
        return null;
      }
      return memberDTOList.get(0);
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
  public MemberDTO getOne(int id) {
    String query = "SELECT id_member, username, lastname, firstname, status, role, phone_number, "
        + "password, refusal_reason, image FROM donnamis.members WHERE id_member = ?";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      List<MemberDTO> memberDTOList = getMemberList(preparedStatement, false);
      if (memberDTOList.size() != 1) {
        return null;
      }
      return memberDTOList.get(0);
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
  public MemberDTO createOneMember(MemberDTO member) {
    String query = "INSERT INTO donnamis.members (username, lastname, firstname, status, role, "
        + "phone_number, password, refusal_reason,image) values (?,?,?,?,?,?,?,?,?) "
        + "RETURNING id_member, username, lastname, firstname, status, role, phone_number, "
        + "password, refusal_reason, image";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, member.getUsername());
      preparedStatement.setString(2, member.getLastname());
      preparedStatement.setString(3, member.getFirstname());
      preparedStatement.setString(4, member.getStatus());
      preparedStatement.setString(5, member.getRole());
      preparedStatement.setString(6, member.getPhone());
      preparedStatement.setString(7, member.getPassword());
      preparedStatement.setString(8, member.getReasonRefusal());
      preparedStatement.setString(9, member.getImage());

      return getMemberList(preparedStatement, false).get(0);
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
  public List<MemberDTO> getAll(String search, String status) {
    String query =
        "SELECT m.id_member, m.username, m.lastname, m.firstname, m.status, m.role, "
            + "m.phone_number, m.password, m.refusal_reason, m.image, a.id_member, a.unit_number, "
            + "a.building_number, a.street, a.postcode, a.commune, a.country "
            + "FROM donnamis.members m, donnamis.addresses a "
            + "WHERE a.id_member = m.id_member ";

    if (status != null && status.equals("waiting")) {
      query += "AND m.status != 'valid' ";
    } else if (status != null && status.equals("pending")) {
      query += "AND m.status = 'pending' ";
    } else if (status != null && status.equals("denied")) {
      query += "AND m.status = 'denied' ";
    } else if (status != null && status.equals("valid")) {
      query += "AND m.status = 'valid' ";
    }
    if (search != null && !search.isEmpty()) {
      query += "AND (lower(m.firstname) LIKE ? OR lower(m.lastname) LIKE ? "
          + "OR lower(m.username) LIKE ?)";
    }
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      if (search != null && !search.isEmpty()) {
        for (int i = 1; i <= 3; i++) {
          preparedStatement.setString(i, "%" + search.toLowerCase() + "%");
        }
      }
      return getMemberList(preparedStatement, true);
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
  public MemberDTO updateOne(MemberDTO memberDTO) {
    Deque<String> memberDTODeque = new ArrayDeque<>();
    String query = "UPDATE donnamis.members SET ";
    if (memberDTO.getUsername() != null && !memberDTO.getUsername().isEmpty()) {
      query += "username = ?,";
      memberDTODeque.addLast(memberDTO.getUsername());
    }
    if (memberDTO.getLastname() != null && !memberDTO.getLastname().isEmpty()) {
      query += "lastname = ?,";
      memberDTODeque.addLast(memberDTO.getLastname());
    }
    if (memberDTO.getFirstname() != null && !memberDTO.getFirstname().isEmpty()) {
      query += "firstname = ?,";
      memberDTODeque.addLast(memberDTO.getFirstname());
    }
    if (memberDTO.getStatus() != null && !memberDTO.getStatus().isEmpty()) {
      query += "status = ?,";
      memberDTODeque.addLast(memberDTO.getStatus());
    }
    if (memberDTO.getRole() != null && !memberDTO.getRole().isEmpty()) {
      query += "role = ?,";
      memberDTODeque.addLast(memberDTO.getRole());
    }
    if (memberDTO.getPhone() != null && !memberDTO.getPhone().isEmpty()) {
      query += "phone_number = ?,";
      memberDTODeque.addLast(memberDTO.getPhone());
    }
    if (memberDTO.getReasonRefusal() != null && !memberDTO.getReasonRefusal().isEmpty()) {
      query += "refusal_reason = ?,";
      memberDTODeque.addLast(memberDTO.getReasonRefusal());
    }
    if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
      query += "password = ?,";
      memberDTODeque.addLast(memberDTO.getPassword());
    }
    if (memberDTO.getImage() != null && !memberDTO.getImage().isEmpty()) {
      query += "image = ?,";
      memberDTODeque.addLast(memberDTO.getImage());
    }

    query = query.substring(0, query.length() - 1);
    if (query.endsWith("SET")) {
      return null;
    }
    query += " WHERE id_member = ? RETURNING id_member,username, lastname, firstname, status, "
        + "role, phone_number, password, refusal_reason, image";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {

      int cnt = 1;
      for (String str : memberDTODeque) {
        preparedStatement.setString(cnt++, str);
      }
      preparedStatement.setInt(cnt, memberDTO.getMemberId());

      return getMemberList(preparedStatement, false).get(0);
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
   *                          id_member, username, lastname, firstname, status, role, phone_number,
   *                          password, refusal_reason, image
   * @return the member.
   */
  private List<MemberDTO> getMemberList(PreparedStatement preparedStatement, boolean hasAdress) {
    List<MemberDTO> memberDTOList = new ArrayList<>();
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      while (resultSet.next()) {
        MemberDTO memberDTO = getMember(resultSet.getInt(1), resultSet.getString(2),
            resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
            resultSet.getString(6), resultSet.getString(7), resultSet.getString(8),
            resultSet.getString(9), resultSet.getString(10));

        if (hasAdress) {
          AddressDTO addressDTO = addressDAO.getAddress(resultSet.getInt(11),
              resultSet.getString(12), resultSet.getString(13),
              resultSet.getString(14), resultSet.getString(15),
              resultSet.getString(16), resultSet.getString(17));
          memberDTO.setAddress(addressDTO);
        }
        memberDTOList.add(memberDTO);
      }
      return memberDTOList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Create a MemberDTO ith all attributes.
   *
   * @param memberId      the member id
   * @param username      the username
   * @param lastName      the lastname
   * @param firstname     the firstname
   * @param status        the status
   * @param role          the role
   * @param phone         the phone
   * @param password      the password
   * @param reasonRefusal the refusal reason
   * @param image         the image
   * @return a memberDTO
   */
  private MemberDTO getMember(int memberId, String username, String lastName, String firstname,
      String status, String role, String phone, String password, String reasonRefusal,
      String image) {

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
}