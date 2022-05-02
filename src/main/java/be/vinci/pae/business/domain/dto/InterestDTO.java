package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.InterestImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;

@JsonDeserialize(as = InterestImpl.class)
public interface InterestDTO {

  MemberDTO getMember();

  void setMember(MemberDTO member);

  LocalDate getNotificationDate();

  void setNotificationDate(LocalDate notificationDate);

  Boolean getIsNotificated();

  void setIsNotificated(boolean isNotificated);

  ObjectDTO getObject();

  void setObject(ObjectDTO object);

  Integer getIdObject();

  void setIdObject(int idObject);

  Integer getIdMember();

  void setIdMember(Integer idMember);

  LocalDate getAvailabilityDate();

  void setAvailabilityDate(LocalDate availabilityDate);

  String getStatus();

  void setStatus(String status);

  Integer getVersion();

  void setVersion(Integer version);

  Boolean getIsCalled();

  void setIsCalled(boolean isCalled);

  OfferDTO getOffer();

  void setOffer(OfferDTO offer);
}
