package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {

  @Inject
  private DALBackendService dalBackendService;
  @Inject
  private MemberFactory memberFactory;
  @Inject
  private AddressFactory addressFactory;

  /**
   * Get a member we want to retrieve by his username.
   *
   * @param username : the username of the member we want to retrieve
   * @return the member
   */
  @Override
  public MemberDTO getOne(String username) {
    String query = "SELECT m.id_member, m.username, m.lastname, m.firstname, m.status, m.role, "
        + "m.phone_number, m.password, m.refusal_reason, m.image, m.version AS version "
        + ", a.id_member, a.unit_number, a.building_number, a.street, a.postcode, a.commune, "
        + "a.version FROM donnamis.members m, donnamis.addresses a WHERE m.username = ? AND "
        + "m.id_member = a.id_member ";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, username);
      return getMemberDTOWithAddressDTO(preparedStatement);
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
  public MemberDTO getOne(Integer id) {
    String query = "SELECT m.id_member, m.username, m.lastname, m.firstname, m.status, m.role, "
        + "m.phone_number, m.password, m.refusal_reason, m.image, m.version AS version "
        + ", a.id_member, a.unit_number, a.building_number, a.street, a.postcode, a.commune, "
        + "a.version FROM donnamis.members m, donnamis.addresses a WHERE m.id_member = ? AND "
        + "m.id_member = a.id_member ";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setInt(1, id);
      return getMemberDTOWithAddressDTO(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get a member with his address on base of the preparedStatement.
   *
   * @param preparedStatement ps that contains all information of member and address
   * @return the memberDTO that contains his address
   */
  private MemberDTO getMemberDTOWithAddressDTO(PreparedStatement preparedStatement) {
    MemberDTO memberDTO;
    try {
      memberDTO = getMemberByPreparedStatement(preparedStatement);
      ResultSet resultSet = preparedStatement.getResultSet();
      if (memberDTO != null) {
        AddressDTO addressDTO = getAddressDTOByResultSet(resultSet);
        memberDTO.setAddress(addressDTO);
      }
      preparedStatement.close();
      resultSet.close();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return memberDTO;
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
        + "phone_number, password, refusal_reason,image, version) values (?,?,?,?,?,?,?,?,?,1) "
        + "RETURNING id_member, username, lastname, firstname, status, role, phone_number, "
        + "password, refusal_reason, image, version";

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
      MemberDTO memberDTO = getMemberByPreparedStatement(preparedStatement);
      preparedStatement.getResultSet().close();
      return memberDTO;
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
            + "m.phone_number, m.password, m.refusal_reason, m.image, m.version as version, "
            + "a.id_member, a.unit_number, a.building_number, a.street, a.postcode, a.commune,"
            + " a.version FROM donnamis.members m, donnamis.addresses a "
            + "WHERE a.id_member = m.id_member ";

    if (status != null && status.equals("waiting")) {
      query += "AND (m.status = 'pending' OR m.status = 'denied') ";
    } else if (status != null && status.equals("pending")) {
      query += "AND m.status = 'pending' ";
    } else if (status != null && status.equals("denied")) {
      query += "AND m.status = 'denied' ";
    } else if (status != null && status.equals("valid")) {
      query += "AND (m.status = 'valid' OR m.status = 'prevented') ";
    }
    if (search != null && !search.isEmpty()) {
      query += "AND (lower(a.postcode) LIKE ? OR lower(a.commune) LIKE ? "
          + "OR lower(m.username) LIKE ? OR lower(m.lastname) LIKE ?)";
    }
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      if (search != null && !search.isEmpty()) {
        for (int i = 1; i <= 4; i++) {
          preparedStatement.setString(i, "%" + search.toLowerCase() + "%");
        }
      }
      return getMemberListByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Update one or many attribute(s) of a member.
   *
   * @param memberDTO a memberDTO
   * @return the updated member
   */
  @Override
  public MemberDTO updateOne(MemberDTO memberDTO) {
    LinkedList<String> memberDTOList = new LinkedList<>();
    String query = "UPDATE donnamis.members SET version=version+1, ";
    if (memberDTO.getUsername() != null && !memberDTO.getUsername().isBlank()) {
      query += "username = ?,";
      memberDTOList.addLast(memberDTO.getUsername());
    }
    if (memberDTO.getLastname() != null && !memberDTO.getLastname().isBlank()) {
      query += "lastname = ?,";
      memberDTOList.addLast(memberDTO.getLastname());
    }
    if (memberDTO.getFirstname() != null && !memberDTO.getFirstname().isBlank()) {
      query += "firstname = ?,";
      memberDTOList.addLast(memberDTO.getFirstname());
    }
    if (memberDTO.getStatus() != null && !memberDTO.getStatus().isBlank()) {
      query += "status = ?,";
      memberDTOList.addLast(memberDTO.getStatus());
    }
    if (memberDTO.getRole() != null && !memberDTO.getRole().isBlank()) {
      query += "role = ?,";
      memberDTOList.addLast(memberDTO.getRole());
    }
    if (memberDTO.getPhone() != null && !memberDTO.getPhone().isBlank()) {
      query += "phone_number = ?,";
      memberDTOList.addLast(memberDTO.getPhone());
    }
    if (memberDTO.getReasonRefusal() != null && !memberDTO.getReasonRefusal().isBlank()) {
      query += "refusal_reason = ?,";
      memberDTOList.addLast(memberDTO.getReasonRefusal());
    }
    if (memberDTO.getPassword() != null && !memberDTO.getPassword().isBlank()) {
      query += "password = ?,";
      Member member = (Member) memberDTO;
      memberDTOList.addLast(member.hashPassword(member.getPassword()));
    }
    if (memberDTO.getImage() != null && !memberDTO.getImage().isBlank()) {
      query += "image = ?,";
      memberDTOList.addLast(memberDTO.getImage());
    }

    query = query.substring(0, query.length() - 1);
    if (query.endsWith("SET")) {
      return null;
    }
    query += " WHERE id_member = ? RETURNING id_member, username, lastname, firstname, status, "
        + "role, phone_number, password, refusal_reason, image, version ";

    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      int cnt = 1;
      for (String str : memberDTOList) {
        preparedStatement.setString(cnt++, str);
      }
      preparedStatement.setInt(cnt, memberDTO.getMemberId());
      MemberDTO modifiedMember = getMemberByPreparedStatement(preparedStatement);
      preparedStatement.getResultSet().close();
      preparedStatement.close();
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
    String query = "UPDATE donnamis.members SET image=?, version=version+1 WHERE id_member=? "
        + "RETURNING id_member, username, lastname, firstname, status, "
        + "role, phone_number, password, refusal_reason, image, version ";
    try (PreparedStatement preparedStatement = dalBackendService.getPreparedStatement(query)) {
      preparedStatement.setString(1, path);
      preparedStatement.setInt(2, id);
      return getMemberByPreparedStatement(preparedStatement);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Make a memberDTO with the result set.
   *
   * @param preparedStatement : a prepared statement that contains all the info
   * @return a list of member.
   */
  private List<MemberDTO> getMemberListByPreparedStatement(PreparedStatement preparedStatement) {
    List<MemberDTO> memberDTOList = new ArrayList<>();
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      while (resultSet.next()) {
        MemberDTO memberDTO = getMemberByResultSet(resultSet);

        AddressDTO addressDTO = getAddressDTOByResultSet(resultSet);
        memberDTO.setAddress(addressDTO);

        memberDTO.setAddress(addressDTO);
        memberDTOList.add(memberDTO);
      }
      preparedStatement.close();
      resultSet.close();
      return memberDTOList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Create an address from a result set.
   *
   * @param resultSet rs containing all info of an address
   * @return the address
   */
  private AddressDTO getAddressDTOByResultSet(ResultSet resultSet) {
    AddressDTO addressDTO = addressFactory.getAddressDTO();
    try {
      addressDTO.setIdMember(resultSet.getInt(12));
      addressDTO.setUnitNumber(resultSet.getString(13));
      addressDTO.setBuildingNumber(resultSet.getString(14));
      addressDTO.setStreet(resultSet.getString(15));
      addressDTO.setPostcode(resultSet.getString(16));
      addressDTO.setCommune(resultSet.getString(17));
      addressDTO.setVersion(resultSet.getInt(18));
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return addressDTO;
  }

  /**
   * Get a memberDTO with the prepared statement.
   *
   * @param preparedStatement : a prepared statement that contains all info
   * @return a memberDTO
   */
  private MemberDTO getMemberByPreparedStatement(PreparedStatement preparedStatement) {
    try {
      preparedStatement.executeQuery();
      ResultSet resultSet = preparedStatement.getResultSet();
      if (!resultSet.next()) {
        return null;
      }
      return getMemberByResultSet(resultSet);
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
      String image = resultSet.getString(10);
      if (image != null) {
        image = Config.getProperty("ImagePath") + image;
      }
      MemberDTO memberDTO = memberFactory.getMemberDTO();
      memberDTO.setMemberId(resultSet.getInt(1));
      memberDTO.setUsername(resultSet.getString(2));
      memberDTO.setLastname(resultSet.getString(3));
      memberDTO.setFirstname(resultSet.getString(4));
      memberDTO.setStatus(resultSet.getString(5));
      memberDTO.setRole(resultSet.getString(6));
      memberDTO.setPhone(resultSet.getString(7));
      memberDTO.setPassword(resultSet.getString(8));
      memberDTO.setReasonRefusal(resultSet.getString(9));
      memberDTO.setImage(image);
      memberDTO.setVersion(resultSet.getInt(11));
      return memberDTO;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }
}