package be.vinci.pae.business.domain.dto;

public interface MemberDTO {

  int getMemberId();

  void setMemberId(int memberId);

  String getPseudo();

  void setPseudo(String pseudo);

  String getName();

  void setName(String name);

  String getFirstname();

  void setFirstname(String firstname);

  String getStatus();

  void setStatus(String status);

  String getRole();

  void setRole(String role);

  String getPhone();

  void setPhone(String phone);

  int getAddresse();

  void setAddresse(int addresse);

  String getReasonRefusal();

  void setReasonRefusal(String reasonRefusal);

  String getPassword();

  void setPassword(String password);


}
