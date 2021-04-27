package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.entity.Board;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.repository.BoardRepository;
import ch.uzh.ifi.hase.soprafs21.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs21.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameGoldDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GameMoveDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.GamePostDTO;
import ch.uzh.ifi.hase.soprafs21.service.GameService;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GameController.class)
 class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;
    @MockBean
    private UserService userService;


    @MockBean
    private BoardRepository boardRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private GameRepository gameRepository;

    @Test
    void given1Player_whenCreateGame_thenReturnJsonArray() throws Exception {
        // given
        GamePostDTO gamePostDTO = new GamePostDTO();
        gamePostDTO.setPlayer1Id(1L);

        GameGetDTO gameGetDTO= new GameGetDTO();

        // this mocks the Service
        given(gameService.createGame(1L, null)).willReturn(1L);
        given(gameService.returnGameInformation(1L)).willReturn(gameGetDTO);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    void givenGame_whenGetGame_thenReturnJsonArray() throws Exception {
        // given
        GameGetDTO gameGetDTO= new GameGetDTO();

        // this mocks the Service

        given(gameService.returnGameInformation(1L)).willReturn(gameGetDTO);

        // when
        MockHttpServletRequestBuilder postRequest = get("/games/"+ 1)
                .contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(postRequest).andExpect(status().isOk());
    }

    @Test
    void givenPlayerTowerCoordinates_whenPlaceTower_thenReturnJsonArray() throws Exception {
        // given
        GameMoveDTO gameMoveDTO = new GameMoveDTO();
        int[] coordinates = new int[]{1,1};
        gameMoveDTO.setCoordinates(coordinates);
        gameMoveDTO.setEntity("FireTower1");

        GameGoldDTO gameGoldDTO= new GameGoldDTO();
        gameGoldDTO.setGold(54);

        User dummyUser = new User();
        dummyUser.setToken("token");
        Board dummyBoard = new Board();

        // this mocks the Service/repo
        given(userRepository.findByToken(Mockito.any())).willReturn(dummyUser);
        given(boardRepository.findByOwner(Mockito.any())).willReturn(dummyBoard);

        given(gameService.placeTower(Mockito.any(),Mockito.any(),Mockito.any())).willReturn(54);

        // when
        MockHttpServletRequestBuilder postRequest = post("/games/towers/"+dummyUser.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gameMoveDTO));

        // then
        mockMvc.perform(postRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$.gold", is(54)));
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
