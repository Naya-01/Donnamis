package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class InterestImpl implements InterestDTO {

  @JsonView(Views.Public.class)
  private MemberDTO member;
  @JsonView(Views.Public.class)
  private ObjectDTO object;
  @JsonView(Views.Public.class)
  private OfferDTO offer;
  @JsonView(Views.Public.class)
  private Integer idMember;
  @JsonView(Views.Public.class)
  private Integer idObject;
  @JsonView(Views.Public.class)
  private LocalDate availabilityDate;
  @JsonView(Views.Public.class)
  private String status;
  @JsonView(Views.Public.class)
  private Boolean isNotificated;
  @JsonView(Views.Public.class)
  private LocalDate notificationDate;
  @JsonView(Views.Public.class)
  private Integer version;
  @JsonView(Views.Public.class)
  private Boolean isCalled;

  @Override
  public MemberDTO getMember() {
    return member;
  }

  @Override
  public void setMember(MemberDTO member) {
    this.member = member;
  }

  @Override
  public LocalDate getNotificationDate() {
    return notificationDate;
  }

  @Override
  public void setNotificationDate(LocalDate notificationDate) {
    this.notificationDate = notificationDate;
  }

  @Override
  public Boolean getIsNotificated() {
    return isNotificated;
  }

  @Override
  public void setIsNotificated(boolean isNotificated) {
    this.isNotificated = isNotificated;
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
  public Integer getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(int idObject) {
    this.idObject = idObject;
  }

  @Override
  public Integer getIdMember() {
    return idMember;
  }

  @Override
  public void setIdMember(Integer idMember) {
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

  @Override
  public Integer getVersion() {
    return version;
  }

  @Override
  public void setVersion(Integer version) {
    this.version = version;
  }

  @Override
  public Boolean getIsCalled() {
    return isCalled;
  }

  @Override
  public void setIsCalled(boolean isCalled) {
    this.isCalled = isCalled;
  }

  @Override
  public OfferDTO getOffer() {
    return offer;
  }

  @Override
  public void setOffer(OfferDTO offer) {
    this.offer = offer;
  }
}
