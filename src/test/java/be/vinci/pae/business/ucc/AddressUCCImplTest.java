package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AddressUCCImplTest {

  private final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());

  private AddressUCC addressUCC;
  private AddressDAO mockAddressDAO;
  private AddressDTO addressDTO;
  private AddressDTO addressDTOUpdated;
  private AddressDTO addressDTOInDB;
  private DALService mockDalService;

  @BeforeEach
  void initAll() {
    this.addressUCC = locator.getService(AddressUCC.class);
    this.mockAddressDAO = locator.getService(AddressDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    AddressFactory addressFactory = locator.getService(AddressFactory.class);
    this.addressDTO = addressFactory.getAddressDTO();
    this.addressDTO.setIdMember(1);
    this.addressDTO.setVersion(1);
    this.addressDTOUpdated = addressFactory.getAddressDTO();
    this.addressDTOUpdated.setIdMember(1);
    this.addressDTOUpdated.setCommune("Brussels");
    this.addressDTOUpdated.setVersion(1);
    this.addressDTOInDB = addressFactory.getAddressDTO();
    this.addressDTOInDB.setIdMember(1);
    this.addressDTOInDB.setVersion(222);

  }

  @DisplayName("test updateOne with an existent address")
  @Test
  public void testUpdateOneWithExistentAddress() {

    Mockito.when(mockAddressDAO.getAddressByMemberId(addressDTOUpdated.getIdMember()))
        .thenReturn(addressDTO);
    Mockito.when(mockAddressDAO.updateOne(addressDTOUpdated)).thenReturn(addressDTOUpdated);
    assertAll(
        () -> assertEquals(addressDTOUpdated, addressUCC.updateOne(addressDTOUpdated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockAddressDAO, Mockito.atLeast(1))
            .updateOne(addressDTOUpdated),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("test updateOne with a non-existent address")
  @Test
  public void testUpdateOneWithNonExistentAddress() {
    Mockito.when(mockAddressDAO.getAddressByMemberId(addressDTO.getIdMember()))
        .thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> addressUCC.updateOne(addressDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockAddressDAO, Mockito.atLeast(1))
            .getAddressByMemberId(addressDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test updateOne with versions that don't match")
  @Test
  public void testUpdateOneWithDifferentsVersions() {
    Mockito.when(mockAddressDAO.getAddressByMemberId(addressDTO.getIdMember()))
        .thenReturn(addressDTOInDB);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> addressUCC.updateOne(addressDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockAddressDAO, Mockito.atLeast(1))
            .getAddressByMemberId(addressDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }


}