package be.vinci.pae.utils;

import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.business.factories.AddressFactoryImpl;
import be.vinci.pae.business.factories.InterestFactory;
import be.vinci.pae.business.factories.InterestFactoryImpl;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.business.factories.MemberFactoryImpl;
import be.vinci.pae.business.factories.ObjectFactory;
import be.vinci.pae.business.factories.ObjectFactoryImpl;
import be.vinci.pae.business.factories.OfferFactory;
import be.vinci.pae.business.factories.OfferFactoryImpl;
import be.vinci.pae.business.factories.RatingFactory;
import be.vinci.pae.business.factories.RatingFactoryImpl;
import be.vinci.pae.business.factories.TypeFactory;
import be.vinci.pae.business.factories.TypeFactoryImpl;
import be.vinci.pae.business.ucc.InterestUCC;
import be.vinci.pae.business.ucc.InterestUCCImpl;
import be.vinci.pae.business.ucc.MemberUCC;
import be.vinci.pae.business.ucc.MemberUCCImpl;
import be.vinci.pae.business.ucc.ObjectUCC;
import be.vinci.pae.business.ucc.ObjectUCCImpl;
import be.vinci.pae.business.ucc.OfferUCC;
import be.vinci.pae.business.ucc.OfferUCCImpl;
import be.vinci.pae.business.ucc.RatingUCC;
import be.vinci.pae.business.ucc.RatingUCCImpl;
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
import be.vinci.pae.dal.dao.OfferDAO;
import be.vinci.pae.dal.dao.OfferDAOImpl;
import be.vinci.pae.dal.dao.RatingDAO;
import be.vinci.pae.dal.dao.RatingDAOImpl;
import be.vinci.pae.dal.dao.TypeDAO;
import be.vinci.pae.dal.dao.TypeDAOImpl;
import be.vinci.pae.dal.services.DALBackendService;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.dal.services.DALServiceImpl;
import be.vinci.pae.ihm.manager.Image;
import be.vinci.pae.ihm.manager.ImageImpl;
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
    bind(InterestFactoryImpl.class).to(InterestFactory.class).in(Singleton.class);
    bind(DALServiceImpl.class).to(DALBackendService.class).to(DALService.class).in(Singleton.class);

    bind(MemberUCCImpl.class).to(MemberUCC.class).in(Singleton.class);
    bind(TypeUCCImpl.class).to(TypeUCC.class).in(Singleton.class);
    bind(InterestUCCImpl.class).to(InterestUCC.class).in(Singleton.class);
    bind(MemberDAOImpl.class).to(MemberDAO.class).in(Singleton.class);
    bind(TypeDAOImpl.class).to(TypeDAO.class).in(Singleton.class);
    bind(AddressDAOImpl.class).to(AddressDAO.class).in(Singleton.class);
    bind(InterestDAOImpl.class).to(InterestDAO.class).in(Singleton.class);
    bind(OfferFactoryImpl.class).to(OfferFactory.class).in(Singleton.class);
    bind(OfferUCCImpl.class).to(OfferUCC.class).in(Singleton.class);
    bind(OfferDAOImpl.class).to(OfferDAO.class).in(Singleton.class);

    bind(ObjectFactoryImpl.class).to(ObjectFactory.class).in(Singleton.class);
    bind(ObjectDAOImpl.class).to(ObjectDAO.class).in(Singleton.class);
    bind(ObjectUCCImpl.class).to(ObjectUCC.class).in(Singleton.class);

    bind(RatingFactoryImpl.class).to(RatingFactory.class).in(Singleton.class);
    bind(RatingDAOImpl.class).to(RatingDAO.class).in(Singleton.class);
    bind(RatingUCCImpl.class).to(RatingUCC.class).in(Singleton.class);

    bind(TokenImpl.class).to(Token.class).in(Singleton.class);
    bind(ImageImpl.class).to(Image.class).in(Singleton.class);
  }
}
