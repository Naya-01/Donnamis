package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.RatingDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.RatingFactory;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.RatingDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
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
  private InterestDAO mockInterestDAO;
  private RatingDTO ratingDTO;
  private InterestDTO interestDTO;
  private DALService mockDalService;
  private final int nonExistingId = 1000;
  private final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());

  @BeforeEach
  void initAll() {
    this.ratingUCC = locator.getService(RatingUCC.class);
    this.mockRatingDAO = locator.getService(RatingDAO.class);
    this.mockInterestDAO = locator.getService(InterestDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    RatingFactory ratingFactory = locator.getService(RatingFactory.class);
    this.ratingDTO = ratingFactory.getRatingDTO();
    this.ratingDTO.setIdObject(1);
    this.ratingDTO.setIdMember(1);
    this.ratingDTO.setRating(3);
    this.ratingDTO.setComment("Not the best object.");
    InterestFactory interestFactory = locator.getService(InterestFactory.class);
    this.interestDTO = interestFactory.getInterestDTO();
    this.interestDTO.setStatus("received");
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
        () -> assertThrows(NotFoundException.class, () -> ratingUCC.getOne(this.nonExistingId)),
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

  @DisplayName("Test addRating with rating that have non existing id")
  @Test
  public void testAddRatingWhenThereIsAlreadyARatingForThisObject() {
    Mockito.when(mockRatingDAO.getOne(this.ratingDTO.getIdObject())).thenReturn(this.ratingDTO);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> ratingUCC.addRating(this.ratingDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("Test addRating without interest for this object and member")
  @Test
  public void testAddRatingWithoutInterestForThisObjectAndMember() {
    Mockito.when(mockRatingDAO.getOne(this.ratingDTO.getIdObject())).thenReturn(null);
    Mockito.when(
        mockInterestDAO.getOne(this.ratingDTO.getIdObject(), this.ratingDTO.getIdMember())
    ).thenReturn(null);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> ratingUCC.addRating(this.ratingDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("Test addRating with interest for this object and member but not received")
  @Test
  public void testAddRatingWithInterestForThisObjectAndMemberButNotReceived() {
    this.interestDTO.setStatus("published");
    Mockito.when(mockRatingDAO.getOne(this.ratingDTO.getIdObject())).thenReturn(null);
    Mockito.when(
        mockInterestDAO.getOne(this.ratingDTO.getIdObject(), this.ratingDTO.getIdMember())
    ).thenReturn(this.interestDTO);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> ratingUCC.addRating(this.ratingDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .rollBackTransaction()
    );
  }

  @DisplayName("Test addRating with interest for this object and member")
  @Test
  public void testAddRatingWithInterestForThisObjectAndMember() {
    Mockito.when(mockRatingDAO.getOne(this.ratingDTO.getIdObject())).thenReturn(null);
    Mockito.when(
        mockInterestDAO.getOne(this.ratingDTO.getIdObject(), this.ratingDTO.getIdMember())
    ).thenReturn(this.interestDTO);
    Mockito.when(mockRatingDAO.addOne(this.ratingDTO)).thenReturn(this.ratingDTO);
    assertAll(
        () -> assertEquals(this.ratingDTO, ratingUCC.addRating(this.ratingDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1))
            .commitTransaction()
    );
  }





}