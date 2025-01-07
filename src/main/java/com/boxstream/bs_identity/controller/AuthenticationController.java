package com.boxstream.bs_identity.controller;

import com.boxstream.bs_identity.dto.ApiResponse;
import com.boxstream.bs_identity.dto.request.AuthenticationRequest;
import com.boxstream.bs_identity.dto.request.IntrospectRequest;
import com.boxstream.bs_identity.dto.response.AuthenticationResponse;
import com.boxstream.bs_identity.dto.response.IntrospectResponse;
import com.boxstream.bs_identity.service.AuthenticationService;
import com.boxstream.bs_identity.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("auth")
public class AuthenticationController {

    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws KeyLengthException {
        var result = authenticationService.authenticateReturnJWT(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .code(200)
                .data(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .code(200)
                .data(result)
                .build();
    }
}
