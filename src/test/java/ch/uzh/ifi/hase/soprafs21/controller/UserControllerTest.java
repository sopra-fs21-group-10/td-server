package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostInDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserUserIdTokenPatchDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
 class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
     void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.ONLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
     void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestUser");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostInDTO userPostDTO = new UserPostInDTO();
        userPostDTO.setPassword("TestUser");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", is(user.getToken())));
    }

    @Test
     void createUser_invalidInput_throw() throws Exception {
        // username already exists
        // thee test does not make much sense, as wi test if it sends what we mock
        // the important test par should be in service(check if user exists)

        // given
        Exception thrownByService=new ResponseStatusException(HttpStatus.CONFLICT,
                "The username provided is not unique. Therefore, the user could not be created!");

        UserPostInDTO userPostDTO = new UserPostInDTO();
        userPostDTO.setPassword("TestUser2");
        userPostDTO.setUsername("testUsername");//same as other

        given(userService.createUser(Mockito.any())).willThrow(thrownByService);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }

    @Test
    void login_validInput_returnToken() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestUser");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);

        UserPostInDTO userPostInDTO = new UserPostInDTO();
        userPostInDTO.setPassword("TestUser");
        userPostInDTO.setUsername("testUsername");

        given(userService.userIn(Mockito.any())).willReturn(user);
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostInDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(user.getToken())));
    }

    @Test
    void login_invalidInput_throw() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestUser");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus(UserStatus.OFFLINE);

        UserPostInDTO userPostInDTO = new UserPostInDTO();
        userPostInDTO.setPassword("TestUser");
        userPostInDTO.setUsername("testUsername");

        Exception thrownByService=new ResponseStatusException(HttpStatus.NOT_FOUND, "Not a valid username");

        given(userService.userIn(Mockito.any())).willThrow(thrownByService);
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder putRequest = put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostInDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResponseStatusException));
    }

    @Test
    void changeData_validInput_dataChanged() throws Exception {
        // integration because weatherAPI
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestUser");
        user.setUsername("testUsername");
        user.setToken("12a3");
        user.setStatus(UserStatus.OFFLINE);

        UserUserIdTokenPatchDTO userUserIdTokenPatchDTO = new UserUserIdTokenPatchDTO();
        userUserIdTokenPatchDTO.setPassword("TestUser2");
        userUserIdTokenPatchDTO.setUsername("testUsername2");
        userUserIdTokenPatchDTO.setLocation("London");

        given(userRepository.getOne(Mockito.any())).willReturn(user);
        Mockito.doNothing().when(userService).editProfile(Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder patchRequest = patch("/users/profiles/{token}",user.getId(),user.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userUserIdTokenPatchDTO));

        // then
        mockMvc.perform(patchRequest)
                .andExpect(status().isOk());
    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}