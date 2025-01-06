package com.boxstream.bs_identity.controller;

import com.boxstream.bs_identity.dto.ApiResponse;
import com.boxstream.bs_identity.dto.request.AuthenticationRequest;
import com.boxstream.bs_identity.dto.response.AuthenticationResponse;
import com.boxstream.bs_identity.service.AuthenticationService;
import com.boxstream.bs_identity.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("auth")
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("login")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        boolean rs = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .data(AuthenticationResponse.builder().success(rs).build())
                .build();
    }

}
