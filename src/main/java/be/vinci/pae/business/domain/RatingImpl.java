package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.utils.Views;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RatingImpl implements RatingDTO {

  @JsonView(Views.Public.class)
  private Integer rating;
  @JsonView(Views.Public.class)
  private String comment;
  @JsonView(Views.Public.class)
  private Integer idMember;
  @JsonView(Views.Public.class)
  private MemberDTO memberRater;
  @JsonView(Views.Public.class)
  private Integer idObject;

  @Override
  public Integer getRating() {
    return rating;
  }

  @Override
  public void setRating(int rating) {
    this.rating = rating;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public void setComment(String comment) {
    this.comment = comment;
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
  public MemberDTO getMemberRater() {
    return this.memberRater;
  }

  @Override
  public void setMemberRater(MemberDTO memberRater) {
    this.memberRater = memberRater;
  }

  @Override
  public Integer getIdObject() {
    return idObject;
  }

  @Override
  public void setIdObject(Integer idObject) {
    this.idObject = idObject;
  }
}
