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
    newAddress.setCountry("Belgique");

    MemberDTO newMember = memberFactory.getMemberDTO();
    newMember.setMemberId(0);
    newMember.setUsername("MatthieuDu42");
    newMember.setLastname("Du bois");
    newMember.setFirstname("Matthieu");
    newMember.setPhone("0412345678");
    newMember.setPassword("matthieuLeChien");
    newMember.setAddress(newAddress);

    return newMember;
  }

  @BeforeEach
  void initAll() {
    Config.load("test.properties");
    this.pathImage = Config.getProperty("ImagePath");
    ServiceLocator locator = ServiceLocatorUtilities.bind(new TestBinder());
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


  @DisplayName("Test updateProfilPicture with a non existent id for member")
  @Test
  public void testUpdateProfilPictureForNonExistentMember() {
    int idMember = 1000;
    Mockito.when(mockMemberDAO.getOne(idMember)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class,
            () -> memberUCC.updateProfilPicture(pathImage, idMember)),
        () -> Mockito.verify(mockMemberDAO).getOne(idMember)
    );
  }

  @DisplayName("Test updateProfilPicture avec success")
  @Test
  public void testUpdateProfilPictureSuccess() {
    MemberDTO memberDTO = memberFactory.getMemberDTO();
    memberDTO.setMemberId(2);
    memberDTO.setImage(pathImage);

    MemberDTO memberDTOWithNewProfilPic = memberFactory.getMemberDTO();
    memberDTOWithNewProfilPic.setMemberId(2);
    memberDTOWithNewProfilPic.setImage(pathImage + "test");

    Mockito.when(mockMemberDAO.getOne(memberDTO.getMemberId())).thenReturn(memberDTO);

    Mockito.when(mockMemberDAO.updateProfilPicture(pathImage + "test", memberDTO.getMemberId()))
        .thenReturn(memberDTOWithNewProfilPic);

    assertAll(
        () -> assertEquals(memberDTOWithNewProfilPic, memberUCC
            .updateProfilPicture(pathImage + "test", memberDTO.getMemberId())),
        () -> assertNotEquals(memberDTO.getImage(), memberUCC
            .updateProfilPicture(pathImage + "test", memberDTO.getMemberId()).getImage()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  -----------------------------  REGISTER UCC  ----------------------------------------  //

  @DisplayName("Test inscription tout va bien")
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


  @DisplayName("Test inscription avec membre déjà existant")
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

  @DisplayName("Test inscription avec champs pouvant êtres vides (tel (member) et boite (address))")
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

  @DisplayName("Test inscription avec champs pouvant êtres null (tel (member) et boite (address))")
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

  @DisplayName("Test inscription avec ajout du membre échoué")
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

  @DisplayName("Test inscription avec ajout de l'adresse échouée")
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


  @DisplayName("Test recherche de membre avec status et recherche vide")
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

  @DisplayName("Test recherche avec aucun membre retourné par le DAO")
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

  @DisplayName("Test recherche avec status waiting")
  @Test
  public void testSearchWaitingStatus() {
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

  @DisplayName("Test recherche avec aucun status mais une recherche en param")
  @Test
  public void testSearchWithSearchParamAndNoStatus() {
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

  @DisplayName("Test recherche avec un valid status et une recherche en param")
  @Test
  public void testSearchWithSearchParamAndValidStatus() {
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

  @DisplayName("Test recherche avec une liste de membres ayant la valeur null retourné par le DAO")
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


  @DisplayName("Test update member avec un membre ayant ses attributs par défaut")
  @Test
  public void testUpdateMemberWithEmptyFieldsMember() {
    MemberDTO nonExistentMember = memberFactory.getMemberDTO();
    Mockito.when(mockMemberDAO.updateOne(nonExistentMember)).thenReturn(null);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC
            .updateMember(nonExistentMember)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test update member avec un membre qui n'est pas dans la DB")
  @Test
  public void testUpdateMemberNonExistentInDB() {
    MemberDTO nonExistentMemberInDB = getMemberNewMember();
    Mockito.when(mockMemberDAO.updateOne(nonExistentMemberInDB)).thenReturn(null);
    assertAll(
        () -> assertThrows(ForbiddenException.class, () -> memberUCC
            .updateMember(nonExistentMemberInDB)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }

  @DisplayName("Test update member succès")
  @Test
  public void testUpdateMemberSuccess() {
    MemberDTO existentMemberInDB = getMemberNewMember();
    existentMemberInDB.setMemberId(5);

    MemberDTO existentMemberInDBUpdated = getMemberNewMember();
    existentMemberInDBUpdated.setMemberId(5);
    existentMemberInDBUpdated.setUsername("lol");

    Mockito.when(mockMemberDAO.updateOne(existentMemberInDB)).thenReturn(existentMemberInDBUpdated);
    assertAll(
        () -> assertEquals(existentMemberInDB.getMemberId(),
            memberUCC.updateMember(existentMemberInDB).getMemberId()),
        () -> assertNotEquals(existentMemberInDB.getUsername(),
            memberUCC.updateMember(existentMemberInDB).getUsername()),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).commitTransaction()
    );
  }

  //  -----------------------------  GET MEMBER UCC  -----------------------------------  //

  @DisplayName("Test get member succès")
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

  @DisplayName("Test get member with non existent member id")
  @Test
  public void testGetMemberNonExistentMemberId() {
    Mockito.when(mockMemberDAO.getOne(0)).thenReturn(null);
    assertAll(
        () -> assertThrows(NotFoundException.class, () -> memberUCC.getMember(0)),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).startTransaction(),
        () -> Mockito.verify(mockDalService, Mockito.atLeastOnce()).rollBackTransaction()
    );
  }
}