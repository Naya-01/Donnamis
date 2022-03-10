package be.vinci.pae.utils;

import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.MemberFactoryImpl;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.business.ucc.MemberUCCImpl;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.AddressDAOImpl;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.dao.MemberDAOImpl;
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
    bind(DALServiceImpl.class).to(DALService.class).in(Singleton.class);
    bind(MemberUCCImpl.class).to(MemberUCC.class).in(Singleton.class);
    bind(MemberDAOImpl.class).to(MemberDAO.class).in(Singleton.class);
    bind(AddressDAOImpl.class).to(AddressDAO.class).in(Singleton.class);
    bind(TokenImpl.class).to(Token.class).in(Singleton.class);
  }
}
