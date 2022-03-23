package be.vinci.pae.business.domain.dto;

import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;

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
