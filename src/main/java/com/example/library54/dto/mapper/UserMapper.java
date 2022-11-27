package com.example.library54.dto.mapper;

import com.example.library54.domain.User;
import com.example.library54.dto.UserDTO;
import com.example.library54.dto.request.CreateUserRequest;
import com.example.library54.dto.request.UpdateRequest;
import com.example.library54.dto.response.RLResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);
    RLResponse userToRLResponse(User user);
    User createUserRequestToUser(CreateUserRequest createUserRequest);
    User updateRequestToUser(UpdateRequest updateRequest);
    List<UserDTO> map(List<User> user);
}
