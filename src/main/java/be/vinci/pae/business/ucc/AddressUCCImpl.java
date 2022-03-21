package be.vinci.pae.business.ucc;

import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.services.DALService;
import jakarta.inject.Inject;

public class AddressUCCImpl implements AddressUCC {

  @Inject
  private AddressDAO addressDAO;
  @Inject
  private DALService dalService;

  /**
   * Update one address.
   *
   * @param idMember       : the id of the member that have this address
   * @param unitNumber     : the unit number
   * @param buildingNumber : the building number
   * @param street         : the name of the street
   * @param postcode       : the postcode
   * @param commune        : the name of the commune
   * @param country        : the name of the country
   * @return the updated address
   */
  @Override
  public AddressDTO updateOne(int idMember, String unitNumber, String buildingNumber, String street,
      String postcode, String commune, String country) {
    dalService.startTransaction();
    AddressDTO addressDTO = addressDAO.updateOne(idMember, unitNumber, buildingNumber, street,
        postcode, commune, country);
    if (addressDTO == null) {
      dalService.rollBackTransaction();
      throw new NotFoundException("Adresse non mise à jour"); //TODO : changer cette exception
    }
    dalService.commitTransaction();
    return addressDTO;
  }
}
