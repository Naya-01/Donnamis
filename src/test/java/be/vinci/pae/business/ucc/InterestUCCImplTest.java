package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class InterestUCCImplTest {

  private InterestUCC interestUCC;
  private InterestDAO mockInterestDAO;
  private ObjectDAO mockObjectDAO;
  private DALService mockDalService;
  private InterestDTO interestDTO;
  private InterestDTO newInterestDTO;
  private int nonExistentId = 1000;
  private ServiceLocator locator;
  private ObjectFactory objectFactory;

  @BeforeEach
  void initAll() {
    locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.interestUCC = locator.getService(InterestUCC.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    InterestFactory interestFactory = locator.getService(InterestFactory.class);
    this.interestDTO = interestFactory.getInterestDTO();
    this.interestDTO.setIdObject(10);
    this.interestDTO.setIdMember(1);
    this.interestDTO.setAvailabilityDate(LocalDate.now());
    this.interestDTO.setStatus("published");
    this.newInterestDTO = interestFactory.getInterestDTO();
    this.objectFactory = locator.getService(ObjectFactory.class);
  }

  @DisplayName("test getInterest with a non existent object and an existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndExistentMember() {
    Mockito.when(mockInterestDAO.getOne(nonExistentId, 1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC
            .getInterest(nonExistentId, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(nonExistentId, 1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object and a non existent member")
  @Test
  public void testGetInterestWithExistentObjectAndNonExistentMember() {
    Mockito.when(mockInterestDAO.getOne(1, nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC
            .getInterest(1, nonExistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(1, nonExistentId),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with a non existent object and a non existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndNonExistentMember() {
    Mockito.when(mockInterestDAO.getOne(nonExistentId, nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () ->
            interestUCC.getInterest(nonExistentId, nonExistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(nonExistentId, nonExistentId),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndExistingInterest() {
    Mockito.when(mockInterestDAO.getOne(10, 1)).thenReturn(interestDTO);
    assertAll(
        () -> assertEquals(interestDTO, interestUCC.getInterest(10, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(10, 1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and non-existent interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndNonExistentInterest() {
    Mockito.when(mockInterestDAO.getOne(10, 1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC
            .getInterest(10, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(10, 1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with a good interest")
  @Test
  public void testAddOneWithAGoodInterest() {
    newInterestDTO.setIdObject(10);
    newInterestDTO.setIdMember(1);
    newInterestDTO.setAvailabilityDate(LocalDate.now());
    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(10);
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    assertAll(
        () -> assertEquals(newInterestDTO, interestUCC.addOne(newInterestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .addOne(newInterestDTO),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("test addOne with a non existent object")
  @Test
  public void testAddOneWithANonExistentObject() {
    newInterestDTO.setIdObject(nonExistentId);
    newInterestDTO.setIdMember(1);
    newInterestDTO.setAvailabilityDate(LocalDate.now());
    Mockito.when(mockObjectDAO.getOne(nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.addOne(newInterestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with an interest that already exists")
  @Test
  public void testAddOneWithAnAlreadyExistentInterest() {
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(),
        interestDTO.getIdMember())).thenReturn(interestDTO);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.addOne(interestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterestedCount with non-existent object")
  @Test
  public void testGetInterestedCountWithNonExistentObject() {
    Mockito.when(mockObjectDAO.getOne(nonExistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getInterestedCount(nonExistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).getOne(1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterestedCount with existent object")
  @Test
  public void testGetInterestedCountWithExistentObject() {
    List<InterestDTO> allInterests = new ArrayList<>();
    allInterests.add(newInterestDTO);
    ObjectDTO object = this.objectFactory.getObjectDTO();
    Mockito.when(mockObjectDAO.getOne(1)).thenReturn(object);
    Mockito.when(mockInterestDAO.getAll(1)).thenReturn(allInterests);
    assertAll(
        () -> assertEquals(allInterests, interestUCC.getInterestedCount(1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(1),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getAll(1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

}