package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
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

  private final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());

  private InterestUCC interestUCC;
  private InterestDAO mockInterestDAO;
  private ObjectDAO mockObjectDAO;
  private DALService mockDalService;
  private ObjectDTO objectDTO;
  private InterestDTO interestDTO;
  private InterestDTO newInterestDTO;
  private int nonExistentId = 1000;
  private ObjectFactory objectFactory;

  @BeforeEach
  void initAll() {
    this.interestUCC = locator.getService(InterestUCC.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    ObjectFactory objectFactory = locator.getService(ObjectFactory.class);
    this.objectDTO = objectFactory.getObjectDTO();
    this.objectDTO.setIdObject(10);

    InterestFactory interestFactory = locator.getService(InterestFactory.class);
    this.interestDTO = interestFactory.getInterestDTO();
    this.interestDTO.setObject(objectDTO);
    this.interestDTO.setIdMember(1);
    this.interestDTO.setAvailabilityDate(LocalDate.now());
    this.interestDTO.setStatus("published");
    this.newInterestDTO = interestFactory.getInterestDTO();
    this.objectFactory = locator.getService(ObjectFactory.class);
  }

  @DisplayName("test getInterest with a non existent object and an existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndExistentMember() {
    objectDTO.setIdObject(nonExistentId);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC
            .getInterest(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object and a non existent member")
  @Test
  public void testGetInterestWithExistentObjectAndNonExistentMember() {
    interestDTO.setIdMember(nonExistentId);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC
            .getInterest(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with a non existent object and a non existent member")
  @Test
  public void testGetInterestWithNonExistentObjectAndNonExistentMember() {
    interestDTO.setIdMember(nonExistentId);
    objectDTO.setIdObject(nonExistentId);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () ->
            interestUCC.getInterest(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndExistingInterest() {
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(interestDTO);
    assertAll(
        () -> assertEquals(interestDTO,
            interestUCC.getInterest(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and non-existent interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndNonExistentInterest() {
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC
            .getInterest(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getOne(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with a good interest")
  @Test
  public void testAddOneWithAGoodInterest() {
    newInterestDTO.setObject(objectDTO);
    newInterestDTO.setIdMember(1);
    newInterestDTO.setAvailabilityDate(LocalDate.now());
    OfferFactory offerFactory = locator.getService(OfferFactory.class);
    OfferDTO offerDTO = offerFactory.getOfferDTO();
    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(10);
    offerDTO.setObject(objectDTO);
    OfferDAO mockOfferDAO = locator.getService(OfferDAO.class);
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    Mockito.when(mockOfferDAO.getOneByObject(objectDTO.getIdObject())).thenReturn(offerDTO);
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
    objectDTO.setIdObject(nonExistentId);
    newInterestDTO.setObject(objectDTO);
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
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
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
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).getOne(nonExistentId),
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
    Mockito.when(mockInterestDAO.getAllPublished(1)).thenReturn(allInterests);
    assertAll(
        () -> assertEquals(allInterests, interestUCC.getInterestedCount(1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(1),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getAllPublished(1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

}