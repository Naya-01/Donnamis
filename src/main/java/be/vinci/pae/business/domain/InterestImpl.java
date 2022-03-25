package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterestImpl implements InterestDTO {
  @JsonView(Views.Public.class)
  private int idObject;
  @JsonView(Views.Public.class)
  private int idMember;
  @JsonView(Views.Public.class)
  private LocalDate availabilityDate;
  @JsonView(Views.Public.class)
  private String status;

  @Override
  public int getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

  @Override
  public int getIdMember() {
    return idMember;
  }

  @Override
  public void setIdMember(int idMember) {
    this.idMember = idMember;
  }

  @Override
  public LocalDate getAvailabilityDate() {
    return availabilityDate;
  }

  @Override
  public void setAvailabilityDate(LocalDate availabilityDate) {
    this.availabilityDate = availabilityDate;
  }

  @Override
  public String getStatus() {
    return status;
  }

  @Override
  public void setStatus(String status) {
    this.status = status;
  }
}
