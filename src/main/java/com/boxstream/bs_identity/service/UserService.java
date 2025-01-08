package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.request.UserUpdateRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.GlobalExceptionHandler;
import com.boxstream.bs_identity.exception.InvalidUUIDFormatException;
import com.boxstream.bs_identity.exception.UserNotFoundException;
import com.boxstream.bs_identity.exception.UsernameExistsException;
import com.boxstream.bs_identity.mapper.UserMapper;
import com.boxstream.bs_identity.repository.RoleRepository;
import com.boxstream.bs_identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;


    public User createNewUser(UserCreationRequest newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) throw new UsernameExistsException();
        User user = userMapper.toUser(newUser);

        // HASHING PASSWORD
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        logger.info("Creating a new user: " + user.getUsername());
        return userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

    @PostAuthorize("returnObject.username == authentication.name")  // only current user can get their user info
    public UserResponse getByUserId(String id) {
        if (!isValidUUID(id)) {
            throw new InvalidUUIDFormatException();
        }
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new));
    }


    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    public UserResponse updateUserFew(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userMapper.updateUserFew(user, userUpdateRequest);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        // map userUpdateRequest to User
        userMapper.updateUser(user, userUpdateRequest);

        // hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // build user roles
        // map list string roles from request to set roles in Entity
        var roles = roleRepository.findAllById(userUpdateRequest.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public UserResponse getCurrentUserInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return userMapper.toUserResponse(user);
    }
}
