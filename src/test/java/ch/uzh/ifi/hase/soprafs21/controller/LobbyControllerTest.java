package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbyPutAndPatchDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.TokenDTO;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the LobbyController i.e. GET/POST/PATCH/PUT request without actually sending them over the network.
 * This tests if the LobbyController works.
 */
@WebMvcTest(LobbyController.class)
class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private UserService userService;

    @MockBean
    LobbyRepository lobbyRepository;

    private User testuser1;
    private User testuser2;
    private Lobby testLobbyFull;
    private Lobby testLobbyNotFull;

    @BeforeEach
    void setup(){
        testuser1 = new User();
        testuser1.setUsername("testuser1");
        testuser1.setPassword("password1");
        testuser1.setToken("token1");
        testuser1.setStatus(UserStatus.OFFLINE);

        testuser2 = new User();
        testuser2.setUsername("testuser2");
        testuser2.setPassword("password2");
        testuser2.setToken("token2");
        testuser2.setStatus(UserStatus.OFFLINE);

        testLobbyFull = new Lobby();
        testLobbyFull.setLobbyId(1L);
        testLobbyFull.setOwner(testuser1);
        testLobbyFull.setPlayer2(testuser2);

        testLobbyNotFull = new Lobby();
        testLobbyNotFull.setLobbyId(2L);
        testLobbyNotFull.setOwner(testuser1);
    }

    @Test
    void getAllLobbies() throws Exception{
        List<Lobby> allUsers = Collections.singletonList(testLobbyNotFull);

        //mock the lobbyservice
        Mockito.when(lobbyService.getLobbies()).thenReturn(allUsers);

        //mock the request
        MockHttpServletRequestBuilder getAllRequest = get("/lobbies").contentType(MediaType.APPLICATION_JSON);


        // perform request
        mockMvc.perform(getAllRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].lobbyOwner", is(testLobbyNotFull.getOwner().getUsername())))
                .andExpect(jsonPath("$[0].lobbyId", is(testLobbyNotFull.getLobbyId().intValue())));

    }

    @Test
    void getLobbyByIdRequestLobbyFull() throws Exception{
        //mock Service
        Mockito.when(lobbyService.findLobbyById(Mockito.anyLong())).thenReturn(testLobbyFull);

        //mock request
        MockHttpServletRequestBuilder getByIdRequest = get("/lobbies/1").contentType(MediaType.APPLICATION_JSON);

        // perform request
        mockMvc.perform(getByIdRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyOwner", is(testLobbyFull.getOwner().getUsername())))
                .andExpect(jsonPath("$.player2Status", is(testLobbyFull.getLobbyStatus().toString())))
                .andExpect(jsonPath("$.player2", is(testLobbyFull.getPlayer2().getUsername())));




    }

    @Test
    void getLobbyByIdRequestLobbyNotFull() throws Exception{
        //mock Service
        Mockito.when(lobbyService.findLobbyById(Mockito.anyLong())).thenReturn(testLobbyNotFull);

        //mock request
        MockHttpServletRequestBuilder getByIdRequest = get("/lobbies/1").contentType(MediaType.APPLICATION_JSON);

        // perform request
        mockMvc.perform(getByIdRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyOwner", is(testLobbyNotFull.getOwner().getUsername())))
                .andExpect(jsonPath("$.player2Status", is(testLobbyNotFull.getLobbyStatus().toString())))
                .andExpect(jsonPath("$.player2", is("")));




    }
    @Test
    void createLobby() throws Exception{
        //mock Service
        Mockito.when(userService.checkIfUserExistByToken(Mockito.any())).thenReturn(testuser1);
        Mockito.when(lobbyService.createLobby(testuser1)).thenReturn(testLobbyNotFull.getLobbyId());
        TokenDTO tokenDTO = new TokenDTO();
        tokenDTO.setToken(testuser1.getToken());
        //mock request
        MockHttpServletRequestBuilder postCreateLobbyRequest = post("/lobbies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(tokenDTO));

        // perform request
        mockMvc.perform(postCreateLobbyRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.lobbyId", is(testLobbyNotFull.getLobbyId().intValue())));


    }
    @Test
    void patchlobby() throws Exception{
        //mock Service
        Mockito.when(userService.checkIfUserExistByToken(Mockito.any())).thenReturn(testuser2);
        Mockito.when(lobbyService.addUserToLobby(1L,testuser2)).thenReturn(testLobbyFull);
        Mockito.when(lobbyService.findLobbyById(Mockito.any())).thenReturn(testLobbyFull);
        LobbyPutAndPatchDTO lobbyPutAndPatchDTO = new LobbyPutAndPatchDTO();
        lobbyPutAndPatchDTO.setLobbyId(1L);
        lobbyPutAndPatchDTO.setToken("sometoken");
        //mock request
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletRequestBuilder postCreateLobbyRequest = patch("/lobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutAndPatchDTO));


        //perform request
        // perform request
        mockMvc.perform(postCreateLobbyRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyOwner", is(testLobbyFull.getOwner().getUsername())))
                .andExpect(jsonPath("$.player2Status", is(testLobbyFull.getLobbyStatus().toString())))
                .andExpect(jsonPath("$.player2", is(testLobbyFull.getPlayer2().getUsername())));



    }

    @Test
    void putDeleteOwner() throws Exception{
        //mock Service
        Mockito.when(userService.checkIfUserExistByToken(Mockito.any())).thenReturn(testuser2);
        Mockito.when(lobbyService.addUserToLobby(1L,testuser2)).thenReturn(testLobbyFull);
        Mockito.when(lobbyService.findLobbyById(Mockito.any())).thenReturn(testLobbyFull);
        LobbyPutAndPatchDTO lobbyPutAndPatchDTO = new LobbyPutAndPatchDTO();
        lobbyPutAndPatchDTO.setLobbyId(1L);
        lobbyPutAndPatchDTO.setToken("sometoken");
        //mock request
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletRequestBuilder patchLobbyRequest = patch("/lobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutAndPatchDTO));


        //perform request
        // perform request
        mockMvc.perform(patchLobbyRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyOwner", is(testLobbyFull.getOwner().getUsername())))
                .andExpect(jsonPath("$.player2Status", is(testLobbyFull.getLobbyStatus().toString())))
                .andExpect(jsonPath("$.player2", is(testLobbyFull.getPlayer2().getUsername())));



    }
    void putRequest() throws Exception{
        //mock Service
        Mockito.when(userService.checkIfUserExistByToken(Mockito.any())).thenReturn(Mockito.any());
        Mockito.when(lobbyService.deleteUserFromLobby(Mockito.any(),Mockito.any())).thenReturn(Mockito.any());
        LobbyPutAndPatchDTO lobbyPutAndPatchDTO = new LobbyPutAndPatchDTO();
        lobbyPutAndPatchDTO.setLobbyId(1L);
        lobbyPutAndPatchDTO.setToken("sometoken");
        //mock request
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletRequestBuilder putLobbyRequest = patch("/lobbies/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(lobbyPutAndPatchDTO));


        //perform request
        // perform request
        mockMvc.perform(putLobbyRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$.lobbyOwner", is(testLobbyFull.getOwner().getUsername())))
                .andExpect(jsonPath("$.player2Status", is(testLobbyFull.getLobbyStatus().toString())))
                .andExpect(jsonPath("$.player2", is(testLobbyFull.getPlayer2().getUsername())));



    }
    // helpers
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
   }
}
