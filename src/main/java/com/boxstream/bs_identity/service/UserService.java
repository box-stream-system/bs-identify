package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid UUID format.");
        }
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));
    }
}
