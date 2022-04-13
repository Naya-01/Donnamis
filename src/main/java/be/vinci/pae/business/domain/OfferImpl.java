package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OfferImpl implements OfferDTO {

  @JsonView(Views.Public.class)
  private Integer idOffer;
  @JsonView(Views.Public.class)
  private LocalDate date;
  @JsonView(Views.Public.class)
  private String timeSlot;
  @JsonView(Views.Public.class)
  private String status;
  @JsonView(Views.Public.class)
  private ObjectDTO object;

  @Override
  public Integer getIdOffer() {
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

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    this.status=status;
  }

}