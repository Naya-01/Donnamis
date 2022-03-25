package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.*;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.NotFoundException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class TypeUCCImplTest {

  private TypeUCC typeUCC;
  private TypeDAO mockTypeDAO;
  private TypeDTO mockRealType;
  private DALService mockDalService;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.typeUCC = locator.getService(TypeUCC.class);
    this.mockTypeDAO = locator.getService(TypeDAO.class);
    this.mockRealType = locator.getService(TypeDTO.class);
    this.mockDalService = locator.getService(DALService.class);
    Mockito.when(mockRealType.getIdType()).thenReturn(1);
    Mockito.when(mockRealType.getTypeName()).thenReturn("Bon type");
  }

  @DisplayName("Test getType with id function with an id that correspond to an existing type")
  @Test
  public void testGetTypeWithGoodId(){
    Mockito.when(mockTypeDAO.getOne(mockRealType.getIdType())).thenReturn(mockRealType);
    assertAll(
        () -> assertEquals(typeUCC.getType(mockRealType.getIdType()), mockRealType),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("Test getType with id function with a negative id")
  @Test
  public void testGetTypeWithANegativeId(){
    Mockito.when(mockTypeDAO.getOne(-1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getType(-1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("Test getType with id function with an inexistant id")
  @Test
  public void testGetTypeWithInexistantId(){
    Mockito.when(mockTypeDAO.getOne(1000)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getType(1000)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }


}