package be.vinci.pae.business.domain;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;

public class RatingImpl implements RatingDTO {
  private int rating;
  private String comment;
  private MemberDTO memberDTO;
  private ObjectDTO objectDTO;

  @Override
  public int getRating() {
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
  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  @Override
  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  @Override
  public ObjectDTO getObjectDTO() {
    return objectDTO;
  }

  @Override
  public void setObjectDTO(ObjectDTO objectDTO) {
    this.objectDTO = objectDTO;
  }
}
