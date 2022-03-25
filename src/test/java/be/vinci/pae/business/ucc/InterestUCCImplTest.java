package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import java.time.LocalDate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class InterestUCCImplTest {

  private InterestUCC interestUCC;
  private InterestDAO mockInterestDAO;
  private DALService mockDalService;
  private InterestDTO mockInterestDTO;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.interestUCC = locator.getService(InterestUCC.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    this.mockInterestDTO = locator.getService(InterestDTO.class);
  }

  @DisplayName("test getInterest with a non existent object and an existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndExistentMember() {
    Mockito.when(mockInterestDAO.getOne(1000, 1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> interestUCC.getInterest(1000, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object and a non existent member")
  @Test
  public void testGetInterestWithExistentObjectAndNonExistentMember() {
    Mockito.when(mockInterestDAO.getOne(1, 1000)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> interestUCC.getInterest(1, 1000)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with a non existent object and a non existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndNonExistentMember() {
    Mockito.when(mockInterestDAO.getOne(1000, 1000)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> interestUCC.getInterest(1000, 1000)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndExistingInterest() {
    Mockito.when(mockInterestDTO.getIdObject()).thenReturn(10);
    Mockito.when(mockInterestDTO.getIdMember()).thenReturn(1);
    Mockito.when(mockInterestDTO.getAvailabilityDate()).thenReturn(LocalDate.now());
    Mockito.when(mockInterestDTO.getStatus()).thenReturn("published");
    Mockito.when(mockInterestDAO.getOne(10, 1)).thenReturn(mockInterestDTO);
    assertAll(
        () -> assertEquals(mockInterestDTO, interestUCC.getInterest(10, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and non-existent interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndNonExistentInterest() {
    Mockito.when(mockInterestDTO.getIdObject()).thenReturn(10);
    Mockito.when(mockInterestDTO.getIdMember()).thenReturn(1);
    Mockito.when(mockInterestDTO.getAvailabilityDate()).thenReturn(LocalDate.now());
    Mockito.when(mockInterestDTO.getStatus()).thenReturn("published");
    Mockito.when(mockInterestDAO.getOne(10, 1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.getInterest(10, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

}