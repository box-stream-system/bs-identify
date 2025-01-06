package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.AuthenticationRequest;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.UserNotFoundException;
import com.boxstream.bs_identity.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;

    public boolean authenticate(AuthenticationRequest authenticationRequest) {
        User user =
                userRepository.findByUsername(authenticationRequest.getUsername())
                        .orElseThrow(UserNotFoundException::new);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        return passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
    }
}
