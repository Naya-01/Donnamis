package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.RatingImpl;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = RatingImpl.class)
public interface RatingDTO {

  Integer getRating();

  void setRating(int rating);

  String getComment();

  void setComment(String comment);

  Integer getIdMember();

  void setIdMember(Integer memberId);

  MemberDTO getMemberRater();

  void setMemberRater(MemberDTO memberRater);

  Integer getIdObject();

  void setIdObject(Integer objectId);
}
