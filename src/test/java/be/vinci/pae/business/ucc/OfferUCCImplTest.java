package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.OfferDTO;
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

class OfferUCCImplTest {

  private DALService mockDalService;
  private OfferDAO offerDAO;
  private OfferUCC offerUCC;

  @BeforeEach
  void setUp() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.mockDalService = locator.getService(DALService.class);
    this.offerDAO = locator.getService(OfferDAO.class);
    this.offerUCC = locator.getService(OfferUCC.class);
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
}