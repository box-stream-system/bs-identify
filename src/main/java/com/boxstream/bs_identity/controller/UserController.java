package com.boxstream.bs_identity.controller;

import com.boxstream.bs_identity.dto.ApiResponse;
import com.boxstream.bs_identity.dto.request.UserCreationRequest;
import com.boxstream.bs_identity.dto.request.UserUpdateRequest;
import com.boxstream.bs_identity.dto.response.UserResponse;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.service.UserService;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("users")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);
    UserService userService;


    @PostMapping("/add")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest user) {
        ApiResponse<UserResponse> response = new ApiResponse<>();
        response.setCode(1);
        response.setMessage("User create successfully!");
        response.setData(userService.createNewUser(user));
        return response;
    }

    @GetMapping("/all")
    public List<UserResponse> getAllUsers() {

        var authenticationInfo = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Authentication info: {}", authenticationInfo.getAuthorities());

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

    @GetMapping("/myinfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getCurrentUserInfo())
                .code(200)
                .build();
    }
}
