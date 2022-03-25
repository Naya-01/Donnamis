package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.BadRequestException;
import jakarta.inject.Inject;

public class AddressUCCImpl implements AddressUCC {

  @Inject
  private AddressDAO addressDAO;
  @Inject
  private DALService dalService;

  /**
   * Update any attribute of an address.
   *
   * @param addressDTO the address that need to be updated
   * @return the addressDTO modified
   */
  @Override
  public AddressDTO updateOne(AddressDTO addressDTO) {
    AddressDTO addressDTOReturned;
    try {
      dalService.startTransaction();
      addressDTOReturned = addressDAO.updateOne(addressDTO);
      if (addressDTOReturned == null) {
        dalService.rollBackTransaction();
        throw new BadRequestException("Adresse non mise à jour");
      }
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    dalService.commitTransaction();
    return addressDTOReturned;
  }
}
