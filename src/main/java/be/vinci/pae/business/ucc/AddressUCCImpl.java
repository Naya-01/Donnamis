package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
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
      AddressDTO addressDTOInDB = addressDAO.getAddressByMemberId(addressDTO.getIdMember());
      if (addressDTOInDB == null) {
        throw new NotFoundException();
      }
      if (addressDTO.getVersion() == null
          || !addressDTO.getVersion().equals(addressDTOInDB.getVersion())) {
        throw new ForbiddenException("Vous ne possédez pas une version à jour d'adresse.");
      }
      addressDTOReturned = addressDAO.updateOne(addressDTO);
      dalService.commitTransaction();
    } catch (Exception e) {
      dalService.rollBackTransaction();
      throw e;
    }
    return addressDTOReturned;
  }
}
