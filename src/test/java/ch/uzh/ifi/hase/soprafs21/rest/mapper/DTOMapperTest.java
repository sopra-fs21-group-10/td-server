package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostInDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation works.
 */
 class DTOMapperTest {
    @Test
     void testCreateUser_fromUserPostDTO_toUser_success() {
        // create UserPostDTO
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setToken("1");


        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

        // check content
        assertEquals(userPostDTO.getToken(), user.getToken());
    }

    @Test
    void testGetPostInDTO_fromUser_toUser_success() {
        // create UserPostDTO
        UserPostInDTO userPostInDTO = new UserPostInDTO();
        userPostInDTO.setUsername("Nick");
        userPostInDTO.setPassword("123");

        // MAP -> Create user
        User user = DTOMapper.INSTANCE.convertUserPostInDTOtoEntity(userPostInDTO);

        // check content
        assertEquals(userPostInDTO.getPassword(), user.getPassword());
        assertEquals(userPostInDTO.getUsername(), user.getUsername());
    }

    @Test
     void testGetUser_fromUser_toUserGetDTO_success() {
        // create User
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        // MAP -> Create UserGetDTO
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

        // check content
        assertEquals(user.getUserId(), userGetDTO.getUserId());
        assertEquals(user.getUsername(), userGetDTO.getUsername());
        assertEquals(user.getStatus(), userGetDTO.getStatus());
    }

    @Test
    void testGetUser_fromUser_toUserPostDTO_success() {
        // create User
        User user = new User();
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);
        user.setToken("1");

        // MAP -> Create userPostDTO
        UserPostDTO userPostDTO = DTOMapper.INSTANCE.convertEntityToUserPostDTO(user);

        // check content
        assertEquals(user.getToken(), userPostDTO.getToken());
    }
}
