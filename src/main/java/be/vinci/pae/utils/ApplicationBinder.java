package be.vinci.pae.utils;

import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.business.factories.AddressFactoryImpl;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.MemberFactoryImpl;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.business.factories.TypeFactoryImpl;
import be.vinci.pae.business.ucc.AddressUCC;
import be.vinci.pae.business.ucc.AddressUCCImpl;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.business.ucc.MemberUCCImpl;
import be.vinci.pae.business.ucc.TypeUCC;
import be.vinci.pae.business.ucc.TypeUCCImpl;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.AddressDAOImpl;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.MemberDAOImpl;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.dao.TypeDAOImpl;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.dal.services.DALServiceImpl;
import be.vinci.pae.ihm.manager.Token;
import be.vinci.pae.ihm.manager.TokenImpl;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

@Provider
public class ApplicationBinder extends AbstractBinder {

  @Override
  protected void configure() {
    bind(MemberFactoryImpl.class).to(MemberFactory.class).in(Singleton.class);
    bind(TypeFactoryImpl.class).to(TypeFactory.class).in(Singleton.class);
    bind(AddressFactoryImpl.class).to(AddressFactory.class).in(Singleton.class);

    bind(DALServiceImpl.class).to(DALService.class).in(Singleton.class);

    bind(MemberUCCImpl.class).to(MemberUCC.class).in(Singleton.class);
    bind(TypeUCCImpl.class).to(TypeUCC.class).in(Singleton.class);
    bind(AddressUCCImpl.class).to(AddressUCC.class).in(Singleton.class);

    bind(MemberDAOImpl.class).to(MemberDAO.class).in(Singleton.class);
    bind(TypeDAOImpl.class).to(TypeDAO.class).in(Singleton.class);
    bind(AddressDAOImpl.class).to(AddressDAO.class).in(Singleton.class);
    
    bind(TokenImpl.class).to(Token.class).in(Singleton.class);
  }
}
