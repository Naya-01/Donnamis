package be.vinci.pae.business.domain.dto;

public interface RatingDTO {

  int getRating();

  void setRating(int rating);

  String getComment();

  void setComment(String comment);

  MemberDTO getMemberDTO();

  void setMemberDTO(MemberDTO memberDTO);

  ObjectDTO getObjectDTO();

  void setObjectDTO(ObjectDTO objectDTO);
}
