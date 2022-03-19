package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import java.time.LocalDate;

public class OfferImpl implements OfferDTO {

  private int idOffer;
  private LocalDate date;
  private String timeSlot;
  private ObjectDTO object;

  @Override
  public int getIdOffer() {
    return idOffer;
  }

  @Override
  public void setIdOffer(int idOffer) {
    this.idOffer = idOffer;
  }

  @Override
  public LocalDate getDate() {
    return date;
  }

  @Override
  public void setDate(LocalDate date) {
    this.date = date;
  }

  @Override
  public String getTimeSlot() {
    return timeSlot;
  }

  @Override
  public void setTimeSlot(String timeSlot) {
    this.timeSlot = timeSlot;
  }

  @Override
  public ObjectDTO getObject() {
    return object;
  }

  @Override
  public void setObject(ObjectDTO object) {
    this.object = object;
  }

}