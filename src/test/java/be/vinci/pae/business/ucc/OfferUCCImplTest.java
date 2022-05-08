package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class OfferUCCImplTest {

  private final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());

  private DALService mockDalService;
  private OfferDAO offerDAO;
  private TypeDAO typeDAO;
  private ObjectDAO objectDAO;
  private MemberDAO memberDAO;
  private OfferUCC offerUCC;
  private TypeFactory typeFactory;
  private ObjectFactory objectFactory;
  private OfferFactory offerFactory;
  private InterestFactory interestFactory;
  private InterestDAO interestDAO;
  private MemberFactory memberFactory;


  private OfferDTO getNewOffer() {
    TypeDTO typeDTO = typeFactory.getTypeDTO();
    typeDTO.setTypeName("Jouets");
    typeDTO.setIdType(1);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(3);
    objectDTO.setType(typeDTO);
    objectDTO.setVersion(1);

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setDate(LocalDate.now());
    offerDTO.setIdOffer(0);
    offerDTO.setVersion(1);
    offerDTO.setObject(objectDTO);
    return offerDTO;
  }

  @BeforeEach
  void setUp() {
    this.mockDalService = locator.getService(DALService.class);
    this.offerDAO = locator.getService(OfferDAO.class);
    this.typeDAO = locator.getService(TypeDAO.class);
    this.objectDAO = locator.getService(ObjectDAO.class);
    this.memberDAO = locator.getService(MemberDAO.class);
    this.offerUCC = locator.getService(OfferUCC.class);
    this.typeFactory = locator.getService(TypeFactory.class);
    this.objectFactory = locator.getService(ObjectFactory.class);
    this.offerFactory = locator.getService(OfferFactory.class);
    this.interestFactory = locator.getService(InterestFactory.class);
    this.interestDAO = locator.getService(InterestDAO.class);
    this.memberFactory = locator.getService(MemberFactory.class);
  }

  //  ----------------------------  GET LAST OFFERS UCC  -------------------------------  //
  @DisplayName("Test getLastOffers with none offer received from DAO")
  @Test
  public void testGetAllLastOffersWithDAOReturningEmptyListOfOffers() {
    Mockito.when(offerDAO.getAllLast()).thenReturn(new ArrayList<>());
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.getLastOffers()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getLastOffers with the last offers received from DAO")
  @Test
  public void testGetAllLastOffersSuccess() {
    OfferDTO offerDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(offerDTO.getDate()).thenReturn(LocalDate.now());

    Mockito.when(offerDAO.getAllLast()).thenReturn(List.of(offerDTO));
    assertAll(
        () -> assertTrue(offerUCC.getLastOffers().contains(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------  GET OFFER BY ID UCC  -------------------------------  //

  @DisplayName("Test getOfferById with an existent id offer")
  @Test
  public void testGetOfferByIdSuccess() {
    OfferDTO offerDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(offerDTO.getDate()).thenReturn(LocalDate.now());
    Mockito.when(offerDTO.getIdOffer()).thenReturn(1);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);
    assertAll(
        () -> assertEquals(offerDTO, offerUCC.getOfferById(1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test getOfferById with a non existent id offer")
  @Test
  public void testGetOfferByIdWithANonExistentIdOffer() {
    OfferDTO offerDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(offerDTO.getDate()).thenReturn(LocalDate.now());
    Mockito.when(offerDTO.getIdOffer()).thenReturn(0);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.getOfferById(0)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  ----------------------------  GET OFFERS UCC  -------------------------------  //

  @DisplayName("Test getOffers with non offer returned")
  @Test
  public void testGetOffersWithEmptyListOfOffersReturned() {
    Mockito.when(offerDAO.getAll("", 0, "", "", ""))
        .thenReturn(new ArrayList<>());
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC
            .getOffers("", 0, "", "", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("Test getOffers with all offers returned")
  @Test
  public void testGetOffersWithAllOffers() {
    OfferDTO offerDTO1 = getNewOffer();
    offerDTO1.setIdOffer(4);
    offerDTO1.getObject().setIdObject(55);
    OfferDTO offerDTO2 = getNewOffer();
    offerDTO2.setIdOffer(5);
    offerDTO2.getObject().setIdObject(56);
    OfferDTO offerDTO3 = getNewOffer();
    offerDTO3.setIdOffer(6);
    offerDTO3.getObject().setIdObject(57);
    offerDTO3.getObject().setIdOfferor(33);

    List<OfferDTO> offerDTOS = List.of(offerDTO1, offerDTO2, offerDTO3);

    Mockito.when(offerDAO.getAll("", 0, "", "", "")).thenReturn(offerDTOS);
    assertAll(
        () -> assertEquals(offerDTOS, offerUCC.getOffers("", 0, "", "", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("Test getOffers with all offers returned correspondent to a research")
  @Test
  public void testGetOffersWithAllOffersWithGivenStatusSearch() {
    OfferDTO offerDTO1 = getNewOffer();
    offerDTO1.setIdOffer(4);
    offerDTO1.getObject().setIdObject(55);
    offerDTO1.getObject().setStatus("given");
    OfferDTO offerDTO2 = getNewOffer();
    offerDTO2.setIdOffer(5);
    offerDTO2.getObject().setIdObject(56);
    offerDTO2.getObject().setStatus("given");
    OfferDTO offerDTO3 = getNewOffer();
    offerDTO3.setIdOffer(6);
    offerDTO3.getObject().setIdObject(57);
    offerDTO3.getObject().setStatus("available");
    offerDTO3.getObject().setIdOfferor(33);

    List<OfferDTO> offerDTOS = List.of(offerDTO1, offerDTO2);

    Mockito.when(offerDAO.getAll("given", 0, "", "", "")).thenReturn(offerDTOS);
    assertAll(
        () -> assertEquals(offerDTOS, offerUCC.getOffers("given", 0, "", "", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("Test getOffers with all offers of someone for a research")
  @Test
  public void testGetOffersWithAMemberOffersWithGivenStatusSearch() {
    OfferDTO offerDTO1 = getNewOffer();
    offerDTO1.setIdOffer(4);
    offerDTO1.getObject().setIdObject(55);
    offerDTO1.getObject().setStatus("given");
    offerDTO1.getObject().setIdOfferor(13);

    OfferDTO offerDTO2 = getNewOffer();
    offerDTO2.setIdOffer(5);
    offerDTO2.getObject().setIdObject(56);
    offerDTO2.getObject().setStatus("given");
    offerDTO2.getObject().setIdOfferor(13);

    OfferDTO offerDTO3 = getNewOffer();
    offerDTO3.setIdOffer(6);
    offerDTO3.getObject().setIdObject(57);
    offerDTO3.getObject().setStatus("available");
    offerDTO3.getObject().setIdOfferor(10);

    List<OfferDTO> offerDTOS = List.of(offerDTO1, offerDTO2);

    Mockito.when(offerDAO.getAll("given", 13, "", "", "")).thenReturn(offerDTOS);
    assertAll(
        () -> assertEquals(offerDTOS, offerUCC.getOffers("given", 13, "", "", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  //  ----------------------------  UPDATE OFFER UCC  -------------------------------  //

  @DisplayName("Test updateOffer with getOneOffer returning null from dao")
  @Test
  public void testUpdateOfferWithGetOneReturningNullFromDAO() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(1);
    offerDTO.setVersion(1);
    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.updateOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer with not same version offer")
  @Test
  public void testUpdateOfferWithNotSameVersionOffer() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(1);
    offerDTO.setVersion(1);
    offerDTO.setVersion(11);
    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(1);
    offerDTOFromDao.setVersion(1);
    offerDTOFromDao.setVersion(13);
    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.updateOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer success with object not updated")
  @Test
  public void testUpdateOfferSuccessWithObjectNotUpdated() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(1);
    offerDTO.setVersion(1);
    offerDTO.setVersion(13);
    offerDTO.setObject(null);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(1);
    offerDTOFromDao.setVersion(1);
    offerDTOFromDao.setVersion(13);
    offerDTOFromDao.setObject(null);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);
    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertEquals(offerDTOFromDao, offerUCC.updateOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test updateOffer with not same object offer version")
  @Test
  public void testUpdateOfferWithNotSameObjectOfferVersion() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(1);
    offerDTO.setVersion(1);
    offerDTO.setVersion(13);
    offerDTO.getObject().setVersion(15);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(1);
    offerDTOFromDao.setVersion(1);
    offerDTOFromDao.setVersion(13);
    offerDTOFromDao.getObject().setVersion(17);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);
    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.updateOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer success with object updated")
  @Test
  public void testUpdateOfferSuccessWithObjectUpdated() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(1);
    offerDTO.setVersion(1);
    offerDTO.setVersion(13);
    offerDTO.getObject().setIdObject(11);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(1);
    offerDTOFromDao.setVersion(1);
    offerDTOFromDao.setVersion(13);
    offerDTOFromDao.getObject().setIdObject(12);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);
    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDao);
    Mockito.when(objectDAO.updateOne(offerDTO.getObject())).thenReturn(offerDTOFromDao.getObject());

    assertAll(
        () -> assertEquals(offerDTOFromDao, offerUCC.updateOffer(offerDTO)),
        () -> assertEquals(offerDTO.getObject().getIdObject(),
            offerUCC.updateOffer(offerDTO).getObject().getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------  CANCEL OFFER UCC  -------------------------------  //

  @DisplayName("Test cancelOffer with non existent offer")
  @Test
  public void testCancelOfferWithNonExistentOffer() {
    OfferDTO mockOfferDTO = getNewOffer();
    mockOfferDTO.setIdOffer(2);
    mockOfferDTO.setStatus("given");
    mockOfferDTO.getObject().setIdOfferor(2);

    MemberDTO mockMember = memberFactory.getMemberDTO();
    mockMember.setMemberId(2);

    Mockito.when(offerDAO.getOne(mockOfferDTO.getIdOffer())).thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> offerUCC.cancelOffer(mockOfferDTO, mockMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test cancelOffer with not same id member version")
  @Test
  public void testCancelOfferWithNotSameIdMemberVersion() {
    OfferDTO mockOfferDTO = getNewOffer();
    mockOfferDTO.setIdOffer(2);
    mockOfferDTO.setStatus("available");
    mockOfferDTO.getObject().setIdOfferor(2);

    MemberDTO mockMember = memberFactory.getMemberDTO();
    mockMember.setMemberId(3);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(2);
    offerDTOFromDao.setStatus("available");
    offerDTOFromDao.getObject().setIdOfferor(2);

    Mockito.when(offerDAO.getOne(mockOfferDTO.getIdOffer())).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.cancelOffer(mockOfferDTO, mockMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test cancelOffer with given status")
  @Test
  public void testCancelOfferWithGivenStatus() {
    OfferDTO mockOfferDTO = getNewOffer();
    MemberDTO mockMember = memberFactory.getMemberDTO();

    mockOfferDTO.setIdOffer(2);
    mockOfferDTO.setStatus("given");
    mockMember.setMemberId(2);
    mockOfferDTO.getObject().setIdOfferor(2);

    Mockito.when(offerDAO.getOne(mockOfferDTO.getIdOffer())).thenReturn(mockOfferDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.cancelOffer(mockOfferDTO, mockMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test cancelOffer with given status")
  @Test
  public void testCancelOfferWithCancelledStatus() {
    OfferDTO mockOfferDTO = getNewOffer();
    MemberDTO mockMember = memberFactory.getMemberDTO();

    mockOfferDTO.setIdOffer(2);
    mockOfferDTO.setStatus("cancelled");
    mockMember.setMemberId(2);
    mockOfferDTO.getObject().setIdOfferor(2);

    Mockito.when(offerDAO.getOne(mockOfferDTO.getIdOffer())).thenReturn(mockOfferDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.cancelOffer(mockOfferDTO, mockMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test cancelOffer with not same offer version")
  @Test
  public void testCancelOfferWithNotSameOfferVersion() {
    OfferDTO mockOfferDTO = getNewOffer();
    mockOfferDTO.setIdOffer(2);
    mockOfferDTO.setStatus("available");
    mockOfferDTO.getObject().setIdOfferor(2);
    mockOfferDTO.setVersion(6);

    MemberDTO mockMember = memberFactory.getMemberDTO();
    mockMember.setMemberId(2);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(2);
    offerDTOFromDao.setStatus("available");
    offerDTOFromDao.getObject().setIdOfferor(2);
    offerDTOFromDao.setVersion(9);

    Mockito.when(offerDAO.getOne(mockOfferDTO.getIdOffer())).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.cancelOffer(mockOfferDTO, mockMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test cancelOffer with not same object offer version")
  @Test
  public void testCancelOfferWithNotSameObjectOfferVersion() {
    OfferDTO mockOfferDTO = getNewOffer();
    mockOfferDTO.setIdOffer(2);
    mockOfferDTO.setStatus("available");
    mockOfferDTO.getObject().setIdOfferor(2);
    mockOfferDTO.setVersion(6);
    mockOfferDTO.getObject().setVersion(16);

    MemberDTO mockMember = memberFactory.getMemberDTO();
    mockMember.setMemberId(2);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(2);
    offerDTOFromDao.setStatus("available");
    offerDTOFromDao.getObject().setIdOfferor(2);
    offerDTOFromDao.setVersion(6);
    offerDTOFromDao.getObject().setVersion(13);

    Mockito.when(offerDAO.getOne(mockOfferDTO.getIdOffer())).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.cancelOffer(mockOfferDTO, mockMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test cancelOffer success without interest assigned")
  @Test
  public void testCancelOfferSuccessWithoutInterestAssigned() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setStatus("available");
    offerDTO.getObject().setStatus("available");
    offerDTO.getObject().setIdOfferor(5);

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setStatus("cancelled");

    MemberDTO mockMember = memberFactory.getMemberDTO();
    mockMember.setMemberId(5);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);
    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDAO);
    Mockito.when(objectDAO.updateOne(offerDTO.getObject()))
        .thenReturn(offerDTO.getObject());
    Mockito.when(interestDAO.getAssignedInterest(
            offerDTOFromDAO.getObject().getIdObject()))
        .thenReturn(null);

    OfferDTO offerDTOUpdated = offerUCC.cancelOffer(offerDTO, mockMember);
    assertAll(
        () -> assertEquals("cancelled", offerDTOUpdated.getStatus()),
        () -> assertEquals("cancelled", offerDTOUpdated.getObject().getStatus()),
        () -> assertEquals(offerDTOUpdated, offerDTOFromDAO),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test cancelOffer success with interest assigned")
  @Test
  public void testCancelOfferSuccessWithInterestAssigned() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setStatus("available");
    offerDTO.getObject().setStatus("available");

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setObject(offerDTO.getObject());
    offerDTOFromDAO.setStatus("cancelled");
    offerDTOFromDAO.getObject().setStatus("cancelled");

    MemberDTO mockMember = memberFactory.getMemberDTO();
    mockMember.setMemberId(5);
    offerDTO.getObject().setIdOfferor(5);
    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIdObject(offerDTOFromDAO.getIdOffer());
    interestDTO.setIdMember(mockMember.getMemberId());

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);
    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDAO);
    Mockito.when(objectDAO.updateOne(offerDTO.getObject()))
        .thenReturn(offerDTO.getObject());
    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);
    Mockito.when(objectDAO.getOne(interestDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO.getObject());
    Mockito.when(memberDAO.getOne(interestDTO.getIdMember()))
        .thenReturn(mockMember);

    OfferDTO offerDTOUpdated = offerUCC.cancelOffer(offerDTO, mockMember);
    assertAll(
        () -> assertEquals("cancelled", offerDTOUpdated.getStatus()),
        () -> assertEquals("cancelled", offerDTOUpdated.getObject().getStatus()),
        () -> assertEquals(offerDTOUpdated, offerDTOFromDAO),
        () -> assertEquals("published", interestDTO.getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------  GET GIVEN OFFERS UCC  -------------------------------  //

  @DisplayName("Test getGivenOffers with empty list of offers")
  @Test
  public void testGetGivenOffersWithEmptyListOffers() {
    List<OfferDTO> listOffers = new ArrayList<>();
    Mockito.when(offerDAO.getAllGivenOffers(2)).thenReturn(listOffers);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> offerUCC.getGivenOffers(2)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getGivenOffers success")
  @Test
  public void testGetGivenOffersSuccess() {
    OfferDTO offerGiven = getNewOffer();
    offerGiven.setStatus("given");
    offerGiven.getObject().setStatus("given");
    OfferDTO offerAvailable = getNewOffer();
    offerAvailable.setStatus("available");
    offerAvailable.getObject().setStatus("available");

    List<OfferDTO> listOffers = new ArrayList<>();
    listOffers.add(offerGiven);
    Mockito.when(offerDAO.getAllGivenOffers(2)).thenReturn(listOffers);

    assertAll(
        () -> assertEquals(1, offerDAO.getAllGivenOffers(2).size()),
        () -> assertTrue(offerUCC.getGivenOffers(2).contains(offerGiven)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- GIVE OFFER UCC  -------------------------------  //
  @DisplayName("Test giveOffer with non existent offer published")
  @Test
  public void testGiveOfferWithNonExistentOfferPublished() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject())).thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.giveOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test giveOffer with not same version offer in db and front")
  @Test
  public void testGiveOfferWithNotSameVersionOfferDbAndFrom() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);
    offerDTO.setVersion(1);
    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(3);
    offerDTOFromDao.getObject().setIdObject(3);
    offerDTOFromDao.getObject().setIdOfferor(2);
    offerDTOFromDao.setVersion(2);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.giveOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test giveOffer with not same version object in db and front")
  @Test
  public void testGiveOfferWithNotSameVersionObjectDbAndFrom() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);
    offerDTO.getObject().setVersion(2);
    offerDTO.setVersion(2);
    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(3);
    offerDTOFromDao.getObject().setIdObject(3);
    offerDTOFromDao.getObject().setIdOfferor(2);
    offerDTOFromDao.setVersion(2);
    offerDTOFromDao.getObject().setVersion(5);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.giveOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test giveOffer with not same member id in param and id offer of object")
  @Test
  public void testGiveOfferWithNotSameMemberIdParamAndIdOfferor() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);
    offerDTO.getObject().setVersion(5);
    offerDTO.setVersion(2);
    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(7);
    offerDTOFromDao.getObject().setIdObject(3);
    offerDTOFromDao.getObject().setIdOfferor(13);
    offerDTOFromDao.setVersion(2);
    offerDTOFromDao.getObject().setVersion(5);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.giveOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test giveOffer without having interest")
  @Test
  public void testGiveOfferWithoutHavingAnyInterest() {
    OfferDTO offerDTO = getNewOffer();
    MemberDTO memberDTO = memberFactory.getMemberDTO();

    memberDTO.setMemberId(2);
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.giveOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test giveOffer with offer that is not assigned")
  @Test
  public void testGiveOfferThatIsNotAssigned() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.setStatus("cancelled");

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(3);
    offerDTOFromDAO.getObject().setIdObject(3);
    offerDTOFromDAO.setStatus("cancelled");

    ObjectDTO objectDTO = offerDTO.getObject();

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIdMember(3);
    interestDTO.setObject(objectDTO);
    interestDTO.setStatus("published");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    offerDTO.getObject().setIdOfferor(2);
    memberDTO.setMemberId(2);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);

    Mockito.when(offerDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO);

    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.giveOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test giveOffer success")
  @Test
  public void testGiveOfferSuccess() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.setStatus("assigned");

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(3);
    offerDTOFromDAO.getObject().setIdObject(3);
    offerDTOFromDAO.setStatus("assigned");

    ObjectDTO objectDTO = offerDTO.getObject();

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIdObject(objectDTO.getIdObject());
    interestDTO.setIdMember(3);
    interestDTO.setObject(objectDTO);
    interestDTO.setStatus("published");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    offerDTO.getObject().setIdOfferor(2);
    offerDTOFromDAO.getObject().setIdOfferor(2);
    memberDTO.setMemberId(2);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);

    Mockito.when(offerDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO);

    Mockito.when(objectDAO.updateOne(offerDTOFromDAO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());

    Mockito.when(offerDAO.updateOne(offerDTOFromDAO))
        .thenReturn(offerDTOFromDAO);

    OfferDTO offerDTOUpdated = offerUCC.giveOffer(offerDTO, memberDTO);

    assertAll(
        () -> assertEquals("received", interestDTO.getStatus()),
        () -> assertEquals("given", offerDTOUpdated.getStatus()),
        () -> assertEquals("given", offerDTOUpdated.getObject().getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- ADD OBJECT UCC  -------------------------------  //


  @DisplayName("Test addObject with an existent object type")
  @Test
  public void testAddObjectSuccessWithExistentType() {
    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setIdType(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);
    offerDTOFromDAO.getObject().setType(typeDTOFromDaoGetOne);

    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(objectDAO.addOne(offerDTO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    OfferDTO offerFromAdd = offerUCC.addObject(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertEquals(offerDTO.getObject().getType(),
            offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }


  @DisplayName("Test addObject an existent object type and then the object offer added in the DB")
  @Test
  public void testAddObjectSuccessWithExistentTypeAndAddOneObject() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setIdType(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(0);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    Mockito.when(objectDAO.addOne(offerDTO.getObject())).thenReturn(objectDTO);

    OfferDTO offerFromAdd = offerUCC.addObject(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addObject with an id type and type name empty")
  @Test
  public void testAddObjectWithEmptyTypeNameOfOfferType() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setIdType(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().getType().setTypeName("");
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getIdType()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(objectDAO.addOne(offerDTO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    OfferDTO offerFromAdd = offerUCC.addObject(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }


  @DisplayName("Test addObject with an id type and type name null")
  @Test
  public void testAddObjectWithNullTypeNameOfOfferType() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setIdType(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().getType().setTypeName(null);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getIdType()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(objectDAO.addOne(offerDTO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    OfferDTO offerFromAdd = offerUCC.addObject(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addObject with a non existent object type and then added in the DB")
  @Test
  public void testAddObjectWithNonExistentTypeAndAddOneTypeReturnsANewType() {

    TypeDTO typeDTOFromDaoAddOne = typeFactory.getTypeDTO();
    typeDTOFromDaoAddOne.setIdType(5);
    typeDTOFromDaoAddOne.setTypeName("Jouets");
    typeDTOFromDaoAddOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(null);
    Mockito.when(typeDAO.addOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoAddOne);
    Mockito.when(objectDAO.addOne(offerDTO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());

    OfferDTO offerFromAdd = offerUCC.addObject(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addObject receiving fatal exception from DAO")
  @Test
  public void testAddObjectFatalExceptionInInsertion() {
    OfferDTO offerDTO = getNewOffer();
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(null);
    Mockito.when(typeDAO.addOne(offerDTO.getObject().getType().getTypeName())).thenThrow(
        FatalException.class);

    assertAll(
        () -> assertThrows(FatalException.class, () -> offerUCC.addObject(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  ---------------------------- NOT COLLECTED OFFER UCC  -------------------------------  //

  @DisplayName("Test notCollectedOffer with null received from dao when get the offer")
  @Test
  public void testNotCollectedOfferWithNullReceivedFromDAOByGetOneOffer() {
    OfferDTO offerDTO = getNewOffer();
    MemberDTO memberDTO = memberFactory.getMemberDTO();

    memberDTO.setMemberId(2);
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> offerUCC.notCollectedOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test notCollectedOffer with not same member id param and if offeror of object")
  @Test
  public void testNotCollectedOfferWithNotSameMemberIdParamAndIdOfferorOfOffer() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(3);
    offerDTOFromDao.getObject().setIdObject(3);
    offerDTOFromDao.getObject().setIdOfferor(5);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.notCollectedOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test notCollectedOffer with not same version offer param and of the db")
  @Test
  public void testNotCollectedOfferWithNotSameVersionOfferParamAndOfDB() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);
    offerDTO.setVersion(6);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(3);
    offerDTOFromDao.getObject().setIdObject(3);
    offerDTOFromDao.getObject().setIdOfferor(2);
    offerDTOFromDao.setVersion(3);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.notCollectedOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test notCollectedOffer with not same version object of offer param and of the db")
  @Test
  public void testNotCollectedOfferWithNotSameVersionObjectOfferParamAndOfDB() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);
    offerDTO.setVersion(3);
    offerDTO.getObject().setVersion(4);

    OfferDTO offerDTOFromDao = getNewOffer();
    offerDTOFromDao.setIdOffer(3);
    offerDTOFromDao.getObject().setIdObject(3);
    offerDTOFromDao.getObject().setIdOfferor(2);
    offerDTOFromDao.setVersion(3);
    offerDTOFromDao.getObject().setVersion(9);

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTOFromDao);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.notCollectedOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test notCollectedOffer without having interest")
  @Test
  public void testNotCollectedOfferWithoutHavingAnyInterest() {
    OfferDTO offerDTO = getNewOffer();
    MemberDTO memberDTO = memberFactory.getMemberDTO();

    memberDTO.setMemberId(2);
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(2);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> offerUCC.notCollectedOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test notCollectedOffer with offer that is not assigned")
  @Test
  public void testNotCollectedOfferThatIsNotAssigned() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.setStatus("cancelled");

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(3);
    offerDTOFromDAO.getObject().setIdObject(3);
    offerDTOFromDAO.setStatus("cancelled");

    ObjectDTO objectDTO = offerDTO.getObject();

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIdMember(3);
    interestDTO.setObject(objectDTO);
    interestDTO.setStatus("published");

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);
    offerDTO.getObject().setIdOfferor(2);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);

    Mockito.when(offerDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> offerUCC.notCollectedOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }


  @DisplayName("Test notCollectedOffer success")
  @Test
  public void testNotCollectedOfferSuccess() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);
    offerDTO.setStatus("assigned");
    offerDTO.getObject().setIdOfferor(2);

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(3);
    offerDTOFromDAO.getObject().setIdObject(3);
    offerDTOFromDAO.setStatus("assigned");
    offerDTOFromDAO.getObject().setIdOfferor(2);

    ObjectDTO objectDTO = offerDTO.getObject();
    offerDTOFromDAO.setObject(objectDTO);

    InterestDTO interestDTO = interestFactory.getInterestDTO();
    interestDTO.setIdMember(3);
    interestDTO.setObject(objectDTO);
    interestDTO.setStatus("published");
    interestDTO.setIdObject(objectDTO.getIdObject());

    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);

    Mockito.when(offerDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO);

    Mockito.when(objectDAO.getOne(interestDTO.getIdObject())).thenReturn(objectDTO);

    Mockito.when(memberDAO.getOne(interestDTO.getIdMember())).thenReturn(memberDTO);

    Mockito.when(objectDAO.updateOne(offerDTOFromDAO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());

    Mockito.when(offerDAO.updateOne(offerDTOFromDAO)).thenReturn(offerDTOFromDAO);

    OfferDTO offerDTOUpdated = offerUCC.notCollectedOffer(offerDTO, memberDTO);

    assertAll(
        () -> assertEquals("not_collected", interestDTO.getStatus()),
        () -> assertEquals("not_collected", offerDTOUpdated.getStatus()),
        () -> assertEquals("not_collected", offerDTOUpdated.getObject().getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- GET OFFERS COUNT UCC  -------------------------------  //

  @DisplayName("Test getOffersCount with non existent member")
  @Test
  public void testGetOffersCountNonExistentMember() {
    Mockito.when(offerDAO.getOffersCount(0)).thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.getOffersCount(0)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getOffersCount with existent member")
  @Test
  public void testGetOffersCountExistentMember() {
    Map<String, Integer> map = new HashMap<>();
    map.put("nbReceived", 0);
    map.put("nbNotCollected", 3);
    map.put("nbGiven", 0);
    map.put("nbOffers", 0);
    Mockito.when(offerDAO.getOffersCount(3)).thenReturn(map);

    assertAll(
        () -> assertEquals(map, offerUCC.getOffersCount(3)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- GET LAST OFFER UCC  -------------------------------  //

  @DisplayName("Test getLastOffer with none offer returned from dao")
  @Test
  public void testGetLastOfferWithNoneOfferReturnedFromDao() {
    int idObject = 3;
    Mockito.when(offerDAO.getLastObjectOffer(idObject)).thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.getLastOffer(idObject)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getLastOffer success")
  @Test
  public void testGetLastOfferSuccess() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);

    assertAll(
        () -> assertEquals(offerDTO, offerUCC.getLastOffer(offerDTO.getObject().getIdObject())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------------- ADD OFFER UCC  -------------------------------  //

  @DisplayName("Test addOffer with no one existing offer")
  @Test
  public void testAddOfferWithNoOneExistingOffer() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.addOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test addOffer with not same id member of object and id offeror")
  @Test
  public void testAddOfferWithNotSameIdMemberAndIfOfferor() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(15);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.addOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test addOffer with already an existing offer that is not cancelled/not collected")
  @Test
  public void testAddOfferWithAlreadyAnExistingOfferThatIsNotCancelledNorNotCollected() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(13);
    offerDTO.setStatus("available");
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.addOffer(offerDTO, memberDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test addOffer with none interests")
  @Test
  public void testAddOfferSuccessWithNoneInterests() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(13);
    offerDTO.setStatus("cancelled");
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);
    Mockito.when(interestDAO.getAllCount(offerDTO.getObject().getIdObject()))
        .thenReturn(0);
    Mockito.when(offerDAO.addOne(offerDTO))
        .thenReturn(offerDTO);

    OfferDTO offerDTOAdded = offerUCC.addOffer(offerDTO, memberDTO);

    assertAll(
        () -> assertEquals("available",
            offerDTOAdded.getObject().getStatus()),
        () -> assertEquals("available",
            offerDTOAdded.getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with 5 interests")
  @Test
  public void testAddOfferSuccessWith5Interests() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(13);
    offerDTO.setStatus("cancelled");
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);
    Mockito.when(interestDAO.getAllCount(offerDTO.getObject().getIdObject()))
        .thenReturn(5);
    Mockito.when(offerDAO.addOne(offerDTO))
        .thenReturn(offerDTO);

    OfferDTO offerDTOAdded = offerUCC.addOffer(offerDTO, memberDTO);

    assertAll(
        () -> assertEquals("interested",
            offerDTOAdded.getObject().getStatus()),
        () -> assertEquals("interested",
            offerDTOAdded.getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with 5 interests V2")
  @Test
  public void testAddOfferSuccessWith5InterestsV2() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(3);
    offerDTO.getObject().setIdOfferor(13);
    offerDTO.setStatus("not_collected");
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerDTO);
    Mockito.when(interestDAO.getAllCount(offerDTO.getObject().getIdObject()))
        .thenReturn(5);
    Mockito.when(offerDAO.addOne(offerDTO))
        .thenReturn(offerDTO);

    OfferDTO offerDTOAdded = offerUCC.addOffer(offerDTO, memberDTO);

    assertAll(
        () -> assertEquals("interested",
            offerDTOAdded.getObject().getStatus()),
        () -> assertEquals("interested",
            offerDTOAdded.getStatus()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ---------------------- GET Given AND ASSIGNED Offers UCC  --------------------------  //

  @DisplayName("Test getGivenAndAssignedOffers with empty list")
  @Test
  public void testGetGivenAndAssignedOffersWithAnEmptyList() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getAllGivenAndAssignedOffers(memberDTO.getMemberId(), ""))
        .thenReturn(new ArrayList<>());

    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> offerUCC.getGivenAndAssignedOffers(memberDTO, "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getGivenAndAssignedOffers success")
  @Test
  public void testGetGivenAndAssignedOffersSuccess() {
    OfferDTO offerDTO = offerFactory.getOfferDTO();
    ArrayList<OfferDTO> listOfOffers = new ArrayList<>();
    listOfOffers.add(offerDTO);
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(13);
    Mockito.when(offerDAO.getAllGivenAndAssignedOffers(memberDTO.getMemberId(), ""))
        .thenReturn(listOfOffers);

    List<OfferDTO> listFromDao = offerUCC.getGivenAndAssignedOffers(memberDTO, "");

    assertAll(
        () -> assertTrue(listFromDao.contains(offerDTO)),
        () -> assertEquals(listFromDao, listOfOffers),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }
}