package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.MemberImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = MemberImpl.class)
public interface MemberDTO {

  Integer getMemberId();

  void setMemberId(int memberId);

  String getUsername();

  void setUsername(String username);

  String getLastname();

  void setLastname(String name);

  String getFirstname();

  void setFirstname(String firstname);

  String getStatus();

  void setStatus(String status);

  String getRole();

  void setRole(String role);

  String getPhone();

  void setPhone(String phone);

  String getReasonRefusal();

  void setReasonRefusal(String reasonRefusal);

  String getPassword();

  void setPassword(String password);

  AddressDTO getAddress();

  void setAddress(AddressDTO address);

  String getImage();

  void setImage(String image);

  Integer getVersion();

  void setVersion(Integer version);

}
