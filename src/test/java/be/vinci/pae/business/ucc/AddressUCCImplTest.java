package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.*;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AddressUCCImplTest {
  private AddressUCC addressUCC;
  private AddressDAO mockAddressDAO;
  private AddressDTO addressDTO;
  private DALService mockDalService;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.addressUCC = locator.getService(AddressUCC.class);
    this.mockAddressDAO = locator.getService(AddressDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    AddressFactory addressFactory = locator.getService(AddressFactory.class);
    this.addressDTO = addressFactory.getAddressDTO();
    this.addressDTO.setIdMember(1);
    this.addressDTO.setCountry("France");
  }

  @DisplayName("test updateOne with an existent address")
  @Test
  public void testUpdateOneWithExistentAddress() {
    Mockito.when(mockAddressDAO.updateOne(addressDTO)).thenReturn(addressDTO);
    assertAll(
        () -> assertEquals(addressDTO, addressUCC.updateOne(addressDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockAddressDAO, Mockito.atLeast(1))
            .updateOne(addressDTO),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("test updateOne with a non-existent address")
  @Test
  public void testUpdateOneWithNonExistentAddress() {
    Mockito.when(mockAddressDAO.updateOne(addressDTO)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> addressUCC.updateOne(addressDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockAddressDAO, Mockito.atLeast(1))
            .updateOne(addressDTO),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }



}