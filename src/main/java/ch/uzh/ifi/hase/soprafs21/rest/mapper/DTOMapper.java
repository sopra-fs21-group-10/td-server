package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically transform/map the internal representation
 * of an entity (e.g., the User) to the external/API representation (e.g., UserGetDTO for getting, UserPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @SuppressWarnings("Unmapped target properties")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(target = "location", ignore = true)//to get rid of warning
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "status", ignore = true)
    User convertUserPostInDTOtoEntity(UserPostInDTO userPostDTO);//warning is irrelevant

    @Mapping(source = "token", target = "token")
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "token", target = "token")
    @Mapping(source = "userId", target = "userId")
    UserPostDTO convertEntityToUserPostDTO(User user);

    @Mapping(source = "userId", target = "userId")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "lobbyId", target = "lobbyId")
    LobbyIdDTO convertEntityToLobbyIdDTO(Long lobbyId);

    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "lobbyOwner", target = "lobbyOwner")
    LobbiesGetDTO convertEntityToLobbiesGetDTO(Long lobbyId, String lobbyOwner);
}
