package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import java.time.LocalDate;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class InterestUCCImplTest {

  private InterestUCC interestUCC;
  private InterestDAO mockInterestDAO;
  private ObjectDAO mockObjectDAO;
  private DALService mockDalService;
  private InterestDTO mockInterestDTO;
  private InterestDTO newMockInterestDTO;
  private int nonExistentId = 1000;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.interestUCC = locator.getService(InterestUCC.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    this.mockInterestDTO = locator.getService(InterestDTO.class);
    Mockito.when(mockInterestDTO.getIdObject()).thenReturn(10);
    Mockito.when(mockInterestDTO.getIdMember()).thenReturn(1);
    Mockito.when(mockInterestDTO.getAvailabilityDate()).thenReturn(LocalDate.now());
    Mockito.when(mockInterestDTO.getStatus()).thenReturn("published");
    this.newMockInterestDTO = locator.getService(InterestDTO.class);
  }

  @DisplayName("test getInterest with a non existent object and an existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndExistentMember() {
    Mockito.when(mockInterestDAO.getOne(nonExistentId, 1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> interestUCC.getInterest(nonExistentId, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object and a non existent member")
  @Test
  public void testGetInterestWithExistentObjectAndNonExistentMember() {
    Mockito.when(mockInterestDAO.getOne(1, nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> interestUCC.getInterest(1, nonExistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with a non existent object and a non existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndNonExistentMember() {
    Mockito.when(mockInterestDAO.getOne(nonExistentId, nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,() -> interestUCC.getInterest(nonExistentId, nonExistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndExistingInterest() {
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
    Mockito.when(mockInterestDAO.getOne(10, 1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.getInterest(10, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test addOne with a good interest")
  @Test
  public void testAddOneWithAGoodInterest(){
    Mockito.when(newMockInterestDTO.getIdObject()).thenReturn(10);
    Mockito.when(newMockInterestDTO.getIdMember()).thenReturn(1);
    Mockito.when(newMockInterestDTO.getAvailabilityDate()).thenReturn(LocalDate.now());
    assertAll(
        () -> assertEquals(newMockInterestDTO, interestUCC.addOne(newMockInterestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1)).addOne(newMockInterestDTO),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test addOne with an interest that already exists")
  @Test
  public void testAddOneWithAnAlreadyExistentInterest(){
    Mockito.when(mockInterestDAO.getOne(mockInterestDTO.getIdObject(),
        mockInterestDTO.getIdMember())).thenReturn(mockInterestDTO);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.addOne(mockInterestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getInterestedCount with non-existent object")
  @Test
  public void testGetInterestedCountWithNonExistentObject(){
    Mockito.when(mockObjectDAO.getOne(nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.getInterestedCount(nonExistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

}