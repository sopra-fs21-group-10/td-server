package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Lobby;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.LobbiesGetDTO;
import ch.uzh.ifi.hase.soprafs21.service.LobbyService;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.List;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the LobbyController i.e. GET/POST/PATCH/PUT request without actually sending them over the network.
 * This tests if the LobbyController works.
 */
@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LobbyService lobbyService;

    @MockBean
    private UserService userService;
/*
    @Test
    private List<LobbiesGetDTO> getAllLobbies() throws Exception{
        Lobby testLobby = new Lobby();
        List<Lobby> allUsers = Collections.singletonList(testLobby);

        //mock the lobbyservice
        Mockito.when(lobbyService.getLobbies()).thenReturn(allUsers);

        //mock the request
        MockHttpServletRequestBuilder getAllRequest = get("/lobbies").contentType(MediaType.APPLICATION_JSON);

        //do the request
        mockMvc.perform((getAllRequest))
                .andExpect(status().isOk())

    }*/
   /* @Test
    void getAllTournamentsPositive() throws Exception{

        List<Tournament> dummyList2 = new ArrayList<>();
        dummyList2.add(testTournament1);
        dummyList2.add(testTournament2);

        given(tournamentService.getAllTournaments()).willReturn(dummyList2);

        // mock the request
        MockHttpServletRequestBuilder getAllRequest = get("/tournaments")
                .contentType(MediaType.APPLICATION_JSON);

        // do the request
        mockMvc.perform(getAllRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].winner.participantID", is(DTOMapper.INSTANCE.convertEntityToParticipantGetDTO(testTournament1.getWinner()).getParticipantID()))) // representation of Participant object is different in json
                .andExpect(jsonPath("$[0].tournamentName", is(testTournament1.getTournamentName())))
                .andExpect(jsonPath("$[0].location", is(testTournament1.getLocation())))
                .andExpect(jsonPath("$[0].tournamentState", is(testTournament1.getTournamentState())))
                .andExpect(jsonPath("$[0].startTime", is(testTournament1.getStartTime())))
                //.andExpect(jsonPath("$[0].gameDuration", is(testTournament1.getGameDuration()))) // confusion with floats?
                //.andExpect(jsonPath("$[0].breakDuration", is(testTournament1.getBreakDuration()))) // dito
                .andExpect(jsonPath("$[0].tournamentCode", is(testTournament1.getTournamentCode())))
                .andExpect(jsonPath("$[0].amountOfPlayers", is(testTournament1.getAmountOfPlayers())))
                .andExpect(jsonPath("$[0].numberTables", is(testTournament1.getNumberTables())))
                .andExpect(jsonPath("$[0].informationBox", is(testTournament1.getInformationBox())));
        //.andExpect(jsonPath("$[0].leaderboard", is(testTournament1.getLeaderboard()))) // no representation of leaderboard object in json format
        //.andExpect(jsonPath("$[0].bracket", is(testTournament1.getBracket()))) // dito
        //.andExpect(jsonPath("$[0].activePlayers", is(testTournament1.getActivePlayers()))); // dito
    }

    @Test
    private void template(){

    }*/
}
