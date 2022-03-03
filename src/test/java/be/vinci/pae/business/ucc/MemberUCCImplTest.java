package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.business.exceptions.NotFoundException;
import be.vinci.pae.business.exceptions.UnauthorizedException;
import be.vinci.pae.dal.dao.MemberDAO;
import jakarta.ws.rs.ForbiddenException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MemberUCCImplTest {

  private String pseudo1 = "rayan";
  private String passwd1 = "rayan123";
  private String roleAccepted = "accepted";
  private String roleRefused = "refused";
  private MemberUCC memberUCC;
  private MemberDAO mockMemberDAO;
  private Member mockMember;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.memberUCC = locator.getService(MemberUCC.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
    mockMember = Mockito.mock(MemberImpl.class);

    Mockito.when(mockMember.getPseudo()).thenReturn(pseudo1);
    Mockito.when(mockMember.getPassword()).thenReturn(passwd1);
    Mockito.when(mockMember.getStatus()).thenReturn(roleAccepted);
    Mockito.when(mockMemberDAO.getOne(pseudo1)).thenReturn(mockMember);
    Mockito.when(mockMember.checkPassword(passwd1)).thenReturn(true);
  }

  @Test
  public void testGoodUsernameGoodPasswordNotRefusedAndInTheDB() {
    assertEquals(mockMember, memberUCC.login(pseudo1, passwd1));
  }

  @Test
  public void testGoodUsernameGoodPasswordRefusedAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(roleRefused);
    assertThrows(UnauthorizedException.class, () -> memberUCC.login(pseudo1, passwd1));
  }

  @Test
  public void testMemberNonExistent() {
    assertThrows(NotFoundException.class, () -> memberUCC.login("test", "test"));
  }

  @Test
  public void testGoodUsernameBadPasswordNotRefusedAndInTheDB() {
    assertThrows(ForbiddenException.class, () -> memberUCC.login(pseudo1, "test"));
  }

  @Test
  public void testGoodUsernameBadPasswordRefusedAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(roleRefused);
    assertThrows(ForbiddenException.class, () -> memberUCC.login(pseudo1, "test"));
  }

  @Test
  public void testPasswordExistentInTheDbForUsernameNonExistent() {
    assertThrows(NotFoundException.class, () -> memberUCC.login("test", passwd1));
  }

  @Test
  public void testPasswordIsEmptyForGoodUsernameInTheDB() {
    assertThrows(ForbiddenException.class, () -> memberUCC.login(pseudo1, ""));
  }

  @Test
  public void testUsernameIsEmptyForGoodPasswordInTheDB() {
    Mockito.when(mockMemberDAO.getOne("")).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login("", passwd1));
  }

  @Test
  public void testUsernameAndPasswordAreEmpty() {
    assertThrows(NotFoundException.class, () -> memberUCC.login("", ""));
  }
}