package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.service.GameService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;


@WebMvcTest(GameController.class)
 class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

//    @Test
//    void given1Player_whenCreateGame_thenReturnJsonArray() throws Exception {
//        // given
//        GamePostDTO gamePostDTO = new GamePostDTO();
//        gamePostDTO.setPlayer1Id(1L);
//
//        GameGetDTO gameGetDTO= new GameGetDTO();
//
//        // this mocks the Service
//        given(gameService.createGame(Mockito.any(), Mockito.any())).willReturn(gameGetDTO);
//
//        // when
//        MockHttpServletRequestBuilder postRequest = post("/games")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(gamePostDTO));
//
//        // then
//        mockMvc.perform(postRequest).andExpect(status().isCreated());
//    }

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
