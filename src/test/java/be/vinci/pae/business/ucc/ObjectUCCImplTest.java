package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.ObjectDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class ObjectUCCImplTest {

  private ObjectUCC objectUCC;
  private ObjectDAO mockObjectDAO;
  private DALService mockDalService;
  private ObjectFactory objectFactory;
  private ObjectDTO objectDTO;
  private int inexistentId = 1000;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.objectUCC = locator.getService(ObjectUCC.class);
    this.mockObjectDAO = locator.getService(ObjectDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    this.objectFactory = locator.getService(ObjectFactory.class);
    this.objectDTO = objectFactory.getObjectDTO();
    objectDTO.setIdObject(1);
    objectDTO.setDescription("the description");
    objectDTO.setIdOfferor(1);
    objectDTO.setStatus("available");
  }

  @DisplayName("test getObject with an existent id")
  @Test
  public void testGetObjectWithExistentId(){
    Mockito.when(mockObjectDAO.getOne(objectDTO.getIdObject())).thenReturn(objectDTO);
    assertAll(
        () -> assertEquals(objectDTO, objectUCC.getObject(objectDTO.getIdObject())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("test getObject with a non existent id")
  @Test
  public void testGetObjectWithNonExistentId(){
    Mockito.when(mockObjectDAO.getOne(inexistentId)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> objectUCC.getObject(inexistentId)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }



}