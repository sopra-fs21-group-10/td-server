package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setPassword("testName");
        testUser.setUsername("testUsername");
        testUser.setToken("token");
        testUser.setStatus(UserStatus.OFFLINE);

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getPassword(), createdUser.getPassword());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    void createUser_duplicateUsername_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    void userIn_validInputs_success() {
        // given -> a first user has already been created
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        User LoggedUser = userService.userIn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertEquals(UserStatus.ONLINE, LoggedUser.getStatus());
    }

    @Test
    void userIn_noName_throwsException() {
        // given -> a first user has already been created
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());


        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);

        // then -> login
        assertThrows(ResponseStatusException.class, () -> userService.userIn(testUser));
    }

    @Test
    void userIn_wrongPassword_throwsException() {
        // given -> a first user has already been created
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());

        User user2 =new User();
        user2.setId(testUser.getId());
        user2.setPassword("testNameNotTheSame");
        user2.setUsername(testUser.getUsername());
        user2.setStatus(testUser.getStatus());

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> login with wrong password
        assertThrows(ResponseStatusException.class, () -> userService.userIn(user2));
    }

    @Test
    void userLogout_validInput_success() {
        // given -> a first user has already been created
        testUser.setStatus(UserStatus.ONLINE);
        assertEquals(UserStatus.ONLINE, testUser.getStatus());

        // when -> setup additional mocks for UserRepository
        userService.userLogout(testUser);

        // then -> user logged out
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
    }

    @Test
    void userLogout_nullUser_throw() {
        // given -> a first user has already been created
        testUser.setStatus(UserStatus.ONLINE);
        assertEquals(UserStatus.ONLINE, testUser.getStatus());

        // when -> setup additional mocks for UserRepository

        // then -> logout not existing user
        assertThrows(ResponseStatusException.class, () -> userService.userLogout(null));
    }

    @Test
    void editProfile_validInputsNotAll_success() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        User createdUser = userService.createUser(testUser);//tested above

        //when
        userService.editProfile(createdUser,null,"password123", null);

        //then
        assertEquals("testUsername",createdUser.getUsername());
        assertEquals("password123",createdUser.getPassword());
        assertEquals("Zurich",createdUser.getLocation());
    }

    @Test
    void editProfile_invalidInputs_throw() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        User createdUser = userService.createUser(testUser);//tested above

        //when

        //then
        //no user
        assertThrows(ResponseStatusException.class, () ->
                userService.editProfile(null, null,"password123", null));
    }

    @Test
    void editProfile_dublicateName_throw() {
        // given
        assertNull(userRepository.findByUsername("testUsername"));

        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");

        User user2 = new User();

        User createdUser = userService.createUser(testUser);//tested above

        //when
        given(userRepository.findByUsername(Mockito.any())).willReturn(user2);


        //then
        //no user
        assertThrows(ResponseStatusException.class, () ->
                userService.editProfile(createdUser,null,"password123", null));

    }
}
