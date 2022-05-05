package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import org.mindrot.jbcrypt.BCrypt;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberImpl implements Member {

  @JsonView(Views.Public.class)
  private Integer memberId;
  @JsonView(Views.Public.class)
  private String username;
  @JsonView(Views.Public.class)
  private String lastname;
  @JsonView(Views.Public.class)
  private String firstname;
  @JsonView(Views.Public.class)
  private String status;
  @JsonView(Views.Public.class)
  private String role;
  @JsonView(Views.Public.class)
  private String phone;
  @JsonView(Views.Public.class)
  private String reasonRefusal;
  @JsonView(Views.Internal.class)
  private String password;
  @JsonView(Views.Public.class)
  private AddressDTO address;
  @JsonView(Views.Public.class)
  private String image;
  @JsonView(Views.Public.class)
  private Integer version;

  @Override
  public Integer getMemberId() {
    return memberId;
  }

  @Override
  public void setMemberId(int memberId) {
    this.memberId = memberId;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String getLastname() {
    return lastname;
  }

  @Override
  public void setLastname(String name) {
    this.lastname = name;
  }

  @Override
  public String getFirstname() {
    return firstname;
  }

  @Override
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String getRole() {
    return role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public String getPhone() {
    return phone;
  }

  @Override
  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override
  public String getReasonRefusal() {
    return reasonRefusal;
  }

  @Override
  public void setReasonRefusal(String reasonRefusal) {
    this.reasonRefusal = reasonRefusal;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public AddressDTO getAddress() {
    return address;
  }

  @Override
  public void setAddress(AddressDTO address) {
    this.address = address;
  }

  @Override
  public String getImage() {
    return this.image;
  }

  @Override
  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public Integer getVersion() {
    return this.version;
  }

  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }

  /**
   * Check the password of the member.
   *
   * @param password : password of the member that need to be checked
   */
  @Override
  public boolean checkPassword(String password) {
    return BCrypt.checkpw(password, this.password);
  }

  /**
   * Hash the password of the member.
   *
   * @param password : password of the member that need to be hashed
   */
  @Override
  public String hashPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

}
