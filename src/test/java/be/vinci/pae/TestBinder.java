package be.vinci.pae;

import be.vinci.pae.business.domain.InterestImpl;
import be.vinci.pae.business.domain.TypeImpl;
import be.vinci.pae.business.domain.dto.InterestDTO;
import be.vinci.pae.business.domain.dto.TypeDTO;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.InterestFactoryImpl;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.MemberFactoryImpl;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.business.factories.TypeFactoryImpl;
import be.vinci.pae.business.ucc.InterestUCC;
import be.vinci.pae.business.ucc.InterestUCCImpl;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.business.ucc.MemberUCCImpl;
import be.vinci.pae.business.ucc.TypeUCC;
import be.vinci.pae.business.ucc.TypeUCCImpl;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.AddressDAOImpl;
import be.vinci.pae.dal.dao.InterestDAO;
import be.vinci.pae.dal.dao.InterestDAOImpl;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.MemberDAOImpl;
import be.vinci.pae.dal.dao.ObjectDAO;
import be.vinci.pae.dal.dao.ObjectDAOImpl;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.dao.TypeDAOImpl;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.dal.services.DALServiceImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

@Provider
public class TestBinder extends AbstractBinder {

  @Override
  protected void configure() {
    bind(MemberFactoryImpl.class).to(MemberFactory.class).in(Singleton.class);
    bind(TypeFactoryImpl.class).to(TypeFactory.class).in(Singleton.class);
    bind(InterestFactoryImpl.class).to(InterestFactory.class).in(Singleton.class);

    bind(Mockito.mock(DALServiceImpl.class)).to(DALService.class);

    bind(MemberUCCImpl.class).to(MemberUCC.class).in(Singleton.class);
    bind(TypeUCCImpl.class).to(TypeUCC.class).in(Singleton.class);
    bind(InterestUCCImpl.class).to(InterestUCC.class).in(Singleton.class);

    bind(Mockito.mock(AddressDAOImpl.class)).to(AddressDAO.class);
    bind(Mockito.mock(TypeDAOImpl.class)).to(TypeDAO.class);
    bind(Mockito.mock(MemberDAOImpl.class)).to(MemberDAO.class);
    bind(Mockito.mock(InterestDAOImpl.class)).to(InterestDAO.class);
    bind(Mockito.mock(ObjectDAOImpl.class)).to(ObjectDAO.class);

    bind(Mockito.mock(InterestImpl.class)).to(InterestDTO.class);

    bind(Mockito.mock(TypeImpl.class)).to(TypeDTO.class);
  }
}