package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.dal.dao.MemberDAO;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MemberUCCImplTest {

  String pseudo1 = "rayan";
  String passwd1 = "rayan123";
  String roleAccepted = "accepted";
  private MemberUCC memberUCC;
  private MemberDAO mockMemberDAO;
  private Member mockMember1;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.memberUCC = locator.getService(MemberUCC.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
    mockMember1 = Mockito.mock(MemberImpl.class);
  }

  @Test
  public void testGoodUsernameGoodPasswordNotRefusedAndInTheDB() {
    Mockito.when(mockMember1.getPseudo()).thenReturn(pseudo1);
    Mockito.when(mockMember1.getPassword()).thenReturn(passwd1);
    Mockito.when(mockMember1.getStatus()).thenReturn(roleAccepted);
    Mockito.when(mockMemberDAO.getOne(pseudo1)).thenReturn(mockMember1);
    Mockito.when(mockMember1.checkPassword(passwd1)).thenReturn(true);
    assertEquals(mockMember1, memberUCC.login(pseudo1, passwd1));
  }


}