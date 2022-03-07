package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.OfferDTO;
import java.util.List;

public interface OfferUCC {
  List<OfferDTO> getAllPosts(String searchPattern);
}
