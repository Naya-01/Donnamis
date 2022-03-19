package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.InterestImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;

@JsonDeserialize(as = InterestImpl.class)
public interface InterestDTO {

  int getIdObject();

  void setIdObject(int idObject);

  int getIdMember();

  void setIdMember(int idMember);

  LocalDate getAvailabilityDate();

  void setAvailabilityDate(LocalDate availabilityDate);

  String getStatus();

  void setStatus(String status);
}
