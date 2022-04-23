package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.business.factories.RatingFactory;
import be.vinci.pae.dal.dao.RatingDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RatingUCCImplTest {

  private RatingUCC ratingUCC;
  private RatingDAO mockRatingDAO;
  private RatingDTO ratingDTO;
  private DALService mockDalService;
  private RatingFactory ratingFactory;
  private final int nonExistingId = 1000;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.ratingUCC = locator.getService(RatingUCC.class);
    this.mockRatingDAO = locator.getService(RatingDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    this.ratingFactory = locator.getService(RatingFactory.class);
    this.ratingDTO = ratingFactory.getRatingDTO();
    this.ratingDTO.setIdObject(1);
    this.ratingDTO.setIdMember(1);
    this.ratingDTO.setRating(3);
    this.ratingDTO.setComment("Not the best object.");
  }

  @DisplayName("Test getOne with existing id")
  @Test
  public void testGetOneWithExistingId() {
    Mockito.when(mockRatingDAO.getOne(this.ratingDTO.getIdObject())).thenReturn(this.ratingDTO);
    assertAll(
        () -> assertEquals(this.ratingDTO, ratingUCC.getOne(this.ratingDTO.getIdObject())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }

  @DisplayName("Test getOne with non existing id")
  @Test
  public void testGetOneWithNonExistingId() {
    Mockito.when(mockRatingDAO.getOne(this.nonExistingId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> ratingUCC.getOne(this.ratingDTO.getIdObject())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("Test getOne with negative id")
  @Test
  public void testGetOneWithNegativeId() {
    Mockito.when(mockRatingDAO.getOne(-1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> ratingUCC.getOne(-1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }





}