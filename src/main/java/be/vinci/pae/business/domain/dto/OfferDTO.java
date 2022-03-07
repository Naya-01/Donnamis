package be.vinci.pae.business.domain.dto;

import java.time.LocalDate;
import java.util.Date;

public interface OfferDTO {

  int getIdOffer();

  void setIdOffer(int idOffer);

  LocalDate getDate();

  void setDate(LocalDate date);

  String getTimeSlot();

  void setTimeSlot(String timeSlot);

  Object getObject();

  void setObject(Object object);
}
