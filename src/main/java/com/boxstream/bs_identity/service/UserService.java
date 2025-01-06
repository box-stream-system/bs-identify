package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.request.UserUpdateRequest;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.InvalidUUIDFormatException;
import com.boxstream.bs_identity.exception.UserNotFoundException;
import com.boxstream.bs_identity.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createNewUser(UserCreationRequest newUser) {


        if (userRepository.existsByUsername(newUser.getUsername()))
            throw new RuntimeException("User exists.");


        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getByUserId(String id) {
        if (!isValidUUID(id)) {
            throw new InvalidUUIDFormatException();
        }
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }


    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public User updateUser(String userId, UserUpdateRequest user) {
        User u = getByUserId(userId);
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setEmail(user.getEmail());
        u.setPassword(user.getPassword());
        u.setMiddleName(user.getMiddleName());
        u.setDateOfBirth(user.getDateOfBirth());
        u.setPhone(user.getPhone());
        return userRepository.save(u);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}
