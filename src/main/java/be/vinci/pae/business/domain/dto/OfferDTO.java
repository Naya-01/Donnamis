package be.vinci.pae.business.domain.dto;

import java.time.LocalDate;

public interface OfferDTO {

  int getIdOffer();

  void setIdOffer(int idOffer);

  LocalDate getDate();

  void setDate(LocalDate date);

  String getTimeSlot();

  void setTimeSlot(String timeSlot);

  ObjectDTO getObject();

  void setObject(ObjectDTO object);
}
