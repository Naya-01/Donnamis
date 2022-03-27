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
  @DisplayName("Test get last offers avec aucune offre recue du DAO")
  @Test
  public void testGetAllLastOffersWithDAOReturningEmptyListOfOffers() {
    Mockito.when(offerDAO.getAllLast()).thenReturn(new ArrayList<>());
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.getLastOffers()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test get last offers avec les dernières offres venant du DAO")
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

  @DisplayName("Test get offer by id avec un id d'offre existant")
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

  @DisplayName("Test get offer by id avec un id d'offre non existant")
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

  @DisplayName("Test ajouter une offre avec un type d'objet existant")
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


  @DisplayName("Test ajouter une offre avec  type d'objet existant et ajout de l'offre impossible")
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


  @DisplayName("Test ajouter une offre avec type d'objet existant et object de l'offre non ajouté")
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

  @DisplayName("Test ajouter une offre avec type d'objet existant et object de l'offre ajouté")
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

  @DisplayName("Test ajouter une offre avec un type d'objet non existant et pas été ajouté en DB")
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


  @DisplayName("Test ajouter une offre avec un id type et nom du type vide")
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

  @DisplayName("Test ajouter une offre avec un id type et nom du type null")
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

  @DisplayName("Test ajouter une offre avec un type d'objet non existant et a été ajouté à la DB")
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


  @DisplayName("Test get offers avec aucune offre retournée")
  @Test
  public void testGetOffersWithEmptyListOfOffersReturned() {
    Mockito.when(offerDAO.getAll("", 0)).thenReturn(new ArrayList<>());
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> offerUCC.getOffers("", 0)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }
}