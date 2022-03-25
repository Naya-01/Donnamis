package be.vinci.pae.business.ucc;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.TypeDTO;
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
import org.mockito.Mockito;

class TypeUCCImplTest {

  private TypeUCC typeUCC;
  private TypeDAO mockTypeDAO;
  private TypeDTO mockRealType;
  private DALService mockDalService;
  private List<TypeDTO> allDefaultTypesMock;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.typeUCC = locator.getService(TypeUCC.class);
    this.mockTypeDAO = locator.getService(TypeDAO.class);
    this.mockRealType = locator.getService(TypeDTO.class);
    this.mockDalService = locator.getService(DALService.class);
    Mockito.when(mockRealType.getIdType()).thenReturn(1);
    Mockito.when(mockRealType.getTypeName()).thenReturn("Bon type");
    this.allDefaultTypesMock = new ArrayList<>();
    this.allDefaultTypesMock.add(mockRealType);
  }

  @DisplayName("Test getType with id function with an id that correspond to an existing type")
  @Test
  public void testGetTypeWithGoodId() {
    Mockito.when(mockTypeDAO.getOne(mockRealType.getIdType())).thenReturn(mockRealType);
    assertAll(
        () -> assertEquals(typeUCC.getType(mockRealType.getIdType()), mockRealType),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("Test getType with id function with a negative id")
  @Test
  public void testGetTypeWithANegativeId() {
    Mockito.when(mockTypeDAO.getOne(-1)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getType(-1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("Test getType with id function with a non-existent id")
  @Test
  public void testGetTypeWithNonExistentId() {
    Mockito.when(mockTypeDAO.getOne(1000)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getType(1000)),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("Test getType with typeName function with a non-existent name")
  @Test
  public void testGetTypeWithNonExistentName() {
    Mockito.when(mockTypeDAO.getOne("non-existent Type")).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getType("non-existent Type")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("Test getType with typeName function with an existent name")
  @Test
  public void testGetTypeWithExistentName() {
    Mockito.when(mockTypeDAO.getOne(mockRealType.getTypeName())).thenReturn(mockRealType);
    assertAll(
        () -> assertEquals(mockRealType, typeUCC.getType(mockRealType.getTypeName())),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("Test getType with typeName function with an empty name")
  @Test
  public void testGetTypeWithEmptyName() {
    Mockito.when(mockTypeDAO.getOne("")).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getType("")),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }

  @DisplayName("Test getAllDefaultTypes function when there are default types in the DB")
  @Test
  public void testGetAllDefaultTypesWithDefaultTypesInTheDB() {
    Mockito.when(mockTypeDAO.getAllDefaultTypes()).thenReturn(allDefaultTypesMock);
    assertAll(
        () -> assertEquals(allDefaultTypesMock, typeUCC.getAllDefaultTypes()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).commitTransaction()
    );
  }

  @DisplayName("Test getAllDefaultTypes function when there are no default types in the DB")
  @Test
  public void testGetAllDefaultTypesWithoutDefaultTypesInTheDB() {
    Mockito.when(mockTypeDAO.getAllDefaultTypes()).thenReturn(new ArrayList<>());
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> typeUCC.getAllDefaultTypes()),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeast(1)).rollBackTransaction()
    );
  }
}