package be.vinci.pae.business.ucc;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.services.DALService;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

class ObjectUCCImplTest {

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

}