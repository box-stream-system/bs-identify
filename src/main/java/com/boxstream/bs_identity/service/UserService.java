package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.request.UserUpdateRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.InvalidUUIDFormatException;
import com.boxstream.bs_identity.exception.UserNotFoundException;
import com.boxstream.bs_identity.exception.UsernameExistsException;
import com.boxstream.bs_identity.mapper.UserMapper;
import com.boxstream.bs_identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;


    public User createNewUser(UserCreationRequest newUser) {
        if (userRepository.existsByUsername(newUser.getUsername())) throw new UsernameExistsException();
        return userRepository.save(userMapper.toUser(newUser));
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).collect(Collectors.toList());
    }

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
        userMapper.updateUser(user, userUpdateRequest);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
