package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
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

  private final String pseudo1 = "rayan";
  private final String badPseudo = "test";
  private final String passwd1 = "rayan123";
  private final String badPassword = "test";
  private final String statusValid = "valid";
  private final String statusDenied = "denied";
  private final String statusPending = "pending";
  private MemberUCC memberUCC;
  private MemberDAO mockMemberDAO;
  private Member mockMember;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.memberUCC = locator.getService(MemberUCC.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
    mockMember = Mockito.mock(MemberImpl.class);

    Mockito.when(mockMember.getUsername()).thenReturn(pseudo1);
    Mockito.when(mockMember.getPassword()).thenReturn(passwd1);
    Mockito.when(mockMember.getStatus()).thenReturn(statusValid);
    Mockito.when(mockMemberDAO.getOne(pseudo1)).thenReturn(mockMember);
    Mockito.when(mockMember.checkPassword(passwd1)).thenReturn(true);
  }

  @Test
  public void testGoodUsernameGoodPasswordValidAndInTheDB() {
    assertAll(
        () -> assertEquals(mockMember, memberUCC.login(pseudo1, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember, Mockito.times(2)).getStatus()
    );

  }

  @Test
  public void testGoodUsernameGoodPasswordDeniedAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusDenied);
    assertAll(
        () -> assertThrows(UnauthorizedException.class, () -> memberUCC.login(pseudo1, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember).getStatus()
    );
  }

  @Test
  public void testGoodUsernameGoodPasswordPendingAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusPending);
    assertAll(
        () -> assertThrows(UnauthorizedException.class, () -> memberUCC.login(pseudo1, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember, Mockito.times(2)).getStatus()
    );
  }

  @Test
  public void testMemberNonExistent() {
    Mockito.when(mockMemberDAO.getOne(badPseudo)).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login(badPseudo, badPassword));
  }

  @Test
  public void testGoodUsernameBadPasswordValidAndInTheDB() {
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(pseudo1, badPassword)),
        () -> Mockito.verify(mockMember).checkPassword(badPassword),
        () -> Mockito.verify(mockMember, Mockito.never()).getStatus()
    );
  }

  @Test
  public void testGoodUsernameBadPasswordDeniedAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusDenied);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(pseudo1, badPassword)),
        () -> Mockito.verify(mockMember).checkPassword(badPassword),
        () -> Mockito.verify(mockMember, Mockito.never()).getStatus()
    );
  }

  @Test
  public void testPasswordExistentInTheDbForUsernameNonExistent() {
    Mockito.when(mockMemberDAO.getOne(badPseudo)).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login(badPseudo, passwd1));
  }

  @Test
  public void testPasswordIsEmptyForGoodUsernameInTheDB() {
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(pseudo1, "")),
        () -> Mockito.verify(mockMember).checkPassword("")
    );
  }

  @Test
  public void testUsernameIsEmptyForGoodPasswordInTheDB() {
    Mockito.when(mockMemberDAO.getOne("")).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login("", passwd1));
  }

  @Test
  public void testUsernameAndPasswordAreEmpty() {
    Mockito.when(mockMemberDAO.getOne("")).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login("", ""));
  }
}