package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ConflictException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import java.time.LocalDate;
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
  private MemberDAO mockMemberDAO;
  private InterestDAO interestDAO;
  private DALService mockDalService;
  private ObjectDTO objectDTO;
  private InterestDTO interestDTO;
  private InterestDTO newInterestDTO;
  private int nonExistentId = 1000;
  private ObjectFactory objectFactory;
  private InterestFactory interestFactory;
  private MemberFactory memberFactory;

  @BeforeEach
  void initAll() {
    this.interestUCC = locator.getService(InterestUCC.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.interestDAO = locator.getService(InterestDAO.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
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
    this.interestDTO.setIdObject(objectDTO.getIdObject());
    this.newInterestDTO = interestFactory.getInterestDTO();
    this.objectFactory = locator.getService(ObjectFactory.class);
    this.interestFactory = locator.getService(InterestFactory.class);
    this.memberFactory = locator.getService(MemberFactory.class);
  }

  //------------------------------- GET INTEREST InterestUCC---------------------------------------
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
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getInterest with an existent object, member and interest")
  @Test
  public void testGetInterestWithExistentObjectAndExistentMemberAndExistingInterest() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(interestDTO.getIdMember());
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember())).thenReturn(memberDTO);
    assertAll(
        () -> assertEquals(interestDTO,
            interestUCC.getInterest(interestDTO.getObject().getIdObject(),
                interestDTO.getIdMember())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
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
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  //------------------------------- ADD ONE InterestUCC---------------------------------------
  @DisplayName("test addOne with already an existing interest")
  @Test
  public void testAddOneWithAlreadyAnExistingInterest() {
    objectDTO.setIdObject(12);
    interestDTO.setObject(objectDTO);
    interestDTO.setIdMember(1);
    interestDTO.setAvailabilityDate(LocalDate.now());
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(interestDTO);
    assertAll(
        () -> assertThrows(ConflictException.class, () -> interestUCC.addOne(interestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with non existing object")
  @Test
  public void testAddOneWithNonExistingObject() {
    objectDTO.setIdObject(12);
    interestDTO.setObject(objectDTO);
    interestDTO.setIdMember(1);
    interestDTO.setAvailabilityDate(LocalDate.now());
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.addOne(interestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with object of interest not same version")
  @Test
  public void testAddOneWithObjectInterestNotSameVersion() {
    objectDTO.setIdObject(12);
    objectDTO.setVersion(15);
    interestDTO.setObject(objectDTO);
    interestDTO.setIdMember(1);
    interestDTO.setAvailabilityDate(LocalDate.now());
    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setVersion(13);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> interestUCC.addOne(interestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  //----------------------
  /*
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
    Mockito.when(mockInterestDAO.addOne(newInterestDTO)).thenReturn(newInterestDTO);
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
        () -> assertThrows(ForbiddenException.class, () -> interestUCC.addOne(interestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with existing interests for an object")
  @Test
  public void testAddOneWithANonExistentInterestAndAlreadyExistingInterestsForObject() {
    Mockito.when(mockInterestDAO.getOne(interestDTO.getObject().getIdObject(),
        interestDTO.getIdMember())).thenReturn(null);
    Mockito.when(mockInterestDAO.getAllCount(interestDTO.getObject().getIdObject()))
        .thenReturn(5);
    Mockito.when(mockInterestDAO.addOne(interestDTO)).thenReturn(interestDTO);
    assertAll(
        () -> assertEquals(interestDTO, interestUCC.addOne(interestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }


   */

/*
  @DisplayName("test getInterestedCount with non-existent object")
  @Test
  public void testGetInterestedCountWithNonExistentObject() {
    Mockito.when(mockObjectDAO.getOne(nonExistentId)).thenReturn(null);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getInterestedCount(nonExistentId, memberDTO)),
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
    ObjectDTO object = this.objectFactory.getObjectDTO();
    Mockito.when(mockObjectDAO.getOne(1)).thenReturn(object);
    Mockito.when(mockInterestDAO.getAllPublishedCount(1)).thenReturn(300); //TODO
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(15);
    assertAll(
        () -> assertEquals(300, interestUCC.getInterestedCount(1, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(1),
        () -> Mockito.verify(mockInterestDAO, Mockito.atLeast(1))
            .getAllPublishedCount(1),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  //  ---------------------------- GET NOTIFICATION UCC  -------------------------------  //

  @DisplayName("Test getNotification with 1 notification interest")
  @Test
  public void testGetNotificationsWith1NotificationInterest() {
    InterestDTO interestDTONotificated = interestFactory.getInterestDTO();
    interestDTONotificated.setIsNotificated(true);
    interestDTONotificated.setIdMember(3);

    InterestDTO interestDTONotNotificated = interestFactory.getInterestDTO();
    interestDTONotNotificated.setIsNotificated(false);
    interestDTONotNotificated.setIdMember(2);

    List<InterestDTO> interestDTOList = new ArrayList<>();
    interestDTOList.add(interestDTONotificated);

    Mockito.when(interestDAO.getAllNotifications(3))
        .thenReturn(interestDTOList);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    assertAll(
        () -> assertEquals(1, interestUCC.getNotifications(memberDTO).size()),
        () -> assertTrue(interestUCC.getNotifications(memberDTO).contains(interestDTONotificated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test getNotification with an empty list of notification interest")
  @Test
  public void testGetNotificationsWithAnEmptyListNotificationInterest() {

    List<InterestDTO> interestDTOList = new ArrayList<>();

    Mockito.when(interestDAO.getAllNotifications(3))
        .thenReturn(interestDTOList);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.getNotifications(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  ------------------------ MARK ALL NOTIFICATIONS SHOWN UCC  ---------------------------  //


  @DisplayName("Test markAllNotificationShown with 1 notification interest")
  @Test
  public void testMarkAllNotificationsShownWith1NotificationInterest() {

    InterestDTO interestDTONotNotificated = interestFactory.getInterestDTO();
    interestDTONotNotificated.setIsNotificated(false);
    interestDTONotNotificated.setIdMember(3);
    interestDTONotNotificated.setIdMember(2);

    List<InterestDTO> interestDTOList = new ArrayList<>();
    interestDTOList.add(interestDTONotNotificated);

    Mockito.when(interestDAO.markAllNotificationsShown(3))
        .thenReturn(interestDTOList);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    assertAll(
        () -> assertEquals(1, interestUCC.markAllNotificationsShown(memberDTO).size()),
        () -> assertTrue(
            interestUCC.markAllNotificationsShown(memberDTO).contains(interestDTONotNotificated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test markAllNotificationShown with an empty list of notification interest")
  @Test
  public void testMarkAllNotificationsShownWithAnEmptyListNotificationInterest() {

    List<InterestDTO> interestDTOList = new ArrayList<>();

    Mockito.when(interestDAO.markAllNotificationsShown(3))
        .thenReturn(interestDTOList);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.markAllNotificationsShown(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  ---------------------------- MARK NOTIFICATION SHOWN UCC  -------------------------------  //

  @DisplayName("Test mark notification shown success")
  @Test
  public void testMarkNotificationShownSuccess() {
    InterestDTO interestDTONotificated = interestFactory.getInterestDTO();
    interestDTONotificated.setIsNotificated(true);
    interestDTONotificated.setIdMember(2);
    Mockito.when(interestDAO.updateNotification(interestDTONotificated))
        .thenReturn(interestDTONotificated);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    assertAll(
        () -> assertFalse(
            interestUCC.markNotificationShown(interestDTONotificated.getObject().getIdObject(),
                memberDTO).getIsNotificated()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test markNotificationShown with field isNotificated already false")
  @Test
  public void testMarkNotificationShownWithFieldIsNotificatedAlreadyFalse() {
    InterestDTO interestDTONotNotificated = interestFactory.getInterestDTO();
    interestDTONotNotificated.setIsNotificated(false);
    interestDTONotNotificated.setIdMember(2);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.markNotificationShown(
                interestDTONotNotificated.getObject().getIdObject(),
                memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  ---------------------------- GET ALL INTERESTS UCC  -------------------------------  //


  @DisplayName("Test getAllInterests with an empty list of interests")
  @Test
  public void testGetAllInterestsWithAnEmptyListOfInterests() {

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(2);

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(interestDAO.getAllPublished(objectDTO.getIdObject()))
        .thenReturn(null);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(objectDTO.getIdOfferor());

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getAllInterests(objectDTO.getIdObject(), memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getAllInterests with 1 interest of 1 offeror")
  @Test
  public void testGetAllInterestsWith1InterestOf1Offeror() {
    objectDTO.setIdOfferor(2);

    InterestDTO interestDTONotificated = interestFactory.getInterestDTO();
    interestDTONotificated.setIsNotificated(true);
    interestDTONotificated.setStatus("published");
    interestDTONotificated.setIdMember(3);
    interestDTONotificated.setIdMember(3);
    interestDTONotificated.setObject(objectDTO);

    InterestDTO interestDTONotNotificated = interestFactory.getInterestDTO();
    interestDTONotNotificated.setIsNotificated(false);
    interestDTONotNotificated.setIdMember(3);
    interestDTONotNotificated.setIdMember(2);
    interestDTONotificated.setStatus("not_collected");

    List<InterestDTO> interestDTOList = new ArrayList<>();
    interestDTOList.add(interestDTONotificated);

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(interestDAO.getAllPublished(objectDTO.getIdObject()))
        .thenReturn(interestDTOList);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(objectDTO.getIdOfferor());

    assertAll(
        () -> assertEquals(1,
            interestUCC.getAllInterests(objectDTO.getIdObject(), memberDTO).size()),
        () -> assertTrue(
            interestUCC.getAllInterests(objectDTO.getIdObject(), memberDTO)
                .contains(interestDTONotificated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test getAllInterests with non existent object")
  @Test
  public void testGetAllInterestsWithNonExistentObject() {

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(null);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(0);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getAllInterests(0, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  ---------------------------- GET NOTIFICATION COUNT UCC  -------------------------------  //

  @DisplayName("Test getNotificationCount success")
  @Test
  public void testGetNotificationCountSuccess() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(interestDAO.getNotificationCount(memberDTO.getMemberId())).thenReturn(5);

    assertAll(
        () -> assertEquals(5, interestUCC.getNotificationCount(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }
  */

}

