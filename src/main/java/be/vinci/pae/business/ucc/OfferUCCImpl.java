package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.OfferDAO;
import jakarta.inject.Inject;
import java.util.List;

public class OfferUCCImpl implements OfferUCC {

  @Inject
  private OfferDAO offerDAO;

  @Override
  public List<OfferDTO> getAllPosts(String searchPattern) {
    List<OfferDTO> offers = offerDAO.getAll(searchPattern);
    if (offers.isEmpty()) {
      throw new NotFoundException("Aucune offre");
    }
    return offers;
  }
}
