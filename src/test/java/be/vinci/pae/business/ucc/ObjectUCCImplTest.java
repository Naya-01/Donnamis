package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.OfferDTO;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.utils.Config;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ObjectUCCImplTest {

  private final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
  private final int inexistentId = 1000;
  private final String pathImage = "C:/img";
  private ObjectUCC objectUCC;
  private ObjectDAO mockObjectDAO;
  private DALService mockDalService;
  private ObjectDTO objectDTO;
  private ObjectDTO objectDTOUpdated;
  private OfferDTO offerDTO;
  private ObjectFactory objectFactory;

  @BeforeEach
  void initAll() {
    this.objectUCC = locator.getService(ObjectUCC.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    objectFactory = locator.getService(ObjectFactory.class);
    this.objectDTO = objectFactory.getObjectDTO();
    this.objectDTO.setIdObject(1);
    this.objectDTO.setDescription("the description");
    this.objectDTO.setIdOfferor(1);
    this.objectDTO.setStatus("available");
    this.objectDTO.setImage(this.pathImage);
    this.objectDTO.setVersion(1);
    this.objectDTOUpdated = objectFactory.getObjectDTO();
    this.objectDTOUpdated.setIdObject(1);
    this.objectDTOUpdated.setDescription("the description2");
    this.objectDTOUpdated.setIdOfferor(1);
    this.objectDTOUpdated.setStatus("available");
    this.objectDTOUpdated.setVersion(1);
    OfferFactory offerFactory = locator.getService(OfferFactory.class);
    this.offerDTO = offerFactory.getOfferDTO();
    this.offerDTO.setObject(objectDTO);
    Config.load("test.properties");
  }

  @DisplayName("test getObject with an existent id")
  @Test
  public void testGetObjectWithExistentId() {
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    assertAll(
        () -> assertEquals(objectDTO, objectUCC.getObject(objectDTO.getIdObject())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test getObject with a non existent id")
  @Test
  public void testGetObjectWithNonExistentId() {
    Mockito.when(mockObjectDAO.getOne(inexistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> objectUCC.getObject(inexistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getAllObjectMember with existent id and "
      + "at least one object in the db for the member")
  @Test
  public void testGetAllObjectMemberWithExistentIdAndSomeObjectsInTheDB() {
    List<ObjectDTO> allObjectsList = new ArrayList<>();
    allObjectsList.add(objectDTO);
    Mockito.when(mockObjectDAO.getAllObjectOfMember(1)).thenReturn(allObjectsList);
    assertAll(
        () -> assertEquals(allObjectsList, objectUCC.getAllObjectMember(1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test getAllObjectMember with non-existent id")
  @Test
  public void testGetAllObjectMemberWithNonExistentId() {
    List<ObjectDTO> allObjectsList = new ArrayList<>();
    Mockito.when(mockObjectDAO.getAllObjectOfMember(inexistentId)).thenReturn(allObjectsList);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> objectUCC.getAllObjectMember(inexistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getAllObjectMember with a member that has no object")
  @Test
  public void testGetAllObjectMemberWithAMemberThatHasNoObject() {
    List<ObjectDTO> allObjectsList = new ArrayList<>();
    Mockito.when(mockObjectDAO.getAllObjectOfMember(1)).thenReturn(allObjectsList);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> objectUCC.getAllObjectMember(1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test updateOne with an existent object")
  @Test
  public void testUpdateOneWithExistentObject() {
    Mockito.when(mockObjectDAO.getOne(objectDTOUpdated.getIdObject())).thenReturn(objectDTO);
    Mockito.when(mockObjectDAO.updateOne(objectDTOUpdated)).thenReturn(objectDTOUpdated);
    assertAll(
        () -> assertEquals(objectDTOUpdated, objectUCC.updateOne(objectDTOUpdated)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).updateOne(objectDTOUpdated),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test updateOne without changing the existent object")
  @Test
  public void testUpdateOneWithoutChangingExistentObject() {
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    Mockito.when(mockObjectDAO.updateOne(objectDTO)).thenReturn(objectDTO);
    assertAll(
        () -> assertEquals(objectDTO, objectUCC.updateOne(objectDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).updateOne(objectDTO),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test updateOne with non-existent object")
  @Test
  public void testUpdateOneWithNonExistentObject() {
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> objectUCC.updateOne(objectDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test updateOne with not same version")
  @Test
  public void testUpdateOneWithNotSameVersion() {
    ObjectDTO objectDTOFromDao = objectFactory.getObjectDTO();
    objectDTOFromDao.setVersion(13);
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTOFromDao);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> objectUCC.updateOne(objectDTO)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1)).getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test updateObjectPicture with existent object that has no image")
  @Test
  public void testUpdateObjectPictureWithExistentObjectThatHasNoImage() {
    objectDTO.setVersion(1);
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    Mockito.when(mockObjectDAO.updateObjectPicture(pathImage, objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    assertAll(
        () -> assertEquals(objectDTO,
            objectUCC.updateObjectPicture(pathImage, objectDTO.getIdObject(),
                objectDTO.getIdOfferor(), 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .updateObjectPicture(pathImage, objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test updateObjectPicture with existent object that has an image")
  @Test
  public void testUpdateObjectPictureWithExistentObjectThatHasAnImage() {
    objectDTO.setImage("C:/img2");
    objectDTO.setVersion(1);
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    Mockito.when(mockObjectDAO.updateObjectPicture(pathImage, objectDTO.getIdObject()))
        .thenReturn(objectDTO);
    assertAll(
        () -> assertEquals(objectDTO,
            objectUCC.updateObjectPicture(pathImage, objectDTO.getIdObject(),
                objectDTO.getIdOfferor(), 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .updateObjectPicture(pathImage, objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test updateObjectPicture with non-existent object")
  @Test
  public void testUpdateObjectPictureWithNonExistentObject() {
    Mockito.when(mockObjectDAO.getOne(1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> objectUCC.updateObjectPicture(pathImage, 1, 1, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test updateObjectPicture with differents versions")
  @Test
  public void testUpdateObjectPictureWithDifferentsVersions() {
    objectDTO.setVersion(1);
    objectDTOUpdated.setVersion(2);
    Mockito.when(mockObjectDAO.getOne(1)).thenReturn(objectDTOUpdated);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> objectUCC.updateObjectPicture(pathImage, 1, 1, 1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getPicture with non-existent object")
  @Test
  public void testGetPictureWithNonExistentObject() {
    Mockito.when(mockObjectDAO.getOne(this.inexistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> objectUCC.getPicture(this.inexistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(this.inexistentId),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("test getPicture with existent object that has no image")
  @Test
  public void testGetPictureWithExistentObjectThatHasNoImage() {
    Mockito.when(mockObjectDAO.getOne(this.objectDTO.getIdObject())).thenReturn(this.objectDTO);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> objectUCC.getPicture(this.objectDTO.getIdObject())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockObjectDAO, Mockito.atLeast(1))
            .getOne(this.objectDTO.getIdObject()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }


}