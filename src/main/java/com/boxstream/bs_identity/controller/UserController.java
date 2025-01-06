package com.boxstream.bs_identity.controller;

import com.boxstream.bs_identity.dto.ApiResponse;
import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.request.UserUpdateRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("users")
public class UserController {
    UserService userService;


    @PostMapping("/add")
    public ApiResponse<User> createUser(@RequestBody @Valid UserCreationRequest user) {
        ApiResponse<User> response = new ApiResponse<>();
        response.setCode(1);
        response.setMessage("User create successfully!");
        response.setData(userService.createNewUser(user));
        return response;
    }

    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.getByUserId(id));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("userId") String userId,
                                           @RequestBody UserUpdateRequest user) {
        return ResponseEntity.ok(userService.updateUser(userId, user));
    }

    @PatchMapping("/update/v1/{userId}")
    public ResponseEntity<UserResponse> patchUpdateUser(@PathVariable("userId") String userId,
                                           @RequestBody UserUpdateRequest user) {
        return ResponseEntity.ok(userService.updateUserFew(userId, user));
    }

    @DeleteMapping("/del/{userId}")
    public String deleteUser(@PathVariable("userId") String userId) {
        userService.deleteUser(userId);
        return "User deleted";
    }
}
