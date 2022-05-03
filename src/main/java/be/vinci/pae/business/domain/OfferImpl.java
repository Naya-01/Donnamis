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
  private Integer idObject;
  @JsonView(Views.Public.class)
  private Integer idOffer;
  @JsonView(Views.Public.class)
  private LocalDate date;
  @JsonView(Views.Public.class)
  private LocalDate oldDate;
  @JsonView(Views.Public.class)
  private String timeSlot;
  @JsonView(Views.Public.class)
  private String status;
  @JsonView(Views.Public.class)
  private ObjectDTO object;
  @JsonView(Views.Public.class)
  private Integer version;

  @Override
  public Integer getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

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
  public LocalDate getOldDate() {
    return oldDate;
  }

  @Override
  public void setOldDate(LocalDate oldDate) {
    this.oldDate = oldDate;
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
    this.status = status;
  }

  @Override
  public Integer getVersion() {
    return version;
  }

  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }
}