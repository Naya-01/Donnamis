package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.OfferImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;

@JsonDeserialize(as = OfferImpl.class)
public interface OfferDTO {

  Integer getIdObject();

  void setIdObject(int idObject);

  Integer getIdOffer();

  void setIdOffer(int idOffer);

  LocalDate getDate();

  void setDate(LocalDate date);

  LocalDate getOldDate();

  void setOldDate(LocalDate oldDate);

  String getTimeSlot();

  void setTimeSlot(String timeSlot);

  ObjectDTO getObject();

  void setObject(ObjectDTO object);

  String getStatus();

  void setStatus(String status);

  Integer getVersion();

  void setVersion(Integer version);
}
