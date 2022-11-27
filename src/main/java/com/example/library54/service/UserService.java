package com.example.library54.service;

import com.example.library54.domain.Role;
import com.example.library54.domain.User;
import com.example.library54.domain.enums.RoleType;
import com.example.library54.dto.UserDTO;
import com.example.library54.dto.mapper.UserMapper;
import com.example.library54.dto.request.CreateUserRequest;
import com.example.library54.dto.request.RegisterRequest;
import com.example.library54.dto.request.UpdateRequest;
import com.example.library54.exception.BadRequestException;
import com.example.library54.exception.ConflictException;
import com.example.library54.exception.ResourceNotFoundException;
import com.example.library54.exception.message.ErrorMessage;
import com.example.library54.repository.LoanRepository;
import com.example.library54.repository.RoleRepository;
import com.example.library54.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private LoanRepository loanRepository;

    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST, registerRequest.getEmail()));
        }

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        String resetPassword = registerRequest.getLastName();

        Role role = roleRepository.findByName(RoleType.ROLE_MEMBER).
                orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessage.ROLE_NOT_FOUND_MESSAGE,
                                RoleType.ROLE_MEMBER.name())));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        role.setRoleCount(role.getRoleCount() + 1L);

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setAddress(registerRequest.getAddress());
        user.setPhone(registerRequest.getPhone());
        user.setBirthDate(registerRequest.getBirthDate());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setScore(0);
        user.setCreateDate(LocalDateTime.now());
        user.setResetPasswordCode(resetPassword);
        user.setBuiltIn(false);

        user.setRoles(roles);

        roleRepository.save(role);

        userRepository.save(user);
        return user;
    }


    public User saveUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST, createUserRequest.getEmail()));
        }

        Role role = roleRepository.findByName(RoleType.ROLE_MEMBER).
                orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ErrorMessage.ROLE_NOT_FOUND_MESSAGE,
                                RoleType.ROLE_MEMBER.name())));

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        role.setRoleCount(role.getRoleCount() + 1L);

        String encodedPassword = passwordEncoder.encode(createUserRequest.getPassword());
        String resetPassword = createUserRequest.getLastName();

        User user = userMapper.createUserRequestToUser(createUserRequest);
        user.setResetPasswordCode(resetPassword);

        user.setPassword(encodedPassword);
        roleRepository.save(role);
        userRepository.save(user);
        return user;
    }

    @Transactional
    public Page<UserDTO> getUserLoanPage(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        Page<UserDTO> dtoPage = users.map(user -> userMapper.userToUserDTO(user));

        return dtoPage;
    }

    public Page getUsersPage(String name ,Pageable pageable) {
        if (name!=null){
            return userRepository.findUsersWithQuery(name, pageable);
        }else {
            Page<User> users = userRepository.findAll(pageable);
            Page dtoPage = users.map(user -> userMapper.userToRLResponse(user));
            return dtoPage;
        }

    }

    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));

        return userMapper.userToUserDTO(user);
    }


    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.map(users);
    }


    public User updateUserByAdmin(Long id, UpdateRequest updateRequest) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));

        if (user.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }
        User userUpdated = userMapper.updateRequestToUser(updateRequest);
        userUpdated.setId(user.getId());
        userUpdated.setResetPasswordCode(updateRequest.getLastName());

        userRepository.save(userUpdated);

        return userUpdated;
    }


    public User memberUserUpdate(Long id, UpdateRequest updateRequest) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));

        if (user.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        }


        if (user.getRoles().toString().contains(RoleType.ROLE_MEMBER.name())) {
            User userUpdated = userMapper.updateRequestToUser(updateRequest);
            userUpdated.setId(user.getId());
            userUpdated.setResetPasswordCode(updateRequest.getLastName());

            userRepository.save(userUpdated);


        }
        return user;
    }


    @Transactional
    public UserDTO deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));

        boolean exists = loanRepository.existsByUserId(user);
        if (exists) {
            throw new BadRequestException(ErrorMessage.USER_USED_BY_LOAN_MESSAGE);
        }

        if (user.getBuiltIn()) {
            throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
        } else {
            Role role = roleRepository.findByName(RoleType.ROLE_MEMBER).
                    orElseThrow(() -> new ResourceNotFoundException(
                            String.format(ErrorMessage.ROLE_NOT_FOUND_MESSAGE,
                                    RoleType.ROLE_MEMBER.name())));


            role.setRoleCount(role.getRoleCount() - 1L);
            roleRepository.save(role);
        }


        userRepository.deleteById(id);
        return userMapper.userToUserDTO(user);

    }
}
