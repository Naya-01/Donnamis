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
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
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
    typeDTO.setId(1);

    ObjectDTO objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(3);
    objectDTO.setType(typeDTO);

    OfferDTO offerDTO = offerFactory.getOfferDTO();
    offerDTO.setDate(LocalDate.now());
    offerDTO.setIdOffer(0);
    offerDTO.setObject(objectDTO);
    return offerDTO;
  }

  @BeforeEach
  void setUp() {
    this.mockDalService = locator.getService(DALService.class);
    this.offerDAO = locator.getService(OfferDAO.class);
    this.typeDAO = locator.getService(TypeDAO.class);
    this.objectDAO = locator.getService(ObjectDAO.class);
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

  //  ----------------------------  ADD OFFER UCC  -------------------------------  //

  @DisplayName("Test addOffer with an existent object type")
  @Test
  public void testAddOfferSuccessWithExistentType() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);
    OfferDTO offerStatus = getNewOffer();
    offerStatus.setStatus("cancelled");
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerStatus);
    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }


  @DisplayName("Test addOffer an existent object type and then the object offer added in the DB")
  @Test
  public void testAddOfferSuccessWithExistentTypeAndAddOneObject() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().setIdObject(0);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    ObjectDTO objectDTO = Mockito.mock(ObjectDTO.class);
    Mockito.when(objectDAO.addOne(offerDTO.getObject())).thenReturn(objectDTO);

    OfferDTO offerStatus = getNewOffer();
    offerStatus.setStatus("cancelled");
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerStatus);
    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with an id type and type name empty")
  @Test
  public void testAddOfferWithEmptyTypeNameOfOfferType() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().getType().setTypeName("");
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getIdType()))
        .thenReturn(typeDTOFromDaoGetOne);

    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);
    OfferDTO offerStatus = getNewOffer();
    offerStatus.setStatus("cancelled");
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerStatus);
    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with an id type and type name null")
  @Test
  public void testAddOfferWithNullTypeNameOfOfferType() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().getType().setTypeName(null);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getIdType()))
        .thenReturn(typeDTOFromDaoGetOne);

    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    OfferDTO offerStatus = getNewOffer();
    offerStatus.setStatus("cancelled");
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerStatus);
    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with a non existent object type and then added in the DB")
  @Test
  public void testAddOfferWithNonExistentTypeAndAddOneTypeReturnsANewType() {

    TypeDTO typeDTOFromDaoAddOne = typeFactory.getTypeDTO();
    typeDTOFromDaoAddOne.setId(5);
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

    OfferDTO offerStatus = getNewOffer();
    offerStatus.setStatus("cancelled");
    Mockito.when(offerDAO.getLastObjectOffer(offerDTO.getObject().getIdObject()))
        .thenReturn(offerStatus);
    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------  GET OFFERS UCC  -------------------------------  //

  @DisplayName("Test getOffers with non offer returned")
  @Test
  public void testGetOffersWithEmptyListOfOffersReturned() {
    Mockito.when(offerDAO.getAll("", 0, "", ""))
        .thenReturn(new ArrayList<>());
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC
            .getOffers("", 0, "", "")),
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

    Mockito.when(offerDAO.getAll("", 0, "", "")).thenReturn(offerDTOS);
    assertAll(
        () -> assertEquals(offerDTOS, offerUCC.getOffers("", 0, "", "")),
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

    Mockito.when(offerDAO.getAll("given", 0, "", "")).thenReturn(offerDTOS);
    assertAll(
        () -> assertEquals(offerDTOS, offerUCC.getOffers("given", 0, "", "")),
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

    Mockito.when(offerDAO.getAll("given", 13, "", "")).thenReturn(offerDTOS);
    assertAll(
        () -> assertEquals(offerDTOS, offerUCC.getOffers("given", 13, "", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  //  ----------------------------  UPDATE OFFER UCC  -------------------------------  //

  @DisplayName("Test updateOffer with the fields of the offers empty")
  @Test
  public void testUpdateOfferWithEmptyFields() {
    OfferDTO mockOfferDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(offerDAO.updateOne(mockOfferDTO)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.updateOffer(mockOfferDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer with a non existent id offer")
  @Test
  public void testUpdateOfferNotExistentIdOffer() {
    OfferDTO mockOfferDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(mockOfferDTO.getIdOffer()).thenReturn(0);

    Mockito.when(offerDAO.updateOne(mockOfferDTO)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.updateOffer(mockOfferDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer success")
  @Test
  public void testUpdateOfferSuccess() {
    OfferDTO mockOfferDTO = getNewOffer();
    mockOfferDTO.setIdOffer(15);
    mockOfferDTO.getObject().setDescription("Très bon jeu");
    mockOfferDTO.getObject().setStatus("available");
    mockOfferDTO.setDate(LocalDate.now().minusMonths(2));

    OfferDTO mockOfferDTOUpdated = getNewOffer();
    mockOfferDTOUpdated.setIdOffer(15);
    mockOfferDTOUpdated.getObject().setDescription("Très bon jeu");
    mockOfferDTOUpdated.getObject().setStatus("available");
    mockOfferDTOUpdated.setDate(LocalDate.now());

    Mockito.when(offerDAO.updateOne(mockOfferDTO)).thenReturn(mockOfferDTOUpdated);

    OfferDTO offerDTO = offerUCC.updateOffer(mockOfferDTO);

    assertAll(
        () -> assertNotEquals(mockOfferDTO, offerDTO),
        () -> assertNotEquals(mockOfferDTO.getDate(), offerDTO.getDate()),
        () -> assertEquals(mockOfferDTO.getIdOffer(), offerDTO.getIdOffer()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  ----------------------------  CANCEL OFFER UCC  -------------------------------  //

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

  @DisplayName("Test cancelOffer success without interest assigned")
  @Test
  public void testCancelOfferSuccessWithoutInterestAssigned() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setStatus("available");
    offerDTO.getObject().setStatus("available");

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setStatus("cancelled");
    offerDTOFromDAO.getObject().setStatus("cancelled");

    MemberDTO mockMember = memberFactory.getMemberDTO();

    mockMember.setMemberId(5);
    offerDTO.getObject().setIdOfferor(5);

    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDAO);
    Mockito.when(objectDAO.updateOne(offerDTOFromDAO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());
    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);
    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);

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
    offerDTOFromDAO.setStatus("cancelled");
    offerDTOFromDAO.getObject().setStatus("cancelled");

    InterestDTO interestDTO = interestFactory.getInterestDTO();

    MemberDTO mockMember = memberFactory.getMemberDTO();

    mockMember.setMemberId(5);
    offerDTO.getObject().setIdOfferor(5);

    Mockito.when(offerDAO.updateOne(offerDTO)).thenReturn(offerDTOFromDAO);
    Mockito.when(objectDAO.updateOne(offerDTOFromDAO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());
    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);
    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);

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

  @DisplayName("Test giveOffer without having interest")
  @Test
  public void testGiveOfferWithoutHavingAnyInterest() {
    OfferDTO offerDTO = getNewOffer();
    offerDTO.setIdOffer(3);
    offerDTO.getObject().setIdObject(3);

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.giveOffer(offerDTO)),
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

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);

    Mockito.when(offerDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> offerUCC.giveOffer(offerDTO)),
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
    interestDTO.setIdMember(3);
    interestDTO.setObject(objectDTO);
    interestDTO.setStatus("published");

    Mockito.when(interestDAO.getAssignedInterest(offerDTO.getObject().getIdObject()))
        .thenReturn(interestDTO);

    Mockito.when(offerDAO.getLastObjectOffer(objectDTO.getIdObject()))
        .thenReturn(offerDTOFromDAO);

    Mockito.when(objectDAO.updateOne(offerDTOFromDAO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());

    Mockito.when(offerDAO.updateOne(offerDTOFromDAO))
        .thenReturn(offerDTOFromDAO);

    OfferDTO offerDTOUpdated = offerUCC.giveOffer(offerDTO);

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
    typeDTOFromDaoGetOne.setId(5);
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
    typeDTOFromDaoGetOne.setId(5);
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
    typeDTOFromDaoGetOne.setId(5);
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
    typeDTOFromDaoGetOne.setId(5);
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
    typeDTOFromDaoAddOne.setId(5);
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

  //  ---------------------------- NOT COLLECTED OFFER UCC  -------------------------------  //

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

    OfferDTO offerDTOFromDAO = getNewOffer();
    offerDTOFromDAO.setIdOffer(3);
    offerDTOFromDAO.getObject().setIdObject(3);
    offerDTOFromDAO.setStatus("assigned");

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

    Mockito.when(objectDAO.updateOne(offerDTOFromDAO.getObject()))
        .thenReturn(offerDTOFromDAO.getObject());

    Mockito.when(offerDAO.updateOne(offerDTOFromDAO))
        .thenReturn(offerDTOFromDAO);

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer())).thenReturn(offerDTO);

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
}