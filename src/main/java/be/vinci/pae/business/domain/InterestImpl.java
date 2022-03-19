package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.InterestDTO;
import java.time.LocalDate;

public class InterestImpl implements InterestDTO {

  private int idObject;
  private int idMember;
  private LocalDate availabilityDate;
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
