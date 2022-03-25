package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.AddressImpl;
import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.exceptions.UnauthorizedException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MemberUCCImplTest {

  private final String username = "rayan";
  private final String badUsername = "test";
  private final String passwd1 = "rayan123";
  private final String badPassword = "test";
  private final String statusValid = "valid";
  private final String statusDenied = "denied";
  private final String statusPending = "pending";
  private MemberUCC memberUCC;
  private MemberDAO mockMemberDAO;
  private Member mockMember;
  private DALService mockDalService;

  @BeforeEach
  void initAll() {
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
    this.memberUCC = locator.getService(MemberUCC.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    mockMember = Mockito.mock(MemberImpl.class);

    Mockito.when(mockMember.getUsername()).thenReturn(username);
    Mockito.when(mockMember.getPassword()).thenReturn(passwd1);
    Mockito.when(mockMember.getStatus()).thenReturn(statusValid);
    Mockito.when(mockMemberDAO.getOne(username)).thenReturn(mockMember);
    Mockito.when(mockMemberDAO.getOne(1)).thenReturn(mockMember);
    Mockito.when(mockMember.checkPassword(passwd1)).thenReturn(true);
  }

  @DisplayName("Test login function with a good username, a good password, a valid member that "
      + "exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameGoodPasswordAndMemberValidAndInTheDB() {
    assertAll(
        () -> assertEquals(mockMember, memberUCC.login(username, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember, Mockito.times(2)).getStatus()
    );

  }


  @DisplayName("Test login function with a good username, a good password, a denied member that "
      + "exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameGoodPasswordAndMemberDeniedAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusDenied);
    assertAll(
        () -> assertThrows(UnauthorizedException.class, () -> memberUCC.login(username, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember).getStatus()
    );
  }

  @DisplayName("Test login function with a good username, a good password, a pending member that "
      + "exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameGoodPasswordAndMemberPendingAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusPending);
    assertAll(
        () -> assertThrows(UnauthorizedException.class, () -> memberUCC.login(username, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember, Mockito.times(2)).getStatus()
    );
  }

  @DisplayName("Test login function with a member that doesn't exist in the DB")
  @Test
  public void testLoginFunctionMemberNonExistent() {
    Mockito.when(mockMemberDAO.getOne(badUsername)).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login(badUsername, badPassword));
  }

  @DisplayName("Test login function with a good username, a bad password, a valid member that "
      + "exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameBadPasswordAndMemberValidAndInTheDB() {
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(username, badPassword)),
        () -> Mockito.verify(mockMember).checkPassword(badPassword),
        () -> Mockito.verify(mockMember, Mockito.never()).getStatus()
    );
  }

  @DisplayName("Test login function with a good username, a bad password, a denied member that "
      + "exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameBadPasswordAndMemberDeniedAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusDenied);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(username, badPassword)),
        () -> Mockito.verify(mockMember).checkPassword(badPassword),
        () -> Mockito.verify(mockMember, Mockito.never()).getStatus()
    );
  }

  @DisplayName("Test login function with a good username, a bad password, a pending member that"
      + " exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameBadPasswordAndMemberPendingAndInTheDB() {
    Mockito.when(mockMember.getStatus()).thenReturn(statusPending);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(username, badPassword)),
        () -> Mockito.verify(mockMember).checkPassword(badPassword),
        () -> Mockito.verify(mockMember, Mockito.never()).getStatus()
    );
  }

  @DisplayName("Test login function with a non existent username but an existent password "
      + "in the DB")
  @Test
  public void testLoginFunctionPasswordExistentInTheDbForNonExistentUsername() {
    Mockito.when(mockMemberDAO.getOne(badUsername)).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login(badUsername, passwd1));
  }

  @DisplayName("Test login function with an existent username in the DB and an empty password")
  @Test
  public void testLoginFunctionPasswordIsEmptyForExistentUsernameInTheDB() {
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC.login(username, "")),
        () -> Mockito.verify(mockMember).checkPassword("")
    );
  }

  @DisplayName("Test login function with an existent password in the DB and an empty username")
  @Test
  public void testLoginFunctionUsernameIsEmptyForExistentPasswordInTheDB() {
    Mockito.when(mockMemberDAO.getOne("")).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login("", passwd1));
  }

  @DisplayName("Test login function with username and password fields empty")
  @Test
  public void testLoginFunctionUsernameAndPasswordAreEmpty() {
    Mockito.when(mockMemberDAO.getOne("")).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.login("", ""));
  }

  @DisplayName("Test getMember function with negative id")
  @Test
  public void testGetMemberFunctionWithNegativeId() {
    Mockito.when(mockMemberDAO.getOne(-1)).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.getMember(-1));
  }

  @DisplayName("Test getMember function with an existent id")
  @Test
  public void testGetMemberFunctionWithExistentId() {
    assertEquals(mockMember, memberUCC.getMember(1));
  }

  @DisplayName("Test getMember function with a non existent id povitive")
  @Test
  public void testGetMemberFunctionWithNonExistentIdPositive() {
    Mockito.when(mockMemberDAO.getOne(100)).thenReturn(null);
    assertThrows(NotFoundException.class, () -> memberUCC.getMember(100));
  }

  //  ----------------------------  UPDATE PROFIL PICTURE UCC  -------------------------------  //

  //FINIR L UCC UPDATE PROFIL PICTURE
  @DisplayName("Test updateProfilPicture with a non existent id for member")
  @Test
  public void testUpdateProfilPictureForNonExistentMember() {
    int idMember = 1000;
    Mockito.when(mockMemberDAO.getOne(idMember)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> memberUCC.updateProfilPicture("C:/img", idMember)),
        () -> Mockito.verify(mockMemberDAO).getOne(idMember)
    );
  }

  //  -----------------------------  REGISTER UCC  ----------------------------------------  //

  @DisplayName("Test inscription tout va bien")
  @Test
  public void testRegisterSuccess() {
    AddressDTO newAddress = Mockito.mock(AddressImpl.class);
    Mockito.when(newAddress.getIdMember()).thenReturn(6);
    Mockito.when(newAddress.getUnitNumber()).thenReturn("4");
    Mockito.when(newAddress.getBuildingNumber()).thenReturn("2");
    Mockito.when(newAddress.getStreet()).thenReturn("Rue de l'aérosol");
    Mockito.when(newAddress.getPostcode()).thenReturn("1234");
    Mockito.when(newAddress.getCommune()).thenReturn("Wolluwe");
    Mockito.when(newAddress.getCountry()).thenReturn("Belgique");

    Member newMember = Mockito.mock(MemberImpl.class);
    Mockito.when(newMember.getMemberId()).thenReturn(6);
    Mockito.when(newMember.getUsername()).thenReturn("MatthieuDu42");
    Mockito.when(newMember.getLastname()).thenReturn("Du bois");
    Mockito.when(newMember.getFirstname()).thenReturn("Matthieu");
    Mockito.when(newMember.getPhone()).thenReturn("0412345678");
    Mockito.when(newMember.getPassword()).thenReturn("matthieuLeChien");
    Mockito.when(newMember.getAddress()).thenReturn(newAddress);

    Mockito.when(mockMemberDAO.getOne("MatthieuDu42")).thenReturn(null);
    Mockito.when(mockMemberDAO.createOneMember(newMember)).thenReturn(newMember);

    MemberDTO memberRegistered = memberUCC.register(newMember);

    assertAll(
        () -> assertNotEquals(0, memberRegistered.getMemberId()),
        () -> assertEquals(memberRegistered.getMemberId(),
            memberRegistered.getAddress().getIdMember()),
        () -> Mockito.verify(mockDalService).startTransaction(),
        () -> Mockito.verify(mockDalService).commitTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.never()).rollBackTransaction()
    );
  }
}