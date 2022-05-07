package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ConflictException;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
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
  private final int nonExistentId = 1000;
  private InterestUCC interestUCC;
  private InterestDAO mockInterestDAO;
  private ObjectDAO mockObjectDAO;
  private MemberDAO mockMemberDAO;
  private OfferDAO mockOfferDAO;
  private DALService mockDalService;
  private ObjectDTO objectDTO;
  private InterestDTO interestDTO;
  private InterestDTO newInterestDTO;
  private ObjectFactory objectFactory;
  private InterestFactory interestFactory;
  private OfferFactory offerFactory;
  private MemberFactory memberFactory;

  @BeforeEach
  void initAll() {
    this.interestUCC = locator.getService(InterestUCC.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.mockOfferDAO = locator.getService(OfferDAO.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    ObjectFactory objectFactory = locator.getService(ObjectFactory.class);
    this.objectDTO = objectFactory.getObjectDTO();
    this.objectDTO.setIdObject(10);
    this.objectDTO.setIdOfferor(35);

    InterestFactory interestFactory = locator.getService(InterestFactory.class);
    this.interestDTO = interestFactory.getInterestDTO();
    this.interestDTO.setObject(objectDTO);
    this.interestDTO.setIdMember(1);
    this.interestDTO.setAvailabilityDate(LocalDate.now());
    this.interestDTO.setStatus("published");
    this.interestDTO.setIdObject(objectDTO.getIdObject());
    this.newInterestDTO = interestFactory.getInterestDTO();
    this.objectFactory = locator.getService(ObjectFactory.class);
    this.offerFactory = locator.getService(OfferFactory.class);
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
  @DisplayName("test addOne with already an existent interest")
  @Test
  public void testAddOneWithAlreadyAnExistentInterest() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(interestDTO);
    assertAll(
        () -> assertThrows(ConflictException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with non existent object")
  @Test
  public void testAddOneWithNonExistentObject() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with object of interest having status neither interested nor available")
  @Test
  public void testAddOneWithObjectInterestGivenStatus() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    objectDTO.setStatus("given");
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTO);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with non existent offer interest")
  @Test
  public void testAddOneWithNonExistentOfferInterest() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    objectDTO.setStatus("available");
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with existent cancelled offer interest")
  @Test
  public void testAddOneWithExistentCancelledOfferInterest() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    objectDTO.setStatus("available");
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setStatus("cancelled");

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with none interest and object have not same version")
  @Test
  public void testAddOneWithNoneInterestExistentAndObjectNotSameVersion() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    objectDTO.setStatus("available");
    objectDTO.setVersion(13);
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setStatus("available");
    ObjectDTO objectDTOFromGetOne = objectFactory.getObjectDTO();
    objectDTOFromGetOne.setStatus("available");
    objectDTOFromGetOne.setVersion(14);
    objectDTOFromGetOne.setIdObject(12);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTOFromGetOne.getIdObject()))
        .thenReturn(offerDTO);
    Mockito.when(mockInterestDAO.getAllCount(interestDTO.getIdObject()))
        .thenReturn(0);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with none interest, not same version, and interested status")
  @Test
  public void testAddOneWithNoneInterestExistentAndObjectNotSameVersionAndInterestedStatus() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    objectDTO.setIdObject(12);
    objectDTO.setStatus("interested");
    objectDTO.setVersion(13);
    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setStatus("interested");
    ObjectDTO objectDTOFromGetOne = objectFactory.getObjectDTO();
    objectDTOFromGetOne.setStatus("interested");
    objectDTOFromGetOne.setVersion(14);
    objectDTOFromGetOne.setIdObject(12);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTOFromGetOne.getIdObject()))
        .thenReturn(offerDTO);
    Mockito.when(mockInterestDAO.getAllCount(interestDTO.getIdObject()))
        .thenReturn(0);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne with none interest and offer have not same version")
  @Test
  public void testAddOneWithNoneInterestExistentAndOfferNotSameVersion() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setStatus("available");
    offerDTO.setVersion(12);

    objectDTO.setIdObject(12);
    objectDTO.setStatus("available");
    objectDTO.setVersion(14);

    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());
    interestDTO.setOffer(offerDTO);

    OfferDTO offerDTOFromGetLast = offerFactory.getOfferDTO();
    offerDTOFromGetLast.setStatus("available");
    offerDTOFromGetLast.setVersion(17);

    ObjectDTO objectDTOFromGetOne = objectFactory.getObjectDTO();
    objectDTOFromGetOne.setStatus("available");
    objectDTOFromGetOne.setVersion(14);
    objectDTOFromGetOne.setIdObject(12);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTOFromGetOne.getIdObject()))
        .thenReturn(offerDTOFromGetLast);
    Mockito.when(mockInterestDAO.getAllCount(interestDTO.getIdObject()))
        .thenReturn(0);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.addOne(interestDTO, authenticatedUser)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test addOne success with none interest existent for the object")
  @Test
  public void testAddOneSuccessWithNoneInterestExistentForTheObject() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setStatus("available");
    offerDTO.setVersion(17);

    objectDTO.setIdObject(12);
    objectDTO.setStatus("available");
    objectDTO.setVersion(14);

    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());
    interestDTO.setOffer(offerDTO);

    OfferDTO offerDTOFromGetLast = offerFactory.getOfferDTO();
    offerDTOFromGetLast.setStatus("available");
    offerDTOFromGetLast.setVersion(17);

    ObjectDTO objectDTOFromGetOne = objectFactory.getObjectDTO();
    objectDTOFromGetOne.setStatus("available");
    objectDTOFromGetOne.setVersion(14);
    objectDTOFromGetOne.setIdObject(12);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(1);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTOFromGetOne.getIdObject()))
        .thenReturn(offerDTOFromGetLast);
    Mockito.when(mockInterestDAO.getAllCount(interestDTO.getIdObject()))
        .thenReturn(0);
    Mockito.when(mockInterestDAO.addOne(interestDTO))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);

    InterestDTO interestDTOAdded = interestUCC.addOne(interestDTO, authenticatedUser);

    assertAll(
        () -> assertEquals("interested", objectDTOFromGetOne.getStatus()),
        () -> assertEquals("interested", offerDTOFromGetLast.getStatus()),
        () -> assertTrue(interestDTOAdded.getIsNotificated()),
        () -> assertEquals(memberDTO, interestDTOAdded.getMember()),
        () -> assertEquals(objectDTOFromGetOne, interestDTOAdded.getObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("test addOne success with 3 interests existent for the object")
  @Test
  public void testAddOneSuccessWith3InterestsExistentForTheObject() {
    MemberDTO authenticatedUser = memberFactory.getMemberDTO();
    authenticatedUser.setMemberId(1);
    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setStatus("available");
    offerDTO.setVersion(17);

    objectDTO.setIdObject(12);
    objectDTO.setStatus("available");
    objectDTO.setVersion(14);

    interestDTO.setObject(objectDTO);
    interestDTO.setAvailabilityDate(LocalDate.now());
    interestDTO.setOffer(offerDTO);

    OfferDTO offerDTOFromGetLast = offerFactory.getOfferDTO();
    offerDTOFromGetLast.setStatus("available");
    offerDTOFromGetLast.setVersion(17);

    ObjectDTO objectDTOFromGetOne = objectFactory.getObjectDTO();
    objectDTOFromGetOne.setStatus("available");
    objectDTOFromGetOne.setVersion(14);
    objectDTOFromGetOne.setIdObject(12);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(1);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), interestDTO.getIdMember()))
        .thenReturn(null);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockOfferDAO.getLastObjectOffer(objectDTOFromGetOne.getIdObject()))
        .thenReturn(offerDTOFromGetLast);
    Mockito.when(mockInterestDAO.getAllCount(interestDTO.getIdObject()))
        .thenReturn(3);
    Mockito.when(mockInterestDAO.addOne(interestDTO))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTOFromGetOne);
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);

    InterestDTO interestDTOAdded = interestUCC.addOne(interestDTO, authenticatedUser);

    assertAll(
        () -> assertTrue(interestDTOAdded.getIsNotificated()),
        () -> assertEquals(memberDTO, interestDTOAdded.getMember()),
        () -> assertEquals(objectDTOFromGetOne, interestDTOAdded.getObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  //  ---------------------------- GET ALL INTERESTS UCC  -------------------------------  //

  @DisplayName("test getAllInterests with non existent object")
  @Test
  public void testGetAllInterestsWithNonExistentObject() {
    Mockito.when(mockObjectDAO.getOne(nonExistentId))
        .thenReturn(null);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getAllInterests(nonExistentId, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getAllInterests with not same id member and id offeror")
  @Test
  public void testGetAllInterestsWithNotSameIdMemberAndIdOfferor() {
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    objectDTO.setIdOfferor(13);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(12);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.getAllInterests(objectDTO.getIdObject(), memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getAllInterests with null list of published interests")
  @Test
  public void testGetAllInterestsWithNullListInterestPublishedFromDao() {
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getAllPublished(objectDTO.getIdObject()))
        .thenReturn(null);
    objectDTO.setIdOfferor(13);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getAllInterests(objectDTO.getIdObject(), memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("test getAllInterests success")
  @Test
  public void testGetAllInterestsSuccess() {
    newInterestDTO.setIdObject(4);
    List<InterestDTO> listOfInterests = new ArrayList<>();
    listOfInterests.add(newInterestDTO);
    objectDTO.setIdOfferor(13);
    objectDTO.setIdObject(4);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getAllPublished(objectDTO.getIdObject()))
        .thenReturn(listOfInterests);

    List<InterestDTO> listFromUcc = interestUCC.getAllInterests(objectDTO.getIdObject(), memberDTO);
    assertAll(
        () -> assertEquals(listOfInterests, listFromUcc),
        () -> assertTrue(listFromUcc.contains(newInterestDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  //  ---------------------------- GET NOTIFICATION COUNT UCC  -------------------------------  //


  @DisplayName("Test getNotificationCount with fatal exception from dao")
  @Test
  public void testGetNotificationCountThrowFatalException() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(mockInterestDAO.getNotificationCount(memberDTO.getMemberId())).thenThrow(
        FatalException.class);

    assertAll(
        () -> assertThrows(FatalException.class, () -> interestUCC.getNotificationCount(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getNotificationCount success")
  @Test
  public void testGetNotificationCountSuccess() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockInterestDAO.getNotificationCount(memberDTO.getMemberId())).thenReturn(5);

    assertAll(
        () -> assertEquals(5, interestUCC.getNotificationCount(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- GET NOTIFICATIONS UCC  -------------------------------  //

  @DisplayName("Test getNotifications with null list of interests returned from dao")
  @Test
  public void testGetNotificationsWithNullListOfInterestsReturnedFromDao() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockInterestDAO.getAllNotifications(memberDTO.getMemberId()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> interestUCC.getNotifications(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getNotifications with 1 notification interest")
  @Test
  public void testGetNotificationsWith1NotificationInterest() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    InterestDTO interestDTONotificated = interestFactory.getInterestDTO();
    interestDTONotificated.setIsNotificated(true);
    interestDTONotificated.setIdMember(memberDTO.getMemberId());
    interestDTONotificated.setIdObject(2);

    List<InterestDTO> interestDTOList = new ArrayList<>();
    interestDTOList.add(interestDTONotificated);

    Mockito.when(mockInterestDAO.getAllNotifications(memberDTO.getMemberId()))
        .thenReturn(interestDTOList);

    List<InterestDTO> listOfNotifications = interestUCC.getNotifications(memberDTO);

    assertAll(
        () -> assertEquals(listOfNotifications, interestDTOList),
        () -> assertTrue(listOfNotifications.contains(interestDTONotificated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ------------------------ MARK ALL NOTIFICATIONS SHOWN UCC  ---------------------------  //

  @DisplayName("Test markAllNotificationsShown with null list of interests returned from dao")
  @Test
  public void testMarkAllNotificationsShownWithNullListOfInterestsReturnedFromDao() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockInterestDAO.markAllNotificationsShown(memberDTO.getMemberId()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.markAllNotificationsShown(memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test markAllNotificationsShown with 1 notification interest")
  @Test
  public void testMarkAllNotificationsShownWith1NotificationInterest() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    InterestDTO interestDTONotificated = interestFactory.getInterestDTO();
    interestDTONotificated.setIsNotificated(true);
    interestDTONotificated.setIdMember(memberDTO.getMemberId());
    interestDTONotificated.setIdObject(2);

    InterestDTO interestDTONotNotificated = interestFactory.getInterestDTO();
    interestDTONotNotificated.setIsNotificated(false);
    interestDTONotNotificated.setIdMember(memberDTO.getMemberId());
    interestDTONotNotificated.setIdObject(2);

    List<InterestDTO> interestDTOList = new ArrayList<>();
    interestDTOList.add(interestDTONotNotificated);

    Mockito.when(mockInterestDAO.markAllNotificationsShown(memberDTO.getMemberId()))
        .thenReturn(interestDTOList);

    List<InterestDTO> listOfNotifications = interestUCC.markAllNotificationsShown(memberDTO);

    assertAll(
        () -> assertEquals(listOfNotifications, interestDTOList),
        () -> assertTrue(listOfNotifications.contains(interestDTONotNotificated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- MARK NOTIFICATION SHOWN UCC  -------------------------------  //
  @DisplayName("Test markNotificationShown with non existent object in db")
  @Test
  public void testMarkNotificationShownWithNonExistentObjectInDb() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockObjectDAO.getOne(nonExistentId))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.markNotificationShown(nonExistentId, memberDTO,
                memberDTO.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test markNotificationShown with non existent interest in db")
  @Test
  public void testMarkNotificationShownWithNonExistentInterestInDb() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(12);
    objectDTO.setIdOfferor(memberDTO.getMemberId());

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);

    Mockito.when(mockInterestDAO.getOne(objectDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.markNotificationShown(objectDTO.getIdObject(), memberDTO,
                memberDTO.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test markNotificationShown with not the same user interest as the requester")
  @Test
  public void testMarkNotificationShownWithNotTheSameUserInterestAsTheRequester() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(12);
    objectDTO.setIdOfferor(3);

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);

    Mockito.when(mockInterestDAO.getOne(objectDTO.getIdObject(), 1))
        .thenReturn(interestFactory.getInterestDTO());

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.markNotificationShown(objectDTO.getIdObject(), memberDTO,
                1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }


  @DisplayName("Test markNotificationShown with an interest already shown")
  @Test
  public void testMarkNotificationShownWithAnInterestAlreadyShown() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(12);
    objectDTO.setIdOfferor(memberDTO.getMemberId());

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIsNotificated(false);
    interestDTO.setIdObject(objectDTO.getIdObject());

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject())).thenReturn(objectDTO);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.markNotificationShown(interestDTO.getIdObject(), memberDTO,
                memberDTO.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test markNotificationShown with an interest already shown v2")
  @Test
  public void testMarkNotificationShownWithAnInterestAlreadyShownV2() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(12);
    objectDTO.setIdOfferor(71);

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIsNotificated(false);
    interestDTO.setIdObject(objectDTO.getIdObject());

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject())).thenReturn(objectDTO);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.markNotificationShown(interestDTO.getIdObject(), memberDTO,
                memberDTO.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test markNotificationShown with an interest already shown v3")
  @Test
  public void testMarkNotificationShownWithAnInterestAlreadyShownV3() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(12);
    objectDTO.setIdOfferor(memberDTO.getMemberId());

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIsNotificated(false);
    interestDTO.setIdObject(objectDTO.getIdObject());

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), 71))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject())).thenReturn(objectDTO);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.markNotificationShown(interestDTO.getIdObject(), memberDTO,
                71)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test markNotificationShown success")
  @Test
  public void testMarkNotificationShownSuccess() {

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(12);
    objectDTO.setIdOfferor(memberDTO.getMemberId());

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIsNotificated(true);
    interestDTO.setIdObject(objectDTO.getIdObject());
    interestDTO.setIdMember(memberDTO.getMemberId());

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);

    InterestDTO interestDTOShown = interestUCC.markNotificationShown(interestDTO.getIdObject(),
        memberDTO,
        memberDTO.getMemberId());

    assertAll(
        () -> assertEquals(objectDTO, interestDTOShown.getObject()),
        () -> assertEquals(memberDTO, interestDTOShown.getMember()),
        () -> assertFalse(interestDTOShown.getIsNotificated()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------GET INTERESTED COUNT UCC  -------------------------------  //

  @DisplayName("Test getInterestedCount with non existent object")
  @Test
  public void testGetInterestedCountWithNonExistentObject() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockObjectDAO.getOne(nonExistentId))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.getInterestedCount(nonExistentId, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getInterestedCount success but the member is not interested in the object")
  @Test
  public void testGetInterestedCountSuccessWithoutIsUserInterested() {
    objectDTO.setIdObject(15);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getAllPublishedCount(objectDTO.getIdObject()))
        .thenReturn(3);
    Mockito.when(mockInterestDAO.getOne(objectDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(null);

    JsonNode result = interestUCC.getInterestedCount(objectDTO.getIdObject(), memberDTO);
    assertAll(
        () -> assertEquals(3, Integer.parseInt(result.get("count").toString())),
        () -> assertFalse(Boolean.parseBoolean(result.get("isUserInterested").toString())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test getInterestedCount success but the member is interested in the object")
  @Test
  public void testGetInterestedCountSuccessWithIsUserInterested() {
    objectDTO.setIdObject(15);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    Mockito.when(mockInterestDAO.getAllPublishedCount(objectDTO.getIdObject()))
        .thenReturn(3);
    Mockito.when(mockInterestDAO.getOne(objectDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestFactory.getInterestDTO());

    JsonNode result = interestUCC.getInterestedCount(objectDTO.getIdObject(), memberDTO);
    assertAll(
        () -> assertEquals(3, Integer.parseInt(result.get("count").toString())),
        () -> assertTrue(Boolean.parseBoolean(result.get("isUserInterested").toString())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------ASSIGN Offer UCC  -------------------------------  //


  @DisplayName("Test assignOffer with non existent interest")
  @Test
  public void testAssignOfferWithNonExistentInterest() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(nonExistentId);

    interestDTO.setIdMember(memberDTO.getMemberId());

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with not same id member and id offeror")
  @Test
  public void testAssignOfferWithNotSameIdMemberAndIdOfferor() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    interestDTO.setIdMember(memberDTO.getMemberId());

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with not same offer version")
  @Test
  public void testAssignOfferWithNotSameOfferVersion() {
    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(interestDTO.getObject());
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(12);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with not same object version")
  @Test
  public void testAssignOfferWithNotSameObjectVersion() {
    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);

    objectDTO.setVersion(12);

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setObject(newObject);

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with prevented member status")
  @Test
  public void testAssignOfferWithPreventedMemberStatus() {
    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);

    objectDTO.setVersion(26);

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setStatus("not_collected");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("prevented");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setMember(memberDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }


  @DisplayName("Test assignOffer with available offer status")
  @Test
  public void testAssignOfferWithAvailableOfferStatus() {
    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);
    newObject.setStatus("available");

    objectDTO.setVersion(26);
    objectDTO.setStatus("available");

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setStatus("available");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);
    offerDTO.setStatus("available");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("valid");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setMember(memberDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with already an assigned interest")
  @Test
  public void testAssignOfferWithAlreadyAnAssignedInterest() {

    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);
    newObject.setStatus("not_collected");

    objectDTO.setVersion(26);
    objectDTO.setStatus("not_collected");

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setStatus("not_collected");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);
    offerDTO.setStatus("not_collected");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("valid");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setMember(memberDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);
    InterestDTO interestFromGetAssigned = interestFactory.getInterestDTO();
    Mockito.when(mockInterestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestFromGetAssigned);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with already an assigned interest v2")
  @Test
  public void testAssignOfferWithAlreadyAnAssignedInterestV2() {

    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);
    newObject.setStatus("interested");

    objectDTO.setVersion(26);
    objectDTO.setStatus("interested");

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setStatus("interested");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);
    offerDTO.setStatus("interested");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("valid");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setMember(memberDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);
    InterestDTO interestFromGetAssigned = interestFactory.getInterestDTO();
    Mockito.when(mockInterestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestFromGetAssigned);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with assigned interest status")
  @Test
  public void testAssignOfferWithAssignedInterestStatus() {

    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);
    newObject.setStatus("not_collected");

    objectDTO.setVersion(26);
    objectDTO.setStatus("not_collected");

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setStatus("not_collected");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);
    offerDTO.setStatus("not_collected");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("valid");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setStatus("assigned");
    interestDTO.setMember(memberDTO);

    Mockito.when(mockInterestDAO.getOne(interestDTO.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);
    Mockito.when(mockInterestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer with not same interest version")
  @Test
  public void testAssignOfferWithNotSameInterestVersion() {

    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);
    newObject.setStatus("not_collected");

    objectDTO.setVersion(26);
    objectDTO.setStatus("not_collected");

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setStatus("not_collected");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);
    offerDTO.setStatus("not_collected");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("valid");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setStatus("published");
    interestDTO.setVersion(12);
    interestDTO.setMember(memberDTO);

    InterestDTO interestDTOInParam = interestFactory.getInterestDTO();
    interestDTOInParam.setVersion(30);
    interestDTOInParam.setIdObject(interestDTO.getIdObject());
    interestDTOInParam.setIdMember(interestDTO.getIdMember());
    interestDTOInParam.setOffer(interestDTO.getOffer());
    interestDTOInParam.setMember(interestDTOInParam.getMember());
    interestDTOInParam.setObject(interestDTO.getObject());

    Mockito.when(mockInterestDAO.getOne(interestDTOInParam.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);
    Mockito.when(mockInterestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> interestUCC.assignOffer(interestDTOInParam, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test assignOffer success")
  @Test
  public void testAssignOfferSuccess() {

    ObjectDTO newObject = objectFactory.getObjectDTO();
    newObject.setIdObject(objectDTO.getIdObject());
    newObject.setVersion(26);
    newObject.setStatus("not_collected");

    objectDTO.setVersion(26);
    objectDTO.setStatus("not_collected");

    OfferDTO offerDTOFromGetLastOne = offerFactory.getOfferDTO();
    offerDTOFromGetLastOne.setObject(newObject);
    offerDTOFromGetLastOne.setIdOffer(18);
    offerDTOFromGetLastOne.setVersion(14);
    offerDTOFromGetLastOne.setStatus("not_collected");

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setObject(interestDTO.getObject());
    offerDTO.setIdOffer(18);
    offerDTO.setVersion(14);
    offerDTO.setStatus("not_collected");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(3);
    memberDTO.setStatus("valid");

    interestDTO.setIdMember(memberDTO.getMemberId());
    interestDTO.getObject().setIdOfferor(memberDTO.getMemberId());
    interestDTO.setOffer(offerDTO);
    interestDTO.setStatus("published");
    interestDTO.setVersion(12);
    interestDTO.setMember(memberDTO);

    InterestDTO interestDTOInParam = interestFactory.getInterestDTO();
    interestDTOInParam.setVersion(interestDTO.getVersion());
    interestDTOInParam.setIdObject(interestDTO.getIdObject());
    interestDTOInParam.setIdMember(interestDTO.getIdMember());
    interestDTOInParam.setOffer(interestDTO.getOffer());
    interestDTOInParam.setMember(interestDTOInParam.getMember());
    interestDTOInParam.setObject(interestDTO.getObject());

    Mockito.when(mockInterestDAO.getOne(interestDTOInParam.getIdObject(), memberDTO.getMemberId()))
        .thenReturn(interestDTO);
    Mockito.when(mockObjectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(interestDTO.getObject());
    Mockito.when(mockMemberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(memberDTO);
    Mockito.when(mockOfferDAO.getLastObjectOffer(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromGetLastOne);
    Mockito.when(mockInterestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    InterestDTO interestDTOAssigned = interestUCC.assignOffer(interestDTOInParam, memberDTO);

    assertAll(
        () -> assertTrue(interestDTOAssigned.getIsNotificated()),
        () -> assertEquals("assigned", interestDTOAssigned.getStatus()),
        () -> assertEquals("assigned", offerDTOFromGetLastOne.getStatus()),
        () -> assertEquals("assigned", newObject.getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

}

