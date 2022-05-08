package be.vinci.pae.business.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.TestBinder;
import be.vinci.pae.business.domain.Member;
import be.vinci.pae.business.domain.MemberImpl;
import be.vinci.pae.business.domain.dto.AddressDTO;
import be.vinci.pae.business.domain.dto.MemberDTO;
import be.vinci.pae.business.factories.AddressFactory;
import be.vinci.pae.business.factories.MemberFactory;
import be.vinci.pae.dal.dao.AddressDAO;
import be.vinci.pae.dal.dao.MemberDAO;
import be.vinci.pae.dal.services.DALService;
import be.vinci.pae.exceptions.ConflictException;
import be.vinci.pae.exceptions.FatalException;
import be.vinci.pae.exceptions.ForbiddenException;
import be.vinci.pae.exceptions.NotFoundException;
import be.vinci.pae.exceptions.UnauthorizedException;
import be.vinci.pae.utils.Config;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class MemberUCCImplTest {

  private final ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());

  private final String username = "rayan";
  private final String badUsername = "test";
  private final String passwd1 = "rayan123";
  private final String badPassword = "test";
  private final String statusValid = "valid";
  private final String statusDenied = "denied";
  private final String statusPending = "pending";
  private MemberUCC memberUCC;
  private MemberDAO mockMemberDAO;
  private AddressDAO mockAddressDAO;
  private Member mockMember;
  private DALService mockDalService;
  private AddressFactory addressFactory;
  private MemberFactory memberFactory;

  private MemberDTO memberPending1;
  private MemberDTO memberPending2;
  private MemberDTO memberValid1;

  private String pathImage;


  private MemberDTO getMemberNewMember() {
    // member to register
    AddressDTO newAddress = addressFactory.getAddressDTO();
    newAddress.setIdMember(0);
    newAddress.setUnitNumber("4");
    newAddress.setBuildingNumber("2");
    newAddress.setStreet("Rue de l'aérosol");
    newAddress.setPostcode("1234");
    newAddress.setCommune("Wolluwe");
    newAddress.setVersion(1);

    MemberDTO newMember = memberFactory.getMemberDTO();
    newMember.setMemberId(0);
    newMember.setUsername("MatthieuDu42");
    newMember.setLastname("Du bois");
    newMember.setFirstname("Matthieu");
    newMember.setPhone("0412345678");
    newMember.setPassword("matthieuLeChien");
    newMember.setAddress(newAddress);
    newMember.setVersion(1);

    return newMember;
  }

  @BeforeEach
  void initAll() {
    Config.load("test.properties");
    this.pathImage = Config.getProperty("ImagePath");
    this.memberUCC = locator.getService(MemberUCC.class);
    this.mockMemberDAO = locator.getService(MemberDAO.class);
    this.mockAddressDAO = locator.getService(AddressDAO.class);
    this.mockDalService = locator.getService(DALService.class);
    this.addressFactory = locator.getService(AddressFactory.class);
    this.memberFactory = locator.getService(MemberFactory.class);
    this.mockMember = Mockito.mock(MemberImpl.class);

    Mockito.when(mockMember.getUsername()).thenReturn(username);
    Mockito.when(mockMember.getPassword()).thenReturn(passwd1);
    Mockito.when(mockMember.getStatus()).thenReturn(statusValid);
    Mockito.when(mockMemberDAO.getOne(username)).thenReturn(mockMember);
    Mockito.when(mockMemberDAO.getOne(1)).thenReturn(mockMember);
    Mockito.when(mockMember.checkPassword(passwd1)).thenReturn(true);

    this.memberPending1 = getMemberNewMember();
    this.memberPending1.setStatus(statusPending);

    this.memberPending2 = getMemberNewMember();
    this.memberPending2.setStatus(statusPending);
    this.memberPending2.setMemberId(1);
    this.memberPending2.setUsername("Marc");

    this.memberValid1 = getMemberNewMember();
    this.memberValid1.setStatus(statusValid);
    this.memberValid1.setMemberId(2);
    this.memberValid1.setUsername("Michel");
    this.memberValid1.setVersion(5);

  }

  @DisplayName("Test login function with a good username, a good password, a valid member that "
      + "exists in the DB")
  @Test
  public void testLoginFunctionGoodUsernameGoodPasswordAndMemberValidAndInTheDB() {
    assertAll(
        () -> assertEquals(mockMember, memberUCC.login(username, passwd1)),
        () -> Mockito.verify(mockMember).checkPassword(passwd1),
        () -> Mockito.verify(mockMember, Mockito.times(3)).getStatus()
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

  @DisplayName("Test login function with prevented status member")
  @Test
  public void testLoginWithPreventedStatusMember() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(12);
    memberDTO.setUsername("marc");
    memberDTO.setStatus("prevented");
    Member member = (Member) memberDTO;
    memberDTO.setPassword(member.hashPassword(passwd1));

    Mockito.when(mockMemberDAO.getOne(memberDTO.getUsername())).thenReturn(memberDTO);
    Mockito.when(mockMemberDAO.updateOne(memberDTO)).thenReturn(memberDTO);

    MemberDTO memberDTOLogin = memberUCC.login(memberDTO.getUsername(), passwd1);
    assertAll(
        () -> assertEquals("valid", memberDTOLogin.getStatus()),
        () -> assertNull(memberDTOLogin.getPassword()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
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


  @DisplayName("Test updateProfilPicture with a non existent id for member")
  @Test
  public void testUpdateProfilPictureForNonExistentMember() {
    int idMember = 1000;
    Mockito.when(mockMemberDAO.getOne(idMember)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> memberUCC.updateProfilPicture(pathImage, idMember, 1)),
        () -> Mockito.verify(mockMemberDAO, Mockito.atLeastOnce()).getOne(idMember)
    );
  }

  @DisplayName("Test updateProfilPicture with success not already having a profil picture")
  @Test
  public void testUpdateProfilPictureSuccessNotHavingAProfilPicture() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);
    memberDTO.setImage(pathImage);
    memberDTO.setVersion(1);

    MemberDTO memberDTOWithNewProfilPic = memberFactory.getMemberDTO();
    memberDTOWithNewProfilPic.setMemberId(2);
    memberDTOWithNewProfilPic.setImage(pathImage + "test");
    memberDTOWithNewProfilPic.setVersion(2);

    Mockito.when(mockMemberDAO.getOne(memberDTO.getMemberId())).thenReturn(memberDTO);

    Mockito.when(mockMemberDAO.updateProfilPicture(pathImage + "test", memberDTO.getMemberId()))
        .thenReturn(memberDTOWithNewProfilPic);

    assertAll(
        () -> assertEquals(memberDTOWithNewProfilPic, memberUCC
            .updateProfilPicture(pathImage + "test", memberDTO.getMemberId(), 1)),
        () -> assertNotEquals(memberDTO.getImage(), memberUCC
            .updateProfilPicture(pathImage + "test", memberDTO.getMemberId(), 1).getImage()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test updateProfilPicture with success already having a profil picture")
  @Test
  public void testUpdateProfilPictureSuccessAlreadyHavingAProfilPicture() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);
    memberDTO.setVersion(1);

    MemberDTO memberDTOWithNewProfilPic = memberFactory.getMemberDTO();
    memberDTOWithNewProfilPic.setMemberId(2);
    memberDTOWithNewProfilPic.setImage(pathImage + "test");

    Mockito.when(mockMemberDAO.getOne(memberDTO.getMemberId())).thenReturn(memberDTO);

    Mockito.when(mockMemberDAO.updateProfilPicture(pathImage + "test", memberDTO.getMemberId()))
        .thenReturn(memberDTOWithNewProfilPic);

    MemberDTO memberToTest = memberUCC
        .updateProfilPicture(pathImage + "test", memberDTO.getMemberId(), 1);

    assertAll(
        () -> assertEquals(memberDTOWithNewProfilPic, memberToTest),
        () -> assertNotEquals(memberDTO.getImage(), memberToTest.getImage()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test updateProfilPicture with versions that don't match")
  @Test
  public void testUpdateProfilPictureWithDifferentsVersions() {
    int idMember = 1;
    Mockito.when(mockMemberDAO.getOne(idMember)).thenReturn(memberValid1);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> memberUCC.updateProfilPicture(pathImage, idMember, 3)),
        () -> Mockito.verify(mockMemberDAO, Mockito.atLeastOnce()).getOne(idMember)
    );
  }

  @DisplayName("Test updateProfilPicture with no version specified")
  @Test
  public void testUpdateProfilPictureWithoutVersion() {
    int idMember = 1;
    Mockito.when(mockMemberDAO.getOne(idMember)).thenReturn(memberValid1);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> memberUCC.updateProfilPicture(pathImage, idMember, null)),
        () -> Mockito.verify(mockMemberDAO, Mockito.atLeastOnce()).getOne(idMember)
    );
  }

  //  -----------------------------  REGISTER UCC  ----------------------------------------  //

  @DisplayName("Test register success")
  @Test
  public void testRegisterSuccess() {
    MemberDTO newMember = this.getMemberNewMember();

    MemberDTO memberFromCreateOneDao = this.getMemberNewMember();
    memberFromCreateOneDao.setMemberId(6);

    AddressDTO addressFromCreateOneDao = memberFromCreateOneDao.getAddress();
    addressFromCreateOneDao.setIdMember(6);

    Mockito.when(mockMemberDAO.getOne(newMember.getUsername())).thenReturn(null);
    Mockito.when(mockMemberDAO.createOneMember(newMember)).thenReturn(memberFromCreateOneDao);

    Mockito.when(mockAddressDAO.createOne(newMember.getAddress()))
        .thenReturn(addressFromCreateOneDao);

    MemberDTO memberRegistered = memberUCC.register(newMember);

    assertAll(
        () -> assertEquals(6, memberRegistered.getMemberId()),
        () -> assertEquals(memberRegistered.getMemberId(),
            memberRegistered.getAddress().getIdMember()),
        () -> assertEquals(memberFromCreateOneDao, memberRegistered),
        () -> assertEquals(addressFromCreateOneDao, memberRegistered.getAddress()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }


  @DisplayName("Test register with a member already existent")
  @Test
  public void testRegisterConflict() {
    MemberDTO newMember = this.getMemberNewMember();

    MemberDTO memberFromCreateOneDao = this.getMemberNewMember();
    memberFromCreateOneDao.setMemberId(6);

    Mockito.when(mockMemberDAO.getOne(newMember.getUsername())).thenReturn(memberFromCreateOneDao);

    assertAll(
        () -> assertThrows(ConflictException.class, () -> memberUCC.register(newMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test register with fields allowed to be empty (phone and unit number)")
  @Test
  public void testRegisterEmptyFields() {
    MemberDTO newMember = this.getMemberNewMember();
    newMember.setPhone("");
    newMember.getAddress().setUnitNumber("");

    MemberDTO memberFromCreateOneDao = this.getMemberNewMember();
    memberFromCreateOneDao.setMemberId(6);
    memberFromCreateOneDao.setPhone(null);

    AddressDTO addressFromCreateOneDao = memberFromCreateOneDao.getAddress();
    addressFromCreateOneDao.setIdMember(6);
    addressFromCreateOneDao.setUnitNumber(null);

    Mockito.when(mockMemberDAO.getOne(newMember.getUsername())).thenReturn(null);
    Mockito.when(mockMemberDAO.createOneMember(newMember)).thenReturn(memberFromCreateOneDao);

    Mockito.when(mockAddressDAO.createOne(newMember.getAddress()))
        .thenReturn(addressFromCreateOneDao);

    MemberDTO memberRegistered = memberUCC.register(newMember);

    assertAll(
        () -> assertEquals(6, memberRegistered.getMemberId()),
        () -> assertEquals(memberRegistered.getMemberId(),
            memberRegistered.getAddress().getIdMember()),
        () -> assertEquals(memberFromCreateOneDao, memberRegistered),
        () -> assertEquals(addressFromCreateOneDao, memberRegistered.getAddress()),
        () -> assertNull(newMember.getPhone()),
        () -> assertNull(newMember.getAddress().getUnitNumber()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test register with fields allowed to be null (phone and unit number)")
  @Test
  public void testRegisterNullFields() {
    MemberDTO newMember = this.getMemberNewMember();
    newMember.setPhone(null);
    newMember.getAddress().setUnitNumber(null);

    MemberDTO memberFromCreateOneDao = this.getMemberNewMember();
    memberFromCreateOneDao.setMemberId(6);
    memberFromCreateOneDao.setPhone(null);

    AddressDTO addressFromCreateOneDao = memberFromCreateOneDao.getAddress();
    addressFromCreateOneDao.setIdMember(6);
    addressFromCreateOneDao.setUnitNumber(null);

    Mockito.when(mockMemberDAO.getOne(newMember.getUsername())).thenReturn(null);
    Mockito.when(mockMemberDAO.createOneMember(newMember)).thenReturn(memberFromCreateOneDao);

    Mockito.when(mockAddressDAO.createOne(newMember.getAddress()))
        .thenReturn(addressFromCreateOneDao);

    MemberDTO memberRegistered = memberUCC.register(newMember);

    assertAll(
        () -> assertEquals(6, memberRegistered.getMemberId()),
        () -> assertEquals(memberRegistered.getMemberId(),
            memberRegistered.getAddress().getIdMember()),
        () -> assertEquals(memberFromCreateOneDao, memberRegistered),
        () -> assertEquals(addressFromCreateOneDao, memberRegistered.getAddress()),
        () -> assertNull(newMember.getPhone()),
        () -> assertNull(newMember.getAddress().getUnitNumber()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test register add a member failed")
  @Test
  public void testRegisterWithNullReceivedFromDAOWhenCreateAMember() {
    MemberDTO newMember = this.getMemberNewMember();

    Mockito.when(mockMemberDAO.getOne(newMember.getUsername())).thenReturn(null);
    Mockito.when(mockMemberDAO.createOneMember(newMember)).thenReturn(null);
    assertAll(
        () -> assertThrows(FatalException.class, () -> memberUCC.register(newMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test register add an address failed")
  @Test
  public void testRegisterWithNullReceivedFromDAOWhenCreateAnAddress() {
    MemberDTO newMember = this.getMemberNewMember();

    MemberDTO memberFromCreateOneDao = this.getMemberNewMember();
    memberFromCreateOneDao.setMemberId(6);

    Mockito.when(mockMemberDAO.getOne(newMember.getUsername())).thenReturn(null);
    Mockito.when(mockMemberDAO.createOneMember(newMember)).thenReturn(memberFromCreateOneDao);
    Mockito.when(mockAddressDAO.createOne(newMember.getAddress())).thenReturn(null);

    assertAll(
        () -> assertThrows(FatalException.class, () -> memberUCC.register(newMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  -----------------------------  SEARCH MEMBERS UCC  -----------------------------------  //


  @DisplayName("Test searchMembers with status and search empty")
  @Test
  public void testSearchMembersEmptySearchAndEmptyStatus() {
    List<MemberDTO> allMemberDTOList = List.of(memberPending1, memberPending2, memberValid1);

    Mockito.when(mockMemberDAO.getAll("", "")).thenReturn(allMemberDTOList);
    assertAll(
        () -> assertEquals(allMemberDTOList, memberUCC.searchMembers("", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );

  }

  @DisplayName("Test searchMembers with none member received from DAO")
  @Test
  public void testSearchMembersEmptyReturnListFromDAO() {
    List<MemberDTO> allDeniedMemberDTOList = List.of();

    Mockito.when(mockMemberDAO.getAll("", "denied")).thenReturn(allDeniedMemberDTOList);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC
            .searchMembers("", "denied")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );

  }

  @DisplayName("Test searchMembers with waiting status")
  @Test
  public void testSearchMembersWaitingStatus() {
    List<MemberDTO> allWaitingMemberDTOList = List.of(memberPending1, memberPending2);

    Mockito.when(mockMemberDAO.getAll("", "waiting"))
        .thenReturn(allWaitingMemberDTOList);
    assertAll(
        () -> assertEquals(allWaitingMemberDTOList, memberUCC
            .searchMembers("", "waiting")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );


  }

  @DisplayName("Test searchMembers with non status but a search param")
  @Test
  public void testSearchMembersWithSearchParamAndNoStatus() {
    List<MemberDTO> allMemberDTOListMatchingSearch = List.of(memberPending1, memberPending2);

    Mockito.when(mockMemberDAO.getAll("ma", ""))
        .thenReturn(allMemberDTOListMatchingSearch);
    assertAll(
        () -> assertEquals(allMemberDTOListMatchingSearch, memberUCC
            .searchMembers("ma", "")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test searchMembers with a valid status and a search param")
  @Test
  public void testSearchMembersWithSearchParamAndValidStatus() {
    List<MemberDTO> allValidMemberDTOMatchingSearch = List.of(memberValid1);

    Mockito.when(mockMemberDAO.getAll("mi", "valid"))
        .thenReturn(allValidMemberDTOMatchingSearch);
    assertAll(
        () -> assertEquals(allValidMemberDTOMatchingSearch, memberUCC
            .searchMembers("mi", "valid")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test searchMembers with a null list received from DAO")
  @Test
  public void testSearchMembersNullListReturnedFromDAO() {

    Mockito.when(mockMemberDAO.getAll("", "denied")).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC
            .searchMembers("", "denied")),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );

  }

  //  -----------------------------  UPDATE MEMBER UCC  -----------------------------------  //

  @DisplayName("Test updateMember success")
  @Test
  public void testUpdateMemberSuccess() {
    MemberDTO existentMemberInDB = getMemberNewMember();
    existentMemberInDB.setMemberId(5);

    MemberDTO existentMemberInDBUpdated = getMemberNewMember();
    existentMemberInDBUpdated.setMemberId(5);
    existentMemberInDBUpdated.setUsername("username");
    Mockito.when(mockAddressDAO.getAddressByMemberId(existentMemberInDB.getMemberId()))
        .thenReturn(existentMemberInDB.getAddress());
    Mockito.when(mockMemberDAO.getOne(existentMemberInDB.getMemberId()))
        .thenReturn(existentMemberInDB);
    Mockito.when(mockMemberDAO.getOne(existentMemberInDBUpdated.getUsername())).thenReturn(null);
    Mockito.when(mockMemberDAO.updateOne(existentMemberInDBUpdated))
        .thenReturn(existentMemberInDBUpdated);
    Mockito.when(mockAddressDAO.updateOne(existentMemberInDBUpdated.getAddress()))
        .thenReturn((existentMemberInDB.getAddress()));

    MemberDTO memberDTOToTest = memberUCC.updateMember(existentMemberInDBUpdated);

    assertAll(
        () -> assertEquals(existentMemberInDBUpdated, memberDTOToTest),
        () -> assertNotEquals(existentMemberInDB.getUsername(), memberDTOToTest.getUsername()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test updateMember success with null address field dto")
  @Test
  public void testUpdateMemberSuccessWithNullAddressFieldDTO() {
    MemberDTO existentMemberInDB = getMemberNewMember();
    existentMemberInDB.setMemberId(5);
    existentMemberInDB.setAddress(null);

    MemberDTO existentMemberInDBUpdated = getMemberNewMember();
    existentMemberInDBUpdated.setMemberId(5);
    existentMemberInDBUpdated.setUsername("lol");

    Mockito.when(mockMemberDAO.updateOne(existentMemberInDB)).thenReturn(existentMemberInDBUpdated);
    Mockito.when(mockAddressDAO.getAddressByMemberId(existentMemberInDB.getMemberId()))
        .thenReturn(existentMemberInDBUpdated.getAddress());

    MemberDTO memberUpdated = memberUCC.updateMember(existentMemberInDB);
    assertAll(
        () -> assertEquals(existentMemberInDB.getMemberId(),
            memberUpdated.getMemberId()),
        () -> assertNotEquals(existentMemberInDB.getUsername(),
            memberUpdated.getUsername()),
        () -> assertEquals(existentMemberInDBUpdated.getAddress(),
            memberUpdated.getAddress()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test updateMember with a member having his fields by default")
  @Test
  public void testUpdateMemberWithEmptyFieldsMember() {
    MemberDTO existentMember = memberFactory.getMemberDTO();
    existentMember.setUsername("usernameExistent");
    existentMember.setAddress(getMemberNewMember().getAddress());
    existentMember.getAddress().setIdMember(2);
    existentMember.setMemberId(2);
    AddressDTO existentAddress = addressFactory.getAddressDTO();
    existentAddress.setVersion(1);
    Mockito.when(mockMemberDAO.getOne(existentMember.getMemberId())).thenReturn(existentMember);

    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC
            .updateMember(existentMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateMember with a non existent member")
  @Test
  public void testUpdateMemberNonExistentInDB() {
    MemberDTO nonExistentMemberInDB = getMemberNewMember();
    Mockito.when(mockMemberDAO.updateOne(nonExistentMemberInDB)).thenReturn(null);
    Mockito.when(mockMemberDAO.getOne(nonExistentMemberInDB.getMemberId())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC
            .updateMember(nonExistentMemberInDB)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateMember with a non existent member with null address field")
  @Test
  public void testUpdateMemberNonExistentInDBWithNullAddressFieldDTO() {
    MemberDTO nonExistentMemberInDBWithoutAddress = getMemberNewMember();
    nonExistentMemberInDBWithoutAddress.setAddress(null);
    Mockito.when(mockMemberDAO.getOne(nonExistentMemberInDBWithoutAddress.getMemberId()))
        .thenReturn(null);
    Mockito.when(
            mockAddressDAO.getAddressByMemberId(nonExistentMemberInDBWithoutAddress.getMemberId()))
        .thenReturn(null);

    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC
            .updateMember(nonExistentMemberInDBWithoutAddress)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateMember with differents versions")
  @Test
  public void testUpdateMemberWithDifferentsVersions() {
    MemberDTO memberValidInDB = getMemberNewMember();
    memberValidInDB.setMemberId(2);
    memberValidInDB.setVersion(7);
    Mockito.when(mockMemberDAO.getOne(memberValid1.getMemberId()))
        .thenReturn(memberValidInDB);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC
            .updateMember(memberValid1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateMember with username already taken")
  @Test
  public void testUpdateMemberWithUsernameAlreadyTaken() {
    MemberDTO memberValidInDB = getMemberNewMember();
    memberValidInDB.setMemberId(250);
    memberValidInDB.setVersion(5);
    memberValidInDB.setUsername(memberValid1.getUsername());
    Mockito.when(mockMemberDAO.getOne(memberValid1.getUsername())).thenReturn(memberValidInDB);
    Mockito.when(mockMemberDAO.getOne(memberValid1.getMemberId()))
        .thenReturn(memberValid1);
    assertAll(
        () -> assertThrows(ConflictException.class, () -> memberUCC
            .updateMember(memberValid1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateMember with no address")
  @Test
  public void testUpdateMemberWithNoAddress() {
    MemberDTO memberValidInDB = getMemberNewMember();
    memberValidInDB.setMemberId(2);
    memberValidInDB.setVersion(5);
    memberValidInDB.setUsername(memberValid1.getUsername());
    Mockito.when(mockMemberDAO.getOne(memberValid1.getMemberId()))
        .thenReturn(memberValidInDB);
    Mockito.when(mockMemberDAO.getOne(memberValid1.getUsername())).thenReturn(memberValid1);
    Mockito.when(mockAddressDAO.getAddressByMemberId(memberValid1.getMemberId()))
        .thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC
            .updateMember(memberValid1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test updateMember with differents versions for address")
  @Test
  public void testUpdateMemberWithDifferentsVersionsForAddress() {
    MemberDTO memberValidInDB = getMemberNewMember();
    memberValidInDB.setMemberId(2);
    memberValidInDB.setVersion(5);

    AddressDTO addressInDB = addressFactory.getAddressDTO();
    addressInDB.setIdMember(0);
    addressInDB.setCommune("Texas");
    addressInDB.setVersion(2);

    memberValidInDB.setUsername(memberValid1.getUsername());
    Mockito.when(mockMemberDAO.getOne(memberValid1.getMemberId()))
        .thenReturn(memberValidInDB);
    Mockito.when(mockMemberDAO.getOne(memberValid1.getUsername())).thenReturn(memberValid1);
    Mockito.when(mockAddressDAO.getAddressByMemberId(memberValid1.getMemberId()))
        .thenReturn(addressInDB);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC
            .updateMember(memberValid1)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  -----------------------------  GET MEMBER UCC  -----------------------------------  //

  @DisplayName("Test getMember success")
  @Test
  public void testGetMemberSuccess() {
    MemberDTO existentMemberInDB = getMemberNewMember();
    existentMemberInDB.setMemberId(5);
    Mockito.when(mockMemberDAO.getOne(5)).thenReturn(existentMemberInDB);
    assertAll(
        () -> assertEquals(existentMemberInDB, memberUCC.getMember(5)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  @DisplayName("Test getMember with non existent member id")
  @Test
  public void testGetMemberNonExistentMemberId() {
    Mockito.when(mockMemberDAO.getOne(0)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC.getMember(0)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  -----------------------------  GET PICTURE UCC  -----------------------------------  //

  @DisplayName("Test getPicture with image")
  @Test
  public void testGetPictureWithImage() {
    MemberDTO memberExistent = getMemberNewMember();
    memberExistent.setMemberId(3);
    memberExistent.setImage(pathImage);

    Mockito.when(mockMemberDAO.getOne(memberExistent.getMemberId())).thenReturn(memberExistent);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> memberUCC.getPicture(memberExistent.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test getPicture without image")
  @Test
  public void testGetPictureWithoutImage() {
    MemberDTO memberExistent = getMemberNewMember();
    memberExistent.setMemberId(3);

    Mockito.when(mockMemberDAO.getOne(memberExistent.getMemberId())).thenReturn(memberExistent);
    assertAll(
        () -> assertThrows(NullPointerException.class,
            () -> memberUCC.getPicture(memberExistent.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }


  @DisplayName("Test getPicture of non existent member")
  @Test
  public void testGetPictureOfNonExistentMember() {
    MemberDTO memberNonExistent = getMemberNewMember();

    Mockito.when(mockMemberDAO.getOne(memberNonExistent.getMemberId())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> memberUCC.getPicture(memberNonExistent.getMemberId())),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  //  -----------------------------  GET PICTURE UCC  -----------------------------------  //

  @DisplayName("Test preventMember with non existent member")
  @Test
  public void testPreventMemberWithNonExistentMember() {
    MemberDTO memberNonExistent = getMemberNewMember();

    Mockito.when(mockMemberDAO.getOne(memberNonExistent.getMemberId())).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> memberUCC.preventMember(memberNonExistent)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test preventMember with not same member version")
  @Test
  public void testPreventMemberWithNotSameMemberVersion() {
    MemberDTO memberExistent = getMemberNewMember();
    memberExistent.setVersion(12);
    MemberDTO memberFromGetOne = getMemberNewMember();
    memberFromGetOne.setVersion(13);

    Mockito.when(mockMemberDAO.getOne(memberExistent.getMemberId())).thenReturn(memberFromGetOne);
    assertAll(
        () -> assertThrows(ForbiddenException.class,
            () -> memberUCC.preventMember(memberExistent)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test preventMember success")
  @Test
  public void testPreventMemberSuccess() {
    MemberDTO memberExistent = getMemberNewMember();
    memberExistent.setVersion(13);
    memberExistent.setMemberId(10);
    memberExistent.getAddress().setIdMember(10);

    MemberDTO memberFromGetOne = getMemberNewMember();
    memberFromGetOne.setVersion(13);
    memberFromGetOne.getAddress().setIdMember(memberExistent.getMemberId());
    memberFromGetOne.setMemberId(memberExistent.getMemberId());
    memberFromGetOne.setStatus("prevented");

    Mockito.when(mockMemberDAO.getOne(memberExistent.getMemberId())).thenReturn(memberFromGetOne);
    Mockito.when(mockMemberDAO.updateOne(memberExistent)).thenReturn(memberFromGetOne);
    Mockito.when(mockAddressDAO.getAddressByMemberId(memberFromGetOne.getMemberId()))
        .thenReturn(memberFromGetOne.getAddress());

    MemberDTO memberDTOUpdated = memberUCC.preventMember(memberExistent);

    assertAll(
        () -> assertEquals("prevented", memberExistent.getStatus()),
        () -> assertEquals("prevented", memberDTOUpdated.getStatus()),
        () -> assertEquals(memberFromGetOne, memberDTOUpdated),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

}