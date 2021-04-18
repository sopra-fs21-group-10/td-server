package ch.uzh.ifi.hase.soprafs21.rest.mapper;

import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.*;
import org.mapstruct.*;
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

    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    User convertUserPostInDTOtoEntity(UserPostInDTO userPostDTO);//warning is irrelevant

    @Mapping(source = "token", target = "token")
    User convertUserPostDTOtoEntity(UserPostDTO userPostDTO);

    @Mapping(source = "token", target = "token")
    UserPostDTO convertEntitytoUserPostDTO(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "status", target = "status")
    UserGetDTO convertEntityToUserGetDTO(User user);

    @Mapping(source = "id", target = "id")
    LobbyPostDTO convertEntitytoLobbyPostDTO(Long id);

    @Mapping(source = "lobbyId", target = "lobbyId")
    @Mapping(source = "ownerName", target = "ownerName")
    LobbiesGetDTO convertEntitytoLobbyPostDTO(Long lobbyId, String ownerName);


}
