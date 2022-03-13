package be.vinci.pae.dal.dao;

import be.vinci.pae.business.domain.dto.OfferDTO;
import java.util.List;

public interface OfferDAO {

  List<OfferDTO> getAll(String searchPattern);
}
