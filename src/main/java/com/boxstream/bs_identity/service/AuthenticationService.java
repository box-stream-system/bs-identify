package com.boxstream.bs_identity.service;

import com.boxstream.bs_identity.dto.request.AuthenticationRequest;
import com.boxstream.bs_identity.dto.request.IntrospectRequest;
import com.boxstream.bs_identity.dto.response.AuthenticationResponse;
import com.boxstream.bs_identity.dto.response.IntrospectResponse;
import com.boxstream.bs_identity.entity.User;
import com.boxstream.bs_identity.exception.AuthenticationFailedException;
import com.boxstream.bs_identity.exception.GlobalExceptionHandler;
import com.boxstream.bs_identity.exception.UserNotFoundException;
import com.boxstream.bs_identity.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    UserRepository userRepository;

    @NonFinal // to not inject this to contructor
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws AuthenticationFailedException, JOSEException, ParseException {
        var token = request.getToken();

        // get verify the signature
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes(StandardCharsets.UTF_8));

        // get signature in token
        SignedJWT signedJWT = SignedJWT.parse(token);

        // get expired time of token
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        // compare
        var verified = signedJWT.verify(verifier);

        // must match the secret key and not expired
        return IntrospectResponse.builder()
                .valid(verified && expiration.after(new Date()))
                .build();
    }

    public AuthenticationResponse authenticateReturnJWT(AuthenticationRequest authenticationRequest) throws KeyLengthException {
        User user =
                userRepository.findByUsername(authenticationRequest.getUsername())
                        .orElseThrow(UserNotFoundException::new);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if(!authenticated) throw new AuthenticationFailedException();

        // GENERATE TOKEN
        var token = gennerateTokenUsingNimbus(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();
    }

    private String gennerateTokenUsingNimbus(User user) throws KeyLengthException {
        // build header
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        // build claims - Data in JWT body is called Claims
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) // subject is stand for the logged in user
                .issuer("tienphuckx.com") // the domain that provide this JWT
                .issueTime(new Date())
                .expirationTime(new Date( // the time that token will be expired
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("email", user.getEmail())
                .claim("phone", user.getPhone())
                .claim("scope", buildScopeRoles(user))
                .build();

        // Final Payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload); // param are header and payload

        // Sign the JWT
        // MACSigner() is a symatric (Ma Hoa Doi Xung) - the same key - like: AES, DES
        // Options: RSA, ....
        // MACSigner() need a secret key 32 bytes

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes(StandardCharsets.UTF_8)));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            logger.error("Could not generate JWT token", e);
            throw new RuntimeException(e);
        }

    }

    String buildScopeRoles(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        user.getRoles().forEach(role -> {
            stringJoiner.add("ROLE_" + role.getName());
            if(!CollectionUtils.isEmpty(role.getPermissions()))
                role.getPermissions().forEach(permission -> {
                    stringJoiner.add(permission.getName());
                });

        });
        return stringJoiner.toString();
    }
}
