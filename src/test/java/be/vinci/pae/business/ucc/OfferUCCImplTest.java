package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.FatalException;
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

class OfferUCCImplTest {

  private DALService mockDalService;
  private OfferDAO offerDAO;
  private TypeDAO typeDAO;
  private ObjectDAO objectDAO;
  private OfferUCC offerUCC;
  private TypeFactory typeFactory;


  private OfferDTO getNewOffer() {
    TypeDTO typeDTO = typeFactory.getTypeDTO();
    typeDTO.setTypeName("Jouets");
    typeDTO.setId(1);

    ObjectDTO objectDTO = Mockito.mock(ObjectDTO.class);
    Mockito.when(objectDTO.getIdObject()).thenReturn(3);
    Mockito.when(objectDTO.getType()).thenReturn(typeDTO);

    OfferDTO offerDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(offerDTO.getDate()).thenReturn(LocalDate.now());
    Mockito.when(offerDTO.getIdOffer()).thenReturn(0);
    Mockito.when(offerDTO.getObject()).thenReturn(objectDTO);
    return offerDTO;
  }

  @BeforeEach
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.mockDalService = locator.getService(DALService.class);
    this.offerDAO = locator.getService(OfferDAO.class);
    this.typeDAO = locator.getService(TypeDAO.class);
    this.objectDAO = locator.getService(ObjectDAO.class);
    this.offerUCC = locator.getService(OfferUCC.class);
    this.typeFactory = locator.getService(TypeFactory.class);
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

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer(),false)).thenReturn(offerDTO);
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

    Mockito.when(offerDAO.getOne(offerDTO.getIdOffer(),false)).thenReturn(null);
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
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with an object type existent and insertion of offer failed")
  @Test
  public void testAddOfferWithExistentTypeAndAddOneOfferReturnsNull() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(null);

    assertAll(
        () -> assertThrows(FatalException.class, () -> offerUCC.addOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }


  @DisplayName("Test addOffer with an existent object type and the object non added in the DB")
  @Test
  public void testAddOfferWithExistentTypeAndAddOneObjectReturnsNull() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDTO.getObject().getIdObject()).thenReturn(0);
    Mockito.when(objectDAO.addOne(offerDTO.getObject())).thenReturn(null);

    assertAll(
        () -> assertThrows(FatalException.class, () -> offerUCC.addOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test addOffer an existent object type and then the object offer added in the DB")
  @Test
  public void testAddOfferSuccessWithExistentTypeAndAddOneObject() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoGetOne);
    Mockito.when(offerDTO.getObject().getIdObject()).thenReturn(0);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

    ObjectDTO objectDTO = Mockito.mock(ObjectDTO.class);
    Mockito.when(objectDAO.addOne(offerDTO.getObject())).thenReturn(objectDTO);

    OfferDTO offerFromAdd = offerUCC.addOffer(offerDTO);

    assertAll(
        () -> assertEquals(offerFromAdd, offerDTOFromDAO),
        () -> assertNotEquals(offerFromAdd.getIdOffer(), offerDTO.getIdOffer()),
        () -> assertNotEquals(offerDTO.getObject().getType(), offerFromAdd.getObject().getType()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test addOffer with a non existent object type and then not added in the DB")
  @Test
  public void testAddOfferWithNonExistentTypeAndAddOneTypeReturnsNull() {
    OfferDTO offerDTO = getNewOffer();
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(null);
    Mockito.when(typeDAO.addOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(FatalException.class, () -> offerUCC.addOffer(offerDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test addOffer with an id type and type name empty")
  @Test
  public void testAddOfferWithEmptyTypeNameOfOfferType() {
    OfferDTO offerDTOFromDAO = getNewOffer();
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().getType().setTypeName("");
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getIdType()))
        .thenReturn(typeDTOFromDaoGetOne);

    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

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
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);

    TypeDTO typeDTOFromDaoGetOne = typeFactory.getTypeDTO();
    typeDTOFromDaoGetOne.setId(5);
    typeDTOFromDaoGetOne.setTypeName("Jouets");
    typeDTOFromDaoGetOne.setIsDefault(true);

    OfferDTO offerDTO = getNewOffer();
    offerDTO.getObject().getType().setTypeName(null);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getIdType()))
        .thenReturn(typeDTOFromDaoGetOne);

    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);

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
    Mockito.when(offerDTOFromDAO.getIdOffer()).thenReturn(5);
    Mockito.when(offerDAO.addOne(offerDTO)).thenReturn(offerDTOFromDAO);
    Mockito.when(typeDAO.getOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(null);
    Mockito.when(typeDAO.addOne(offerDTO.getObject().getType().getTypeName()))
        .thenReturn(typeDTOFromDaoAddOne);

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
    Mockito.when(offerDTO1.getIdOffer()).thenReturn(4);
    Mockito.when(offerDTO1.getObject().getIdObject()).thenReturn(55);
    OfferDTO offerDTO2 = getNewOffer();
    Mockito.when(offerDTO2.getIdOffer()).thenReturn(5);
    Mockito.when(offerDTO2.getObject().getIdObject()).thenReturn(56);
    OfferDTO offerDTO3 = getNewOffer();
    Mockito.when(offerDTO3.getIdOffer()).thenReturn(6);
    Mockito.when(offerDTO3.getObject().getIdObject()).thenReturn(57);
    Mockito.when(offerDTO3.getObject().getIdOfferor()).thenReturn(33);

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
    Mockito.when(offerDTO1.getIdOffer()).thenReturn(4);
    Mockito.when(offerDTO1.getObject().getIdObject()).thenReturn(55);
    Mockito.when(offerDTO1.getObject().getStatus()).thenReturn("given");
    OfferDTO offerDTO2 = getNewOffer();
    Mockito.when(offerDTO2.getIdOffer()).thenReturn(5);
    Mockito.when(offerDTO2.getObject().getIdObject()).thenReturn(56);
    Mockito.when(offerDTO2.getObject().getStatus()).thenReturn("given");
    OfferDTO offerDTO3 = getNewOffer();
    Mockito.when(offerDTO3.getIdOffer()).thenReturn(6);
    Mockito.when(offerDTO3.getObject().getIdObject()).thenReturn(57);
    Mockito.when(offerDTO3.getObject().getIdOfferor()).thenReturn(33);
    Mockito.when(offerDTO3.getObject().getStatus()).thenReturn("available");

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
    Mockito.when(offerDTO1.getIdOffer()).thenReturn(4);
    Mockito.when(offerDTO1.getObject().getIdObject()).thenReturn(55);
    Mockito.when(offerDTO1.getObject().getStatus()).thenReturn("given");
    Mockito.when(offerDTO1.getObject().getIdOfferor()).thenReturn(13);

    OfferDTO offerDTO2 = getNewOffer();
    Mockito.when(offerDTO2.getIdOffer()).thenReturn(5);
    Mockito.when(offerDTO2.getObject().getIdObject()).thenReturn(56);
    Mockito.when(offerDTO2.getObject().getStatus()).thenReturn("given");
    Mockito.when(offerDTO2.getObject().getIdOfferor()).thenReturn(13);

    OfferDTO offerDTO3 = getNewOffer();
    Mockito.when(offerDTO3.getIdOffer()).thenReturn(6);
    Mockito.when(offerDTO3.getObject().getIdObject()).thenReturn(57);
    Mockito.when(offerDTO3.getObject().getIdOfferor()).thenReturn(33);
    Mockito.when(offerDTO3.getObject().getStatus()).thenReturn("available");
    Mockito.when(offerDTO3.getObject().getIdOfferor()).thenReturn(10);

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
    Mockito.when(offerDAO.updateOne(mockOfferDTO,false)).thenReturn(null);
    assertAll(
        () -> assertThrows(FatalException.class, () -> offerUCC.updateOffer(mockOfferDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer with a non existent id offer")
  @Test
  public void testUpdateOfferNotExistentIdOffer() {
    OfferDTO mockOfferDTO = Mockito.mock(OfferDTO.class);
    Mockito.when(mockOfferDTO.getIdOffer()).thenReturn(0);

    Mockito.when(offerDAO.updateOne(mockOfferDTO,false)).thenReturn(null);
    assertAll(
        () -> assertThrows(FatalException.class, () -> offerUCC.updateOffer(mockOfferDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateOffer success")
  @Test
  public void testUpdateOfferSuccess() {
    OfferDTO mockOfferDTO = getNewOffer();
    Mockito.when(mockOfferDTO.getIdOffer()).thenReturn(15);
    Mockito.when(mockOfferDTO.getObject().getDescription()).thenReturn("Très bon jeu");
    Mockito.when(mockOfferDTO.getObject().getStatus()).thenReturn("available");
    Mockito.when(mockOfferDTO.getDate()).thenReturn(LocalDate.now().minusMonths(2));

    OfferDTO mockOfferDTOUpdated = getNewOffer();
    Mockito.when(mockOfferDTOUpdated.getIdOffer()).thenReturn(15);
    Mockito.when(mockOfferDTOUpdated.getObject().getDescription()).thenReturn("Très bon jeu");
    Mockito.when(mockOfferDTOUpdated.getObject().getStatus()).thenReturn("available");
    Mockito.when(mockOfferDTOUpdated.getDate()).thenReturn(LocalDate.now());

    Mockito.when(offerDAO.updateOne(mockOfferDTO, false)).thenReturn(mockOfferDTOUpdated);

    OfferDTO offerDTO = offerUCC.updateOffer(mockOfferDTO);

    assertAll(
        () -> assertNotEquals(mockOfferDTO, offerDTO),
        () -> assertNotEquals(mockOfferDTO.getDate(), offerDTO.getDate()),
        () -> assertEquals(mockOfferDTO.getIdOffer(), offerDTO.getIdOffer()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

}