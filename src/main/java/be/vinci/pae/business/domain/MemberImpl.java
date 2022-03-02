package be.vinci.pae.business.domain;

import org.mindrot.jbcrypt.BCrypt;

public class MemberImpl implements Member {

  private int memberId;
  private String username;
  private String lastname;
  private String firstname;
  private String status;
  private String role;
  private String phone;
  private int addresse;
  private String reasonRefusal;
  private String password;


  @Override
  public int getMemberId() {
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
  public int getAddresse() {
    return addresse;
  }

  @Override
  public void setAddresse(int addresse) {
    this.addresse = addresse;
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
